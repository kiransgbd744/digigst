package com.ey.advisory.app.gstr3b;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Slf4j
@Service("Gstr1vs3bRequestStatusDaoImpl")
public class Gstr1vs3bRequestStatusDaoImpl
		implements Gstr1vs3bRequestStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	private CommonUtility commonUtility;

	@Override
	public List<Gstr1Vs3BRequestStatusDto> getRequestIdSummaryData(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		List<Gstr1Vs3BRequestStatusDto> respList = new ArrayList<>();

		Long entityId = reqDto.getEntityId();

		String taxPeriodFrom = reqDto.getTaxPeriodFrom();
		String taxPeriodTo = reqDto.getTaxPeriodTo();
		List<String> initiationByUserId = reqDto.getInitiationByUserId();
		
		String reconStatus = reqDto.getReconStatus();

		if (reconStatus != null && !reconStatus.isEmpty()) {
			if (reconStatus.equalsIgnoreCase("REPORT GENERATION FAILED")) {
				reconStatus = "REPORT_GENERATION_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATED")) {
				reconStatus = "REPORT_GENERATED";
			} else if (reconStatus.equalsIgnoreCase("RECON COMPLETED")) {
				reconStatus = "RECON_COMPLETED";
			} else if (reconStatus.equalsIgnoreCase("RECON FAILED")) {
				reconStatus = "RECON_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATION COMPLETED")) {
				reconStatus = "REPORT_GENERATION_COMPLETED";
			} else if (reconStatus.equalsIgnoreCase("RECON REQUESTED")) {
				reconStatus = "RECON_REQUESTED";
			}
		}

		try {
			boolean isusernamereq = commonUtility
					 .getAnsForQueMultipleUserAccessToAsyncReports(entityId);
			String initiatedby="";
			if(isusernamereq){
				initiatedby=" AND CF.CREATED_BY = :userName ";
			}
			
			int returnPeriodFrom = 0;
			if (!Strings.isNullOrEmpty(taxPeriodFrom)) {
				returnPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
				reqDto.setReturnPeriodFrom(returnPeriodFrom);
			}
			int returnPeriodTo = 0;
			if (!Strings.isNullOrEmpty(taxPeriodTo)) {
				returnPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
				reqDto.setReturnPeriodTo(returnPeriodTo);
			}
			
			String condtion = queryCondition(reqDto, isusernamereq, initiatedby);
			String queryString = createQuery(condtion);

			Query q = entityManager.createNativeQuery(queryString);
			
			if (reqDto.getEntityId() != null) {
				q.setParameter("entityId", entityId);
			}
			
			if (returnPeriodFrom != 0 && returnPeriodTo != 0) {
				q.setParameter("returnPeriodFrom", returnPeriodFrom);
				q.setParameter("returnPeriodTo", returnPeriodTo);
			}else if (returnPeriodFrom != 0 && returnPeriodTo == 0) {
				q.setParameter("returnPeriodFrom", returnPeriodFrom);
			}
			
			if (!isusernamereq) {
				if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
					q.setParameter("initiationByUserId", initiationByUserId);
				} 
			}

			if(isusernamereq){
				q.setParameter("userName", userName);
			}
			

			if (reqDto.getReconStatus() != null
					&& !reqDto.getReconStatus().isEmpty()) {
				q.setParameter("reconStatus", reconStatus);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data {} %s", reqDto);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			respList = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format("Error Occured while executing query",
					ex);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return respList;

	}

	// To-Do query change
	private String createQuery(String condition) {

		String query = " select CF.RECON_REPORT_CONFIG_ID, count(*) AS GSTIN_COUNT, "
				+ " GSTIN.GSTIN, CF.FROM_RET_PERIOD, CF.TO_RET_PERIOD, CF.CREATED_DATE, "
				+ " CF.CREATED_BY, CF.COMPLETED_ON,CF.STATUS,CF.FILE_PATH "
				+ " FROM TBL_RECON_GSTR1_3B_CONFIG CF INNER JOIN "
				+ " GSTR1_VS_3B_STATUS GSTIN ON  "
				+ " CF.RECON_REPORT_CONFIG_ID = GSTIN.RECON_REPORT_CONFIG_ID "
				+ " where CF.ENTITY_ID =:entityId "
				+   condition
				+ " GROUP BY CF.RECON_REPORT_CONFIG_ID, "
				+ " CF.FROM_RET_PERIOD, "
				+ " CF.TO_RET_PERIOD, CF.CREATED_DATE, CF.CREATED_BY, "
				+ " CF.COMPLETED_ON, CF.STATUS, CF.FILE_PATH, GSTIN.GSTIN ";

		return query;
	}

	private String queryCondition(Gstr2InitiateReconReqDto reqDto, boolean isusernamereq, 
			String initiatedby) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Gstr1vs3bRequestStatusDaoImpl.queryCondition() ";
			LOGGER.debug(msg);
		}
		
		StringBuilder queryBuilder = new StringBuilder();
		
		/*
		if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() != 0) {
			queryBuilder.append(" AND RC.FROM_RETURN_PERIOD BETWEEN "
					+ ":returnPeriodFrom AND :returnPeriodTo");
			
			queryBuilder.append(" AND RC.TO_RETURN_PERIOD BETWEEN "
					+ ":returnPeriodFrom AND :returnPeriodTo");
		}*/
		
		
		if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() != 0) {
			queryBuilder.append("AND ( CF.FROM_RET_PERIOD >= :returnPeriodFrom"
					+ " AND CF.TO_RET_PERIOD <= :returnPeriodTo )");
		}else if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() == 0) {
			queryBuilder.append("AND CF.FROM_RET_PERIOD >= :returnPeriodFrom");
		}
		
		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				queryBuilder.append(" AND CF.CREATED_BY IN (:initiationByUserId) ");
			} 
		}
		
		if(isusernamereq){
			queryBuilder.append(initiatedby);
		}
		
		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			queryBuilder.append(" AND CF.STATUS =:reconStatus ");
		}
		
		return queryBuilder.toString();

	}

	private Gstr1Vs3BRequestStatusDto convert(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr1Vs3BRequestStatusDto object";
			LOGGER.debug(str);
		}

		Gstr1Vs3BRequestStatusDto convert = new Gstr1Vs3BRequestStatusDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		convert.setRequestId(requestId);
		
		BigInteger bi = GenUtil.getBigInteger(arr[1]);
		Integer gstnCount = bi.intValue();
		convert.setGstinCount(gstnCount);
		
		GstinDto gstinDto = new GstinDto((String) arr[2]);
		convert.setGstins(new ArrayList<GstinDto>(Arrays.asList(gstinDto)));
		
		if (arr[3] != null && arr[4] != null) {
			Integer toTaxPeriod = ((Integer) arr[4]).intValue();
			convert.setToTaxPeriod(toTaxPeriod);

			Integer fromTaxPeriod = ((Integer) arr[3]).intValue();
			convert.setFromTaxPeriod(fromTaxPeriod);
		} else {
			convert.setToTaxPeriod(null);
			convert.setFromTaxPeriod(null);
		}
		
		Timestamp date = (Timestamp) arr[5];
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			String dateTime = EYDateUtil.toISTDateTimeFromUTC(dt).toString();
			dateTime = String.format("%-20s", dateTime).replace(' ', '0');
			String Date = dateTime.substring(0, 10);
			String Time = dateTime.substring(11, 19);
			String updatedDateTime = (Date + " " + Time);
			convert.setInitiatedOn(updatedDateTime);
		}
		
		convert.setInitiatedBy((String) arr[6]);

		date = (Timestamp) arr[7];
		if (date != null) {
			String cdateTime = date != null ? EYDateUtil
					.toISTDateTimeFromUTC(date.toLocalDateTime()).toString()
					: null;
			String cDate = cdateTime.substring(0, 10);
			String cTime = cdateTime.substring(11, 19);
			String cupdatedDateTime = (cDate + " " + cTime);
			convert.setCompletionOn(cupdatedDateTime);
		}
		
		convert.setStatus((String) arr[8]);
		
		String filePath = arr[9] !=null ?arr[9].toString():null;
		if(filePath !=null)
		{
			convert.setRptDownldPath(filePath);
			convert.setDownld(true);
		}
		else
		{
			convert.setRptDownldPath(null);
		}

		return convert;
	}

	@Override
	public List<BigInteger> getRequestIds(String userName, Long entityId)
			throws AppException {
		try {
			String queryString = "SELECT RRC.RECON_REPORT_CONFIG_ID AS REQUEST_ID "
					+ "FROM TBL_RECON_GSTR1_3B_CONFIG RRC "
					+ " WHERE RRC.ENTITY_ID =:entityId ORDER BY 1 DESC";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data of RequestIds "
						+ ", entityId " + entityId + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<BigInteger> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "EWB3WayInitiateReconReportRequestStatusDaoImpl.getRequestIds");
		}
	}

}
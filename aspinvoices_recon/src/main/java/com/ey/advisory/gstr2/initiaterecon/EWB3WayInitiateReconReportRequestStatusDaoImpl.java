/**
 * 
 */
package com.ey.advisory.gstr2.initiaterecon;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("EWB3WayInitiateReconReportRequestStatusDaoImpl")
public class EWB3WayInitiateReconReportRequestStatusDaoImpl
		implements EWB3WayInitiateReconReportRequestStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;


	private EWB3WayInitiateReconReportRequestStatusDto convert(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " EWB3WayInitiateReconReportRequestStatusDto object";
			LOGGER.debug(str);
		}

		EWB3WayInitiateReconReportRequestStatusDto convert = new EWB3WayInitiateReconReportRequestStatusDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		convert.setRequestId(requestId);
		Timestamp date = (Timestamp) arr[1];
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			String dateTime = EYDateUtil.toISTDateTimeFromUTC(dt).toString();
			dateTime = String.format("%-20s", dateTime).replace(' ', '0');
			String Date = dateTime.substring(0, 10);
			String Time = dateTime.substring(11, 19);
			String updatedDateTime = (Date + " " + Time);
			convert.setInitiatedOn(updatedDateTime);
		}

		convert.setInitiatedBy((String) arr[2]);

		date = (Timestamp) arr[4];
		if (date != null) {
			String cdateTime = date != null ? EYDateUtil
					.toISTDateTimeFromUTC(date.toLocalDateTime()).toString()
					: null;
			String cDate = cdateTime.substring(0, 10);
			String cTime = cdateTime.substring(11, 19);
			String cupdatedDateTime = (cDate + " " + cTime);
			convert.setCompletionOn(cupdatedDateTime);
		}
		convert.setStatus((String) arr[5]);
		BigInteger bi = GenUtil.getBigInteger(arr[6]);
		Integer gstnCount = bi.intValue();
		convert.setGstinCount(gstnCount);
		GstinDto gstinDto = new GstinDto((String) arr[7]);
		convert.setGstins(new ArrayList<GstinDto>(Arrays.asList(gstinDto)));
		
		
		if (arr[3] != null && arr[8] != null) {
			Integer toTaxPeriod = ((Integer) arr[3]).intValue();
			convert.setToTaxPeriod(toTaxPeriod);

			Integer fromTaxPeriod = ((Integer) arr[8]).intValue();
			convert.setFromTaxPeriod(fromTaxPeriod);
		} else {
			convert.setToTaxPeriod(null);
			convert.setFromTaxPeriod(null);
		}

		date = (Timestamp) arr[9];
		String toDate = date != null ? date.toString() : null;
		convert.setToDocDate(toDate);

		date = (Timestamp) arr[10];
		String fromDate = date != null ? date.toString() : null;
		convert.setFromDocDate(fromDate);
		
		// Req type 
		
		convert.setReqType((String) arr[11]);
		convert.setReconType((String) arr[12]);
		
		return convert;
	}

	

	@Override
	public List<BigInteger> getRequestIds(String userName, Long entityId)
			throws AppException {
		try {
			String queryString = "SELECT RRC.RECON_CONFIG_ID AS REQUEST_ID "
					+ "FROM TBL_3WAY_RECON_CONFIG RRC "
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

	@Override
	public List<EWB3WayInitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		List<EWB3WayInitiateReconReportRequestStatusDto> respList = null;
		Long entityId = reqDto.getEntityId();
		List<Long> requestId = reqDto.getRequestId();
	// check 
		String reconType = reqDto.getReconType();
		
		String initiationDateFrom = reqDto.getInitiationDateFrom();
		String initiationDateTo = reqDto.getInitiationDateTo();
		List<String> initiationByUserId = reqDto.getInitiationByUserId();
		
		List<Long> entityIds = new ArrayList<Long>();
		entityIds.add(entityId);
		
		boolean restrictionFlag = true;

		String reconStatus = reqDto.getReconStatus();

		if (reconStatus != null && !reconStatus.isEmpty()) {
			if (reconStatus.equalsIgnoreCase("REPORT GENERATION FAILED")) {
				reconStatus = "REPORT_GENERATION_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATED")) {
				reconStatus = "REPORT_GENERATED";
			}
		}

		try {
			String condtion = queryCondition(reqDto, restrictionFlag, reconType);
			String queryString = createQuery(condtion);

			Query q = entityManager.createNativeQuery(queryString);
			if (reqDto.getEntityId() != null) {
				q.setParameter("entityId", entityId);
			}
			if (reqDto.getInitiationDateFrom() != null
					&& !reqDto.getInitiationDateFrom().isEmpty()) {
				q.setParameter("initiationDateFrom", initiationDateFrom);
			}
			if (reqDto.getInitiationDateTo() != null
					&& !reqDto.getInitiationDateTo().isEmpty()) {
				q.setParameter("initiationDateTo", initiationDateTo);
			}
			if (reqDto.getReconType() != null
					&& !reqDto.getReconType().isEmpty()) {
				q.setParameter("reconType", reconType);
			}
			if (!CollectionUtils.isEmpty(reqDto.getRequestId())) {
				q.setParameter("requestId", requestId);
			}
			
			q.setParameter("userName", userName);
			
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				q.setParameter("initiationByUserId", initiationByUserId);
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

	private String createQuery(String condtion) {

				
		String query =	" SELECT RRC.RECON_CONFIG_ID AS REQUEST_ID, "
				+ " RRC.CREATED_DATE, RRC.CREATED_BY, RRC.TO_RET_PERIOD, "
				+ " RRC.COMPLETED_ON, "
				+ " RRC.STATUS, COUNT(*) "
				+ " AS GSTIN_COUNT, RRGD.GSTIN, RRC.FROM_RET_PERIOD, "
				+ " RRC.TO_DOC_DATE, RRC.FROM_DOC_DATE, RRC.REQUEST_TYPE,"
				+ " RRC.RECON_TYPE FROM " + "TBL_3WAY_RECON_CONFIG RRC "
				+ " INNER JOIN TBL_3WAY_RECON_GSTIN RRGD "
				+ " ON RRGD.RECON_CONFIG_ID = RRC.RECON_CONFIG_ID "
				+ " WHERE (RRC.CREATED_BY = :userName "
				+ " OR RRC.CREATED_BY = 'SYSTEM') "
				+ " AND RRC.ENTITY_ID =:entityId" + condtion +" GROUP BY "
				+ " RRC.RECON_CONFIG_ID, RRC.CREATED_DATE, "
				+ " RRC.CREATED_BY, RRC.COMPLETED_ON, RRC.STATUS,"
				+ " RRC.TO_RET_PERIOD,RRC.FROM_RET_PERIOD, RRGD.GSTIN, "
				+ " RRC.TO_DOC_DATE, RRC.FROM_DOC_DATE, RRC.REQUEST_TYPE, "
				+ " RECON_TYPE ";

		return query;
	}

	private String queryCondition(Gstr2InitiateReconReqDto reqDto, 
			boolean restrictionFlag, String reconType) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin EWB3WayInitiateReconReportRequestStatusDaoImpl .queryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		if (reqDto.getInitiationDateFrom() != null
				&& (!reqDto.getInitiationDateFrom().isEmpty())) {
			condition1.append(
					" AND TO_VARCHAR(RRC.CREATED_DATE,'YYYY-MM-DD') BETWEEN :initiationDateFrom ");
		}

		if (reqDto.getInitiationDateTo() != null
				&& (!reqDto.getInitiationDateTo().isEmpty())) {
			condition1.append(" AND :initiationDateTo ");
		}

		if (!CollectionUtils.isEmpty(reqDto.getRequestId())) {
			condition1
					.append(" AND RRC.RECON_CONFIG_ID IN (:requestId) ");
		}

		if (reqDto.getReconType() != null
				&& (!reqDto.getReconType().isEmpty())) {
			condition1.append(" AND RRC.RECON_TYPE = :reconType ");
		}
// check
		/*if (restrictionFlag) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				condition1.append(" AND RRC.CREATED_BY = :userName ");
			} else {
				condition1.append(" AND RRC.CREATED_BY = :userName "
						+ " OR (RRC.CREATED_BY = 'SYSTEM' AND RRC.ENTITY_ID =:entityId ) ");
			}
		} else if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
			condition1.append(" AND RRC.CREATED_BY IN (:initiationByUserId) ");
		}*/
		
		if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
			condition1.append(" AND RRC.CREATED_BY IN (:initiationByUserId) ");
		} else {
			condition1.append(" AND RRC.CREATED_BY = :userName "
					+ " OR (RRC.CREATED_BY = 'SYSTEM' AND RRC.ENTITY_ID =:entityId ) ");
		}

		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			condition1.append(" AND RRC.STATUS =:reconStatus ");
		}

		return condition1.toString();
	}

}

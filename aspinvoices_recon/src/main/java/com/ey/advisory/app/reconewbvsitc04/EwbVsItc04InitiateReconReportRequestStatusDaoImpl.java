/**
 * 
 */
package com.ey.advisory.app.reconewbvsitc04;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@Component("EwbVsItc04InitiateReconReportRequestStatusDaoImpl")
public class EwbVsItc04InitiateReconReportRequestStatusDaoImpl
		implements EwbVsItc04InitiateReconReportRequestStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;
	
	@Autowired
	private CommonUtility commonUtility;


	private EwbVsItc04InitiateReconReportRequestStatusDto convert(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " EwbVsItc04InitiateReconReportRequestStatusDto object";
			LOGGER.debug(str);
		}
		
		EwbVsItc04InitiateReconReportRequestStatusDto convert = new EwbVsItc04InitiateReconReportRequestStatusDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		convert.setRequestId(requestId);
	
		BigInteger count = arr[1] != null ? GenUtil.getBigInteger(arr[1]) : null;
		convert.setGstinCount(count);
	
		String fy = arr[2] != null ? (String) arr[2] : null;
		convert.setFinancialYear(fy);
		
		String fromTaxPeriod = arr[3] != null ? (String) arr[3] : null;
		convert.setFromTaxPeriod(fromTaxPeriod);
		String toTaxPeriod = arr[4] != null ? (String) arr[4] : null;
		convert.setToTaxPeriod(toTaxPeriod);

		Timestamp date = (Timestamp) arr[5];
		if (date != null) {
			String cdateTime = date != null ? EYDateUtil
					.toISTDateTimeFromUTC(date.toLocalDateTime()).toString()
					: null;
			String cDate = cdateTime.substring(0, 10);
			String cTime = cdateTime.substring(11, 19);
			String cupdatedDateTime = (cDate + " " + cTime);
			convert.setInitiatedOn(cupdatedDateTime);
			
		}
		
		convert.setInitiatedBy((String) arr[6]);
		
		Timestamp completionDate = (Timestamp) arr[7];
		if (completionDate != null) {
			String cdateTime = completionDate != null ? EYDateUtil
					.toISTDateTimeFromUTC(completionDate.toLocalDateTime()).toString()
					: null;
			String cDate = cdateTime.substring(0, 10);
			String cTime = cdateTime.substring(11, 19);
			String cupdatedDateTime = (cDate + " " + cTime);
			convert.setCompletionOn(cupdatedDateTime);
			
		}
		
		convert.setStatus((String) arr[8]);
		String gstins = ((String)arr[9]);
		List<GstinDto> gstinsList = new ArrayList<GstinDto>();
		
		String[] gstinArray = gstins.split(",");
		for(int i=0; i<gstinArray.length; i++){
			GstinDto gstinDto = new GstinDto();
			gstinDto.setGstin(gstinArray[i]);
			gstinsList.add(gstinDto);
		}
		
		convert.setGstins(gstinsList);
		
		return convert;
	}

	

	@Override
	public List<BigInteger> getRequestIds(String userName, Long entityId)
			throws AppException {
		try {
			String queryString = "SELECT DISTINCT RRC.REQUESTID AS REQUEST_ID "
					+ "FROM TBL_EWBVSITC04_RECON_CONFIG RRC "
					+ " WHERE RRC.ENTITY_ID =:entityId AND INITIATED_BY=:userName ORDER BY 1 DESC";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);
			q.setParameter("userName", userName);
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
					+ "EwbVsItc04InitiateReconReportRequestStatusDaoImpl.getRequestIds");
		}
	}

	@Override
	public List<EwbVsItc04InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		List<EwbVsItc04InitiateReconReportRequestStatusDto> respList = null;
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
			} else if (reconStatus.equalsIgnoreCase("RECON INITIATED")) {
				reconStatus = "RECON_INITIATED";
			} else if (reconStatus.equalsIgnoreCase("RECON FAILED")) {
				reconStatus = "RECON_FAILED";
			} else if (reconStatus.equalsIgnoreCase("NO DATA FOUND")) {
				reconStatus = "NO_DATA_FOUND";
			}
		}

		try {
			
			boolean isusernamereq = commonUtility
					 .getAnsForQueMultipleUserAccessToAsyncReports(entityId);
			String initiatedby="";
			if(isusernamereq){
				initiatedby=" AND INITIATED_BY =:userName ";
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
			if (reqDto.getReconStatus() != null
					&& !reqDto.getReconStatus().isEmpty()) {
				q.setParameter("reconStatus", reconStatus);
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
					&& (!reqDto.getReconStatus().isEmpty())) {
				q.setParameter("reconStatus",reconStatus);
			}
			
			q.setParameter("entityId", entityId);
			
			

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

				
		String query =	" SELECT REQUESTID,COUNT(*) As COUNT,MAX(FY) AS FY, "
				+ " MAX(CASE WHEN RIGHT(ITC_FROM_TAXPERIOD,1)=3 THEN 'Q1' "
				+ " WHEN RIGHT(ITC_FROM_TAXPERIOD,1)=4 THEN 'Q2' "
				+ " WHEN RIGHT(ITC_FROM_TAXPERIOD,1)=5 THEN 'Q3' "
				+ " WHEN RIGHT(ITC_FROM_TAXPERIOD,1)=6 THEN 'Q4' "
				+ " WHEN RIGHT(ITC_FROM_TAXPERIOD,1)=7 THEN 'H1' "
				+ " WHEN RIGHT(ITC_FROM_TAXPERIOD,1)=8 THEN 'H2' "
				+ " END) AS FROM_TAX_PERIOD, "
				+ " MAX(CASE WHEN RIGHT(ITC_TO_TAXPERIOD,1)=3 THEN 'Q1' "
				+ " WHEN RIGHT(ITC_TO_TAXPERIOD,1)=4 THEN 'Q2' "
				+ " WHEN RIGHT(ITC_TO_TAXPERIOD,1)=5 THEN 'Q3' "
				+ " WHEN RIGHT(ITC_TO_TAXPERIOD,1)=6 THEN 'Q4' "
				+ " WHEN RIGHT(ITC_TO_TAXPERIOD,1)=7 THEN 'H1' "
				+ " WHEN RIGHT(ITC_TO_TAXPERIOD,1)=8 THEN 'H2' "
				+ " END) AS FROM_TO_PERIOD, "
				+ " MAX(INITIATED_ON) AS INITIATION,MAX(INITIATED_BY) "
				+ " AS INITIATION_BY,MAX(COMPLETED_ON) AS COMPLETION, "
				+ " MAX(STATUS) AS STATUS, "
				+ " STRING_AGG(GSTIN,',' ORDER BY ID) AS GSTIN_LIST FROM TBL_EWBVSITC04_RECON_CONFIG "
				+ " WHERE ENTITY_ID = :entityId " 
				+ condtion 
				+ " GROUP BY REQUESTID ORDER BY REQUESTID DESC ";

		return query;
	}

	private String queryCondition(Gstr2InitiateReconReqDto reqDto, boolean isusernamereq, 
			String initiatedby) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin EwbVsItc04InitiateReconReportRequestStatusDaoImpl .queryCondition() ";
			LOGGER.debug(msg);
		}
		
        StringBuilder queryBuilder = new StringBuilder();
		
		// EWB_FROM_TAXPERIOD, EWB_TO_TAXPERIOD -> INTEGER
		
		if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() != 0) {
			queryBuilder.append(" AND ( ITC_FROM_TAXPERIOD >= :returnPeriodFrom"
					+ " AND ITC_TO_TAXPERIOD <= :returnPeriodTo )");
		}else if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() == 0) {
			queryBuilder.append(" AND ITC_FROM_TAXPERIOD >= :returnPeriodFrom");
		}
		
		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				queryBuilder.append(" AND INITIATED_BY IN (:initiationByUserId) ");
			} 
		}
		
		if(isusernamereq){
			queryBuilder.append(initiatedby);
		}
		
				
		if (reqDto.getReconStatus() != null
			&& (!reqDto.getReconStatus().isEmpty())) {
			queryBuilder.append(" AND STATUS =:reconStatus ");
		}
		
		return queryBuilder.toString();

	}

}

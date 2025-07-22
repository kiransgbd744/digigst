/**
 * 
 */
package com.ey.advisory.app.anx2.initiaterecon;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconReportRequestStatusDao;
import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2AERPRequestEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2AERPRequestRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2InitiateReconReportRequestStatusDaoImpl")
public class Gstr2InitiateReconReportRequestStatusDaoImpl
		implements Gstr2InitiateReconReportRequestStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("AutoRecon2AERPRequestRepository")
	private AutoRecon2AERPRequestRepository erpRequestRepo;
	
	@Override
	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestStatus(
			String userName, Long entityId) {

		String queryString = createQueryString(userName);

		Query q = entityManager.createNativeQuery(queryString);

		// userName = "SYSTEM";

		q.setParameter("userName", userName);
		q.setParameter("entityId", entityId);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Gstr2InitiateReconReportRequestStatusDto> retList = list.stream()
				.map(o -> convert(o, null))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Gstr2InitiateReconReportRequestStatusDto convert(Object[] arr, 
			Map<String, AutoRecon2AERPRequestEntity> map) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr2InitiateReconReportRequestStatusDto object";
			LOGGER.debug(str);
		}
		AutoRecon2AERPRequestEntity erpEntity = null;
		Gstr2InitiateReconReportRequestStatusDto convert = new Gstr2InitiateReconReportRequestStatusDto();

		BigInteger b = (BigInteger) arr[0];
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
		convert.setPath((String) arr[6]);
		BigInteger bi = (BigInteger) arr[7];
		Integer gstnCount = bi.intValue();
		convert.setGstinCount(gstnCount);
		
		 date = (Timestamp) arr[17];
		 String gstinInitiatedOn = null;
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			String dateTime = EYDateUtil.toISTDateTimeFromUTC(dt).toString();
			dateTime = String.format("%-20s", dateTime).replace(' ', '0');
			String Date = dateTime.substring(0, 10);
			String Time = dateTime.substring(11, 19);
			 gstinInitiatedOn = (Date + " " + Time);
			
		}
		
		 date = (Timestamp) arr[18];
		 String gstinCompletedOn = null;
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			String dateTime = EYDateUtil.toISTDateTimeFromUTC(dt).toString();
			dateTime = String.format("%-20s", dateTime).replace(' ', '0');
			String Date = dateTime.substring(0, 10);
			String Time = dateTime.substring(11, 19);
			gstinCompletedOn = (Date + " " + Time);
			
		}
		GstinDto gstinDto = null;
		if(map != null && !map.isEmpty() && (!arr[15].toString().equalsIgnoreCase("EINVPR"))) {
			
			 erpEntity = map.get(requestId + "_" + (String) arr[8]);
			 if(erpEntity!=null)
			 {
			 gstinDto = new GstinDto((String) arr[8], 
						(String) arr[16], gstinInitiatedOn, gstinCompletedOn, 
						erpEntity.getStatus());

		}
		else
		{
			gstinDto = new GstinDto((String) arr[8], 
					(String) arr[16], gstinInitiatedOn, gstinCompletedOn, 
					null);

		}
		}else
		{
			gstinDto = new GstinDto((String) arr[8], 
					(String) arr[16], gstinInitiatedOn, gstinCompletedOn, 
					null);
		}
		if (arr[3] != null && arr[9] != null) {
			Integer toTaxPeriod = ((Integer) arr[3]).intValue();
			convert.setToTaxPeriod(toTaxPeriod);

			Integer fromTaxPeriod = ((Integer) arr[9]).intValue();
			convert.setFromTaxPeriod(fromTaxPeriod);
		} else {

			convert.setToTaxPeriod(null);
			convert.setFromTaxPeriod(null);

		}

		date = (Timestamp) arr[10];
		String toDate = date != null ? date.toString() : null;
		convert.setToDocDate(toDate);

		date = (Timestamp) arr[11];
		String fromDate = date != null ? date.toString() : null;
		convert.setFromDocDate(fromDate);

		convert.setReqType((String) arr[12] != null ? (String) arr[12] : "ERP");
		convert.setGstins(new ArrayList<GstinDto>(Arrays.asList(gstinDto)));
		if (arr[13] != null && arr[14] != null) {
			Integer toTaxPeriodA2 = ((Integer) arr[13]).intValue();
			convert.setToTaxPeriod2A(toTaxPeriodA2);

			Integer fromTaxPeriodA2 = ((Integer) arr[14]).intValue();
			convert.setFromTaxPeriod2A(fromTaxPeriodA2);
		} else {
			convert.setToTaxPeriod2A(null);
			convert.setFromTaxPeriod2A(null);
		}
		convert.setReconType((String) arr[15]);
		convert.setIsItcRejOpted(
				arr[19] != null && !((String) arr[19]).trim().isEmpty()
						? (String) arr[19]
						: null);

		
		return convert;
	}

	private String createQueryString(String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}
		String queryStr = "SELECT RRC.RECON_REPORT_CONFIG_ID AS REQUEST_ID, "
				+ "RRC.CREATED_DATE, RRC.CREATED_BY, RRC.To_RET_PERIOD, "
				+ "RRC.COMPLETED_ON, "
				+ "RRC.STATUS, RRC.FILE_PATH AS PATH, COUNT(*) "
				+ "AS GSTIN_COUNT, RRGD.GSTIN, RRC.FROM_RET_PERIOD, "
				+ "RRC.TO_DOC_DATE, RRC.FROM_DOC_DATE, RRC.REQUEST_TYPE, "
				+ "RRC.A2_TO_RET_PERIOD, RRC.A2_FROM_RET_PERIOD, "
				+ "RRC.RECON_TYPE FROM " + "TBL_RECON_REPORT_CONFIG RRC "
				+ "INNER JOIN TBL_RECON_REPORT_GSTIN_DETAILS RRGD "
				+ "ON RRGD.RECON_REPORT_CONFIG_ID = RRC.RECON_REPORT_CONFIG_ID "
				+ " WHERE (RRC.CREATED_BY = :userName "
				+ " OR RRC.CREATED_BY = 'SYSTEM')"
				+ " AND RRC.ENTITY_ID =:entityId" + " GROUP BY "
				+ "RRC.RECON_REPORT_CONFIG_ID, RRC.CREATED_DATE, "
				+ "RRC.CREATED_BY, RRC.COMPLETED_ON, RRC.STATUS,"
				+ " RRC.To_RET_PERIOD,RRC.FROM_RET_PERIOD, RRGD.GSTIN, "
				+ "RRC.FILE_PATH, RRC.TO_DOC_DATE, RRC.FROM_DOC_DATE, "
				+ "RRC.REQUEST_TYPE,RRC.A2_TO_RET_PERIOD, "
				+ "RRC.A2_FROM_RET_PERIOD, RECON_TYPE";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s",
					queryStr);
			LOGGER.debug(str);
		}

		return queryStr;
	}

	@Override
	public List<BigInteger> getRequestIds(String userName, Long entityId, 
			Gstr2InitiateReconReqDto reqDto)
			throws AppException {
		try {
			String initiationDateFrom = reqDto.getInitiationDateFrom();
			String initiationDateTo = reqDto.getInitiationDateTo();
			String queryString=null;
			String reconType = reqDto.getReconType();
			String convertdFromTaxPeriod=null;
			String convertdToTaxPeriod=null;
			
			if(Strings.isNullOrEmpty(reconType))
			{
				 queryString = "SELECT RRC.RECON_REPORT_CONFIG_ID AS REQUEST_ID "
						+ " FROM TBL_RECON_REPORT_CONFIG RRC "
						+ " WHERE RRC.ENTITY_ID =:entityId AND "
						+ " TO_VARCHAR(RRC.CREATED_DATE,'YYYY-MM-DD') "
						+ " BETWEEN :initiationDateFrom  AND :initiationDateTo "
						+ " ORDER BY 1 DESC";
			}
			else
			{
				 convertdFromTaxPeriod = convertFromTaxPeriod(initiationDateFrom);
				 convertdToTaxPeriod = convertToTaxPeriod(initiationDateTo);
				
				
				queryString = "SELECT RRC.REQUEST_ID AS REQUEST_ID "
						+ " FROM TBL_RECON_GSTR2A2B_VS_3B RRC "
						+ " WHERE RRC.ENTITY_ID =:entityId AND "
						+ " TO_VARCHAR(RRC.INITIATED_ON,'YYYY-MM-DD') "
						+ " BETWEEN :convertdFromTaxPeriod  AND :convertdToTaxPeriod "
						+ " ORDER BY 1 DESC";
			}
			
			

			Query q = entityManager.createNativeQuery(queryString);
			if(Strings.isNullOrEmpty(reconType))
			{
			q.setParameter("entityId", entityId);
			q.setParameter("initiationDateFrom", initiationDateFrom);
			q.setParameter("initiationDateTo", initiationDateTo);
			}
			else
			{
				q.setParameter("entityId", entityId);
				q.setParameter("convertdFromTaxPeriod", convertdFromTaxPeriod);
				q.setParameter("convertdToTaxPeriod", convertdToTaxPeriod);
			}
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
					+ "Gstr2InitiateReconReportRequestStatusDaoImpl.getRequestIds");
		}
	}

	@Override
	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		List<Gstr2InitiateReconReportRequestStatusDto> respList = null;
		Long entityId = reqDto.getEntityId();
		List<Long> requestId = reqDto.getRequestId();
		String reconType = reqDto.getReconType();

		List<String> reconTypes = new ArrayList<>();
		if (reconType != null && !reconType.isEmpty()) {
			if (reconType.equalsIgnoreCase("2APR")) {
				reconTypes.add("2APR");
				reconTypes.add("AP_M_2APR");
				reconTypes.add("NON_AP_M_2APR");
			} else if (reconType.equalsIgnoreCase("2APRAU")) {
				reconTypes.add("AUTO_2APR");
			} else if (reconType.equalsIgnoreCase("EINVPR")) {
				reconTypes.add("EINVPR");
			}else
			{
				reconTypes.add("2BPR");
			}
		}

		String initiationDateFrom = reqDto.getInitiationDateFrom();
		String initiationDateTo = reqDto.getInitiationDateTo();
		List<String> initiationByUserId = reqDto.getInitiationByUserId();
		
		List<Long> entityIds = new ArrayList<Long>();
		entityIds.add(entityId);
		List<Long> optedEntities = entityConfigPemtRepo
				.getAllEntitiesOptedUserRestriction(entityIds, "I29");

		boolean restrictionFlag = true;
		if (optedEntities == null || optedEntities.isEmpty()) {
			restrictionFlag = false;
		}

		String reconStatus = reqDto.getReconStatus();

		if (reconStatus != null && !reconStatus.isEmpty()) {
			if (reconStatus.equalsIgnoreCase("REPORT GENERATION FAILED")) {
				reconStatus = "REPORT_GENERATION_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATED")) {
				reconStatus = "REPORT_GENERATED";
			}
		}

		try {
			String condtion = queryCondition(reqDto, restrictionFlag, reconTypes);
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
				q.setParameter("reconTypes", reconTypes);
			}
			if (!CollectionUtils.isEmpty(reqDto.getRequestId())) {
				q.setParameter("requestId", requestId);
			}
			if (restrictionFlag) {
				q.setParameter("userName", userName);
			} else if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				q.setParameter("initiationByUserId", initiationByUserId);
			}
			if (reqDto.getReconStatus() != null
					&& !reqDto.getReconStatus().isEmpty()) {
				q.setParameter("reconStatus", reconStatus);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data {} %s", reqDto);
			}
			
			List<AutoRecon2AERPRequestEntity> erpRepoList = erpRequestRepo.findAll();
			
			Map<String, AutoRecon2AERPRequestEntity> map = erpRepoList.stream()
					.collect(Collectors.toMap(o -> o.getReconConfigID()
							+ "_" + o.getGstin(), o -> o,
							(o1, o2) -> o2, TreeMap::new));
			
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			respList = list.stream().map(o -> convert(o,map))
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

		String query = "SELECT RRC.RECON_REPORT_CONFIG_ID AS REQUEST_ID, "
	            + "RRC.CREATED_DATE, RRC.CREATED_BY, RRC.To_RET_PERIOD, "
	            + "RRC.COMPLETED_ON, "
	            + "RRC.STATUS, RRC.FILE_PATH AS PATH, COUNT(*) "
	            + "AS GSTIN_COUNT, RRGD.GSTIN, RRC.FROM_RET_PERIOD, "
	            + "RRC.TO_DOC_DATE, RRC.FROM_DOC_DATE, RRC.REQUEST_TYPE, "
	            + "RRC.A2_TO_RET_PERIOD, RRC.A2_FROM_RET_PERIOD, "
	            + "RRC.RECON_TYPE, RRGD.STATUS AS STATUS_GSTIN, "
	            + "RRGD.CREATED_ON AS CREATED_ON_GSTIN, "
	            + "RRGD.COMPLETED_ON AS COMPLETED_ON_GSTIN, "
	            + "RRC.IS_ITC_REJ_OPTED "  // Added the new column here
	            + "FROM TBL_RECON_REPORT_CONFIG RRC "
	            + "INNER JOIN TBL_RECON_REPORT_GSTIN_DETAILS RRGD "
	            + "ON RRGD.RECON_REPORT_CONFIG_ID = RRC.RECON_REPORT_CONFIG_ID "
	            + "WHERE RRC.ENTITY_ID =:entityId " + condtion 
	            + " GROUP BY RRC.RECON_REPORT_CONFIG_ID, RRC.CREATED_DATE, "
	            + "RRC.CREATED_BY, RRC.COMPLETED_ON, RRC.STATUS, "
	            + "RRC.To_RET_PERIOD, RRC.FROM_RET_PERIOD, RRGD.GSTIN, "
	            + "RRC.FILE_PATH, RRC.TO_DOC_DATE, RRC.FROM_DOC_DATE, "
	            + "RRC.REQUEST_TYPE, RRC.A2_TO_RET_PERIOD, "
	            + "RRC.A2_FROM_RET_PERIOD, RRC.RECON_TYPE, RRGD.STATUS, "
	            + "RRGD.CREATED_ON, RRGD.COMPLETED_ON, "
	            + "RRC.IS_ITC_REJ_OPTED";  // Also added here in GROUP BY


		return query;
	}

	private String queryCondition(Gstr2InitiateReconReqDto reqDto, boolean restrictionFlag, 
						List<String> reconTypes) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Gstr2InitiateReconReportRequestStatusDaoImpl.queryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		if (reqDto.getInitiationDateFrom() != null
				&& (!reqDto.getInitiationDateFrom().isEmpty())) {
			condition1.append(
					" AND TO_VARCHAR(ADD_SECONDS(RRC.CREATED_DATE, 19800), 'YYYY-MM-DD') BETWEEN :initiationDateFrom ");
		}

		if (reqDto.getInitiationDateTo() != null
				&& (!reqDto.getInitiationDateTo().isEmpty())) {
			condition1.append(" AND :initiationDateTo ");
		}

		if (!CollectionUtils.isEmpty(reqDto.getRequestId())) {
			condition1
					.append(" AND RRC.RECON_REPORT_CONFIG_ID IN (:requestId) ");
		}

		if (reqDto.getReconType() != null
				&& (!reqDto.getReconType().isEmpty())) {
			condition1.append(" AND RRC.RECON_TYPE IN (:reconTypes) ");
		}

		if (restrictionFlag) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				condition1.append(" AND RRC.CREATED_BY = :userName ");
			} else if(reconTypes.contains("2BPR") || reconTypes.contains("EINVPR")) {
				condition1.append(" AND RRC.CREATED_BY = :userName ");
			} else {
				condition1.append(" AND (RRC.CREATED_BY = :userName "
						+ " OR RRC.CREATED_BY = 'SYSTEM') AND RRC.ENTITY_ID =:entityId ");
			}
		} else if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
			condition1.append(" AND RRC.CREATED_BY IN (:initiationByUserId) ");
		}

		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			condition1.append(" AND RRC.STATUS =:reconStatus ");
		}

		return condition1.toString();
	}

	
	 public String convertFromTaxPeriod(String fromTaxperiod) {
	        String initiationDateFrom = fromTaxperiod; // Example input
	        
	        // Step 1: Extract month and year from the input string
	        String month = initiationDateFrom.substring(0, 2);
	        String year = initiationDateFrom.substring(2, 6);
	        
	        // Step 2: Construct the date string with day = 01
	        String dateStr = year + "-" + month + "-01";
	        
	        // Step 3: Convert to LocalDate and format it as needed
	        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        
	        // Step 4: Format the date to the desired output
	        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        
	        System.out.println(formattedDate); // Output will be "2024-08-01"
			return formattedDate;
	    }
	 public String convertToTaxPeriod(String ToTaxperiod) {
	        String initiationDateto = ToTaxperiod; // Example input
	        
	        // Step 1: Extract month and year from the input string
	        String month = initiationDateto.substring(0, 2);
	        String year = initiationDateto.substring(2, 6);
	        
	        // Step 2: Construct the date string with day = 01
	        String dateStr = year + "-" + month + "-31";
	        
	        // Step 3: Convert to LocalDate and format it as needed
	        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        
	        // Step 4: Format the date to the desired output
	        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        
	        System.out.println(formattedDate); // Output will be "2024-08-01"
			return formattedDate;
	    }


		@Override
		public List<String> getUserIds(String userName, Long entityId,
				Gstr2InitiateReconReqDto reqDto) {

			try {
				/*String initiationDateFrom = reqDto.getInitiationDateFrom();
				String initiationDateTo = reqDto.getInitiationDateTo();*/
				String queryString=null;
				String reconType = reqDto.getReconType();
				String convertdFromTaxPeriod=null;
				String convertdToTaxPeriod=null;
				
				if(!Strings.isNullOrEmpty(reconType))
				{ /*convertdFromTaxPeriod = convertFromTaxPeriod(initiationDateFrom);
				 convertdToTaxPeriod = convertToTaxPeriod(initiationDateTo);*/
					queryString = "SELECT RRC.INITIATED_BY AS INITIATED_BY "
							+ " FROM TBL_RECON_GSTR2A2B_VS_3B RRC "
							+ " WHERE RRC.ENTITY_ID =:entityId "
							+ " ORDER BY 1 DESC";
				}

				Query q = entityManager.createNativeQuery(queryString);
			
					q.setParameter("entityId", entityId);
					/*q.setParameter("convertdFromTaxPeriod", convertdFromTaxPeriod);
					q.setParameter("convertdToTaxPeriod", convertdToTaxPeriod);*/
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("executing query to get the data of RequestIds "
							+ ", entityId " + entityId + "and query = "
							+ queryString);
				}
				@SuppressWarnings("unchecked")
				List<String> list = q.getResultList();
				return list;
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new AppException("Exception Occured in "
						+ "Gstr2InitiateReconReportRequestStatusDaoImpl.getRequestIds");
			}
		
		}

		@Override
		public List<String> getUserEmailIds(String userName, Long entityId,
				Gstr2InitiateReconReqDto reqDto) {

			try {
				/*String initiationDateFrom = reqDto.getInitiationDateFrom();
				String initiationDateTo = reqDto.getInitiationDateTo();*/
				String queryString=null;
				String reconType = reqDto.getReconType();
				/*String convertdFromTaxPeriod=null;
				String convertdToTaxPeriod=null;*/
				
				if(!Strings.isNullOrEmpty(reconType))
				{ /*convertdFromTaxPeriod = convertFromTaxPeriod(initiationDateFrom);
				 convertdToTaxPeriod = convertToTaxPeriod(initiationDateTo);*/
					queryString = "SELECT RRC.INITIATED_BY_EMAIL_ID AS INITIATED_BY_EMAIL_ID "
							+ " FROM TBL_RECON_GSTR2A2B_VS_3B RRC "
							+ " WHERE RRC.ENTITY_ID =:entityId "
							+ " ORDER BY 1 DESC";
				}

				Query q = entityManager.createNativeQuery(queryString);
			
					q.setParameter("entityId", entityId);
					/*q.setParameter("convertdFromTaxPeriod", convertdFromTaxPeriod);
					q.setParameter("convertdToTaxPeriod", convertdToTaxPeriod);*/
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("executing query to get the data of RequestIds "
							+ ", entityId " + entityId + "and query = "
							+ queryString);
				}
				@SuppressWarnings("unchecked")
				List<String> list = q.getResultList();
				return list;
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new AppException("Exception Occured in "
						+ "Gstr2InitiateReconReportRequestStatusDaoImpl.getRequestIds");
			}
		
		}
}

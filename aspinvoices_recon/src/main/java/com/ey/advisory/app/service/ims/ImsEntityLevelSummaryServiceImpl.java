package com.ey.advisory.app.service.ims;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsProcessedInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsSaveJobQueueRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Component("ImsEntityLevelSummaryServiceImpl")
@Slf4j
public class ImsEntityLevelSummaryServiceImpl implements ImsEntityLevelSummaryService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private ImsSaveJobQueueRepository queueRepo;
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;
	
	@Autowired
	private ImsProcessedInvoiceRepository psdRepo;

	private final List<String> GET_TYPES = Arrays.asList("B2B", "B2B - Amendment", "CDN", "CDN - Amendment", "ISD",
			"ISD - Amendment", "ECOM", "ECOM - Amendment", "IMPG", "IMPGSEZ");
	
	final List<String> statusList = ImmutableList.of("In Queue", "InProgress","RefId Generated");

	@Override
	public List<ImsEntitySummaryResponseDto> getImsSummaryEntityLvlData(ImsEntitySummaryReqDto criteria) {
		LOGGER.debug("Processing IMS Summary Entity Level Data for gstin: {}", criteria);

		List<GSTNDetailEntity> regList = gstNDetailRepository.findRegTypeByGstinList(criteria.getGstins());
		Map<String, String> regMap = regList.stream()
				.collect(Collectors.toMap(GSTNDetailEntity::getGstin, GSTNDetailEntity::getRegistrationType));

		Map<String, String> stateNames = entityService.getStateNames(criteria.getGstins());

		Map<String, String> authTokenStatus = authTokenService.getAuthTokenStatusForGstins(criteria.getGstins());
		LOGGER.debug("Fetched auth token statuses: {}", authTokenStatus);

		String queryString = createQueryString(criteria);

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("gstins", criteria.getGstins());

		List<String> tableTypes = criteria.getTableType();

		List<String> sections;

		if (tableTypes.isEmpty() || tableTypes.equals("All")) {
			sections = new ArrayList<>(GET_TYPES);
		} else {
			sections = tableTypes.stream().filter(GET_TYPES::contains).collect(Collectors.toList());
		}
		q.setParameter("sections", sections);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<ImsEntitySummaryResponseDto> retList = new ArrayList<>();

		if (list.isEmpty()) {
			LOGGER.info("No data found. Returning default DTOs.");
		} else {
	
			List<String> inProgressGstins = queueRepo.findInProgressGstins(statusList, criteria.getGstins());
			LOGGER.debug("IMSEntityLevelSummary nativeQuery result is: {}", list);
			retList = list.stream().map(o -> convert(o, regMap, stateNames, authTokenStatus,inProgressGstins))
					.collect(Collectors.toCollection(ArrayList::new));
		}

		// Get GSTINs that were returned in the result
		Set<String> resultGstins = retList.stream().map(ImsEntitySummaryResponseDto::getGstin)
				.collect(Collectors.toSet());

		// Create a separate list for default DTOs
		List<ImsEntitySummaryResponseDto> defaultDtos = new ArrayList<>();

		// Create default DTOs for any GSTINs that didn't return a result
		criteria.getGstins().forEach(gstin -> {
			if (!resultGstins.contains(gstin)) {
				ImsEntitySummaryResponseDto defaultDto = new ImsEntitySummaryResponseDto();
				defaultDto.setGstin(gstin);
				defaultDto.setState(stateNames.get(gstin.toString()));
				defaultDto.setRegType(regMap.get(gstin.toString()));
				defaultDto.setAuthToken(authTokenStatus.get(gstin).equalsIgnoreCase("A") ? "Active" : "Inactive");
				defaultDto.setSaveStatus("NOT INITIATED");

				// Add to the default DTO list instead
				defaultDtos.add(defaultDto);
			}
		});

		// Add the default DTOs to the retList
		retList.addAll(defaultDtos);

		// Sort the final list by GSTIN
		retList.sort(Comparator.comparing(ImsEntitySummaryResponseDto::getGstin));

		return retList;
	}

	private ImsEntitySummaryResponseDto convert(Object[] arr, Map<String, String> regMap,
			Map<String, String> stateNames, Map<String, String> authTokenStatus,List<String> inProgressGstins) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String str = "Converting generic object to ImsEntitySummaryResponseDto object";
				LOGGER.debug(str);
			}
			ImsEntitySummaryResponseDto convert = new ImsEntitySummaryResponseDto();
			String gstin = (String) arr[0];
			convert.setGstin(gstin);
			convert.setState(stateNames.get(convert.getGstin()).toString());
			convert.setRegType(regMap.get(convert.getGstin()).toString());
			convert.setAuthToken(authTokenStatus.get(convert.getGstin()).equalsIgnoreCase("A") ? "Active" : "Inactive");
			
			if (inProgressGstins != null && !inProgressGstins.isEmpty() && inProgressGstins.contains(gstin)) {
				convert.setSaveStatus("In Progress");
			} else {
				convert.setSaveStatus((String) arr[1]);
			}
			convert.setTimeStamp(formatDateTime((Timestamp) arr[2]));

			convert.setGstnTotal(arr[3] != null ? ((Number) arr[3]).intValue() : 0);
			convert.setGstnNoAction(arr[4] != null ? ((Number) arr[4]).intValue() : 0);
			convert.setGstnAccepted(arr[5] != null ? ((Number) arr[5]).intValue() : 0);
			convert.setGstnRejected(arr[6] != null ? ((Number) arr[6]).intValue() : 0);
			convert.setGstnPendingTotal(arr[7] != null ? ((Number) arr[7]).intValue() : 0);

			convert.setAspTotal(arr[8] != null ? ((Number) arr[8]).intValue() : 0);
			convert.setAspNoAction(arr[9] != null ? ((Number) arr[9]).intValue() : 0);
			convert.setAspAccepted(arr[10] != null ? ((Number) arr[10]).intValue() : 0);
			convert.setAspRejected(arr[11] != null ? ((Number) arr[11]).intValue() : 0);
			convert.setAspPendingTotal(arr[12] != null ? ((Number) arr[12]).intValue() : 0);

			return convert;
		} catch (Exception e) {
			throw new AppException("Error while converting data to ImsEntitySummaryResponseDto", e);
		}
	}

	private String createQueryString(ImsEntitySummaryReqDto criteria) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}
		String queryStr = "WITH STATUS AS ( " +
			    "SELECT SUPPLIER_GSTIN, " +
			    "(CASE " +
			        "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') LIKE '%2%' THEN 'Inprogress' " +
			        "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') LIKE '%1%' THEN 'Initiated' " +
			        "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') = '5' THEN 'Failed' " +
			        "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') = '3' THEN 'Saved' " +
			        "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') LIKE '%5,3%' THEN 'Partially Saved' " +
			        "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') LIKE '%3,5%' THEN 'Partially Saved' " +
			        "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') LIKE '%4%' THEN 'Partially Saved' " +
			        "ELSE 'Initiated' " +
			    "END) AS STATUS, " +
			    "MAX(IFNULL(MODIFIED_ON,NOW())) AS CREATED_ON " +
			    "FROM ( " +
			        "SELECT SUPPLIER_GSTIN, " +
			        "MAP(GSTN_SAVE_STATUS,'IN',1,'IP',2,'P',3,'PE',4,'REC',5,'ER',5) GSTN_SAVE_STATUS, " +
			        "GSTN_SAVE_STATUS AS GSTN_SAVE_STATUS1, " +
			        "MAX(MODIFIED_ON) AS MODIFIED_ON " +
			        "FROM GSTR1_GSTN_SAVE_BATCH " +
			        "WHERE IS_DELETE = FALSE " +
			        "AND RETURN_TYPE = 'IMS' " +
			        "AND SUPPLIER_GSTIN IN (:gstins)"+
			        "GROUP BY SUPPLIER_GSTIN,GSTN_SAVE_STATUS" +
			    ") " +
			    "GROUP BY SUPPLIER_GSTIN " +
			"), GSTN AS ( " +
			    "SELECT GSTIN, " +
			    "SUM(GSTN_TOTAL) AS GSTN_TOTAL, " +
			    "SUM(GSTN_NO_ACTION) AS GSTN_NO_ACTION, " +
			    "SUM(GSTN_ACCEPTED) AS GSTN_ACCEPTED, " +
			    "SUM(GSTN_REJECTED) AS GSTN_REJECTED, " +
			    "SUM(GSTN_PENDING) AS GSTN_PENDING " +
			    "FROM TBL_GETIMS_GSTN_COUNTS " +
			    "WHERE IS_DELETE = FALSE " +
			    "AND GSTIN IN (:gstins) " +
			    "AND MAP(SECTION, " +
			        "'B2BA','B2B - Amendment', " +
			        "'CN', 'CDN', " +
			        "'DN','CDN', " +
			        "'CNA','CDN - Amendment', " +
			        "'DNA','CDN - Amendment', " +
			        "'ISDCN','ISD', " +
			        "'ISDA','ISD - Amendment', " +
			        "'ISDCNA','ISD - Amendment', " +
			        "'ECOMA','ECOM - Amendment', " +
			        "'IMPGA','IMPG', " +
			        "'IMPGSEZA','IMPGSEZ', " +
			        "SECTION,SECTION) IN (:sections) " +
			    "GROUP BY GSTIN " +
			"), DIGI AS ( " +
			    "SELECT RECIPIENT_GSTIN, " +
			    "COUNT(*) AS DIGI_TOTAL, " +
			    "SUM(CASE WHEN ACTION_RESPONSE = 'N' THEN 1 ELSE 0 END) AS DIGI_NO_ACTION, " +
			    "SUM(CASE WHEN ACTION_RESPONSE = 'A' THEN 1 ELSE 0 END) AS DIGI_ACCEPTED, " +
			    "SUM(CASE WHEN ACTION_RESPONSE = 'R' THEN 1 ELSE 0 END) AS DIGI_REJECTED, " +
			    "SUM(CASE WHEN ACTION_RESPONSE = 'P' THEN 1 ELSE 0 END) AS DIGI_PENDING " +
			    "FROM TBL_GETIMS_PROCESSED " +
			    "WHERE IS_DELETE = FALSE " +
			    "AND IS_SAVED_TO_GSTIN = FALSE " +
			    "AND RECIPIENT_GSTIN IN (:gstins) " +
			    "AND MAP(TABLE_TYPE, " +
			        "'B2BA','B2B - Amendment', " +
			        "'CN', 'CDN', " +
			        "'DN','CDN', " +
			        "'CNA','CDN - Amendment', " +
			        "'DNA','CDN - Amendment', " +
			        "'CDNA','CDN - Amendment', " +
			        "'ECOMA','ECOM - Amendment', " +
			        "TABLE_TYPE) IN (:sections) " +
			    "GROUP BY RECIPIENT_GSTIN " +
			") " +
			"SELECT IFNULL(IFNULL(STATUS.SUPPLIER_GSTIN, GSTN.GSTIN), DIGI.RECIPIENT_GSTIN) AS GSTIN, " +
			"IFNULL(STATUS.STATUS, 'NOT INITIATED') AS STATUS, " +
			"STATUS.CREATED_ON, " +
			"GSTN.GSTN_TOTAL, GSTN.GSTN_NO_ACTION, GSTN.GSTN_ACCEPTED, GSTN.GSTN_REJECTED, GSTN.GSTN_PENDING, " +
			"IFNULL(DIGI.DIGI_TOTAL, 0) AS DIGI_TOTAL, " +
			"IFNULL(DIGI.DIGI_NO_ACTION, 0) AS DIGI_NO_ACTION, " +
			"IFNULL(DIGI.DIGI_ACCEPTED, 0) AS DIGI_ACCEPTED, " +
			"IFNULL(DIGI.DIGI_REJECTED, 0) AS DIGI_REJECTED, " +
			"IFNULL(DIGI.DIGI_PENDING, 0) AS DIGI_PENDING " +
			"FROM GSTN " +
			"LEFT JOIN DIGI ON DIGI.RECIPIENT_GSTIN = GSTN.GSTIN " +
			"LEFT JOIN STATUS ON GSTN.GSTIN = STATUS.SUPPLIER_GSTIN;";


		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s", queryStr);
			LOGGER.debug(str);
		}

		return queryStr;
	}

	@Override
	public List<ImsSaveSatusPopUpResponseDto> getImsDetailCallPopupData(String gstin) {
		LOGGER.debug("Processing IMS Detail Call PopUp Data for gstin: {}", gstin);

		if (gstin == null || gstin.isEmpty()) {
			LOGGER.error("GSTIN is null or empty");
			return Collections.emptyList();
		}

		List<ImsSaveSatusPopUpResponseDto> responseList = new ArrayList<>();
		try {
			List<Object[]> savedDataList = getImsSaveStatusDetails(gstin);

			if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
				for (Object[] data : savedDataList) {
					ImsSaveSatusPopUpResponseDto responseDto = new ImsSaveSatusPopUpResponseDto();

					responseDto.setDateTime(formatDateTime((Timestamp) data[1]));
					String action = data[6] != null ? (String) data[6] : "SAVE";
					if(action.equalsIgnoreCase("SAVE")) {
						responseDto.setRequestType("Save"  + " IMS");
						}else {
							responseDto.setRequestType("Reset"  + " IMS");
						}
					//responseDto.setRequestType(action + " IMS");
					responseDto.setRefId(data[3] != null ? (String) data[3] : null);
					responseDto.setSection((String) data[2]);
					responseDto.setStatus(data[4] != null ? (String) data[4] : " ");
					//responseDto.setStatus(data[4] != null ? mapStatus((String) data[4]) : " ");
					responseDto.setErrorCount((Integer) data[5]);
					responseDto.setCount((Integer) data[7]);
					responseList.add(responseDto);
				}
			} else {
				LOGGER.debug("No data found for GSTIN: {}", gstin);
			}
		} catch (Exception e) {
			throw new AppException("Error processing IMS Detail Call PopUp GSTIN: {}", e);
		}
		return responseList;
	}

	private List<Object[]> getImsSaveStatusDetails(String gstin) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ID, CREATED_ON, SECTION, GSTN_SAVE_REF_ID, GSTN_SAVE_STATUS, "
				+ "IFNULL(ERROR_COUNT, 0) ERROR_COUNT, OPERATION_TYPE, BATCH_SIZE  FROM GSTR1_GSTN_SAVE_BATCH "
				+ "WHERE RETURN_TYPE = 'IMS' AND CREATED_ON IS NOT NULL AND GSTN_SAVE_REF_ID IS NOT NULL "
				+ "AND SUPPLIER_GSTIN = :gstin AND IS_DELETE = false "
				+ "GROUP BY ID, CREATED_ON, GSTN_SAVE_REF_ID, OPERATION_TYPE, "
				+ "GSTN_SAVE_STATUS, ERROR_COUNT, SECTION, BATCH_SIZE " + "ORDER BY CREATED_ON DESC, GSTN_SAVE_STATUS");

		Query query = entityManager.createNativeQuery(buffer.toString());

		query.setParameter("gstin", gstin);

		@SuppressWarnings("unchecked")
		List<Object[]> itemsList = query.getResultList();

		return itemsList;
	}

	private String formatDateTime(Timestamp timestamp) {
		if (timestamp == null)
			return null;
		LocalDateTime dateTimeInIst = EYDateUtil.toISTDateTimeFromUTC(timestamp);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		return dateTimeInIst.format(formatter);
	}

	private String mapStatus(String gstnSaveStatus) {
		switch (gstnSaveStatus) {
		case "P":
			return "Success";
		case "PE":
			return "Partial-Success";
		case "ER":
			return "Failed";
		case "ERC":
			return "In-Progress";
		case "IP":
			return "In-Progress";
		case "REC":
			return "In-Progress";
		default:
			return "Initiated";
		}
	}

	@Override
	public List<ImsSaveQueueStatusResponseDto> getImsDetailQueueData(String gstin) {
		List<ImsSaveJobQueueEntity> queueEntitites = queueRepo.findAllData(gstin);
		List<ImsSaveQueueStatusResponseDto> dtos = new ArrayList<>();
		
		for(ImsSaveJobQueueEntity entity : queueEntitites){
			ImsSaveQueueStatusResponseDto dto = new ImsSaveQueueStatusResponseDto();
			dto.setMessage(entity.getSaveResponse());
			dto.setSection(entity.getSection());
			dto.setStatus(entity.getStatus());	
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			dto.setDateTime(EYDateUtil.toISTDateTimeFromUTC
					(entity.getCreatedOn()).format(formatter));
			String action = entity.getAction() != null ? entity.getAction() : "SAVE";
			if(action.equalsIgnoreCase("SAVE")) {
			dto.setRequestType("Save"  + " IMS");
			}else {
			dto.setRequestType("Reset"  + " IMS");
			}
			dtos.add(dto);
		}
	
		return dtos;
	}
	



}

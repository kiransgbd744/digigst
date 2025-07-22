package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Component("SupplierImsEntityLevelServiceImpl")
@Slf4j
public class SupplierImsEntityLevelServiceImpl implements SupplierImsEntityLevelService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	private GetAnx1BatchRepository batchStatusRepository;

	private final List<String> GET_TYPES = Arrays.asList("B2B", "B2BA", "CDNR", "CDNRA", "ECOM", "ECOMA");

	private final List<String> RETURN_TYPE = Arrays.asList("GSTR1", "GSTR1A");

	private static final String SUPPLIER_IMS_STS_SUCCESS = "SUCCESS";
	private static final String SUPPLIER_IMS_STS_SUCCESSNODATA = "SUCCESS_WITH_NO_DATA";
	private static final String SUPPLIER_IMS_STS_PARTIALSUCCESS = "PARTIAL SUCCESS";
	private static final String SUPPLIER_IMS_STS_FAILED = "FAILED";
	private static final String SUPPLIER_IMS_STS_INPROGRESS = "INPROGRESS";
	private static final String SUPPLIER_IMS_STS_INITIATED = "INITIATED";
	private static final String SUPPLIER_IMS_STS_NOTINITIATED = "NOT INITIATED";

	@Override
	public List<SupplierImsEntitySummaryResponseDto> getSupplierImsSummaryEntityLvlData(
			SupplierImsEntityReqDto criteria) {
		LOGGER.debug("Processing SUPPLIER_IMS Summary Entity Level Data for gstin: {}", criteria);

		try {
			List<String> gstins = criteria.getGstins();
			List<GSTNDetailEntity> regList = gstNDetailRepository.findRegTypeByGstinList(criteria.getGstins());
			Map<String, String> regMap = regList.stream()
					.collect(Collectors.toMap(GSTNDetailEntity::getGstin, GSTNDetailEntity::getRegistrationType));

			Map<String, String> stateNames = entityService.getStateNames(criteria.getGstins());
			Map<String, String> authTokenStatus = authTokenService.getAuthTokenStatusForGstins(gstins);
			LOGGER.debug("Fetched auth token statuses: {}", authTokenStatus);

			List<Object[]> gstrSummaryStatus = getGstr1And1AStatus(gstins, criteria.getReturnPeriod());

			Map<String, Pair<String, String>> supplierImsStatusAndTimeStamp = getSupplierImsStatusAndTimeStamp(
					gstins, criteria.getReturnPeriod());

			List<String> sections = getFilteredSections(criteria.getTableTypes(), GET_TYPES);
			List<String> retTypes = getFilteredSections(criteria.getReturnTypes(), RETURN_TYPE);

			StoredProcedureQuery storedProc = buildStoredProcedureQuerySummaryScreen(gstins, criteria.getReturnPeriod(),
					sections, retTypes, "summaryLevel");
			List<Object[]> resultList = getStoredProcedureResults(storedProc);
			LOGGER.debug("Result list fetched from stored procedure: {}", resultList);

			List<SupplierImsEntitySummaryResponseDto> retList = new ArrayList<>();

			if (resultList.isEmpty()) {
				LOGGER.info("SUPPLIER_IMS Summary Entity Proc No data found. Returning default DTOs.");
			} else {
				LOGGER.debug("SUPPLIER_IMS Summary Entity Proc result is: {}", resultList);
				retList.addAll(
						resultList.stream()
								.map(row -> convertToSummaryDto(row, authTokenStatus, regMap, stateNames,
										supplierImsStatusAndTimeStamp, gstrSummaryStatus))
								.collect(Collectors.toList()));

			}

			// Get GSTINs that were returned in the result
			Set<String> resultGstins = retList.stream().map(SupplierImsEntitySummaryResponseDto::getGstin)
					.collect(Collectors.toSet());
			List<String> missingGstins = criteria.getGstins().stream().filter(gstin -> !resultGstins.contains(gstin))
					.collect(Collectors.toList());

			// Create a separate list for default DTOs
			// Add the default DTOs to the retList
			retList.addAll(buildSummaryDefaultDtos(missingGstins, stateNames, regMap, authTokenStatus,
					supplierImsStatusAndTimeStamp, gstrSummaryStatus));

			// Sort the final list by GSTIN
			retList.sort(Comparator.comparing(SupplierImsEntitySummaryResponseDto::getGstin));
			return retList;

		} catch (Exception ex) {
			throw new AppException("Exception occurred while processing IMS summary data", ex);
		}
	}

	private List<SupplierImsEntitySummaryResponseDto> buildSummaryDefaultDtos(List<String> gstins,
			Map<String, String> stateNames, Map<String, String> regMap, Map<String, String> authTokenStatus,
			Map<String, Pair<String, String>> supplierImsPair, List<Object[]> gstrSummaryStatus) {

		List<SupplierImsEntitySummaryResponseDto> defaultDtos = new ArrayList<>();

		for (String gstin : gstins) {
			SupplierImsEntitySummaryResponseDto defaultDto = new SupplierImsEntitySummaryResponseDto();
			defaultDto.setGstin(gstin);
			defaultDto.setState(stateNames.get(gstin));
			defaultDto.setRegType(regMap.get(gstin));
			defaultDto.setAuthTokenStatus("A".equalsIgnoreCase(authTokenStatus.get(gstin)) ? "Active" : "Inactive");
			Pair<String, String> imsPair = supplierImsPair.get(gstin);
			if (imsPair != null) {
				defaultDto.setImsStatus(imsPair.getLeft());
				defaultDto.setImsTimeStamp(imsPair.getRight() != null ? imsPair.getRight() : null);
			}else {
				defaultDto.setImsStatus("NOT INITIATED");
			}

			boolean hasGstr1 = false;
			boolean hasGstr1A = false;

			for (Object[] statusRow : gstrSummaryStatus) {
				String statusGstin = safeString(statusRow[0]);
				String apiSection = safeString(statusRow[1]);
				String status = safeString(statusRow[2]);
				Timestamp createdOn = (Timestamp) statusRow[3];

				if (gstin.equals(statusGstin)) {
					if ("GSTR1".equalsIgnoreCase(apiSection)) {
						defaultDto.setGstr1SummaryStatus(status);
						defaultDto.setGstr1SummaryStatusTimeStamp(formatDateTime(createdOn));
						hasGstr1 = true;
					} else if ("GSTR1A".equalsIgnoreCase(apiSection)) {
						defaultDto.setGstr1ASummaryStatus(status);
						defaultDto.setGstr1ASummaryStatusTimeStamp(formatDateTime(createdOn));
						hasGstr1A = true;
					}
				}
			}
			// Set NOT INITIATED for whichever summary is missing
			if (!hasGstr1) {
				defaultDto.setGstr1SummaryStatus("NOT INITIATED");
			}
			if (!hasGstr1A) {
				defaultDto.setGstr1ASummaryStatus("NOT INITIATED");
			}
			defaultDtos.add(defaultDto);
		}

		return defaultDtos;
	}

	private Map<String, Pair<String, String>> getSupplierImsStatusAndTimeStamp(List<String> gstins,
			String returnPeriod) {

		Map<String, Pair<String, String>> resultMap = new HashMap<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		List<GetAnx1BatchEntity> detailStatuses = GET_TYPES.stream()
				.flatMap(getType -> batchStatusRepository.findStatusesByGstinInAndApiSectionAndGetType(gstins,
						returnPeriod, APIConstants.SUPPLIER_IMS_INVOICE, getType).stream())
				.collect(Collectors.toList());

		LOGGER.debug("Fetched SUPPLIER_IMS_DETAILS statuses: {}", detailStatuses);

		for (String gstin : gstins) {
			List<GetAnx1BatchEntity> gstinDetailStatuses = detailStatuses.stream()
					.filter(s -> gstin.equals(s.getSgstin())).collect(Collectors.toList());

			String detailStatus = determineOverallStatus(gstinDetailStatuses);

			LocalDateTime latestDetailTimestamp = gstinDetailStatuses.stream().map(GetAnx1BatchEntity::getCreatedOn)
					.filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null);

			LocalDateTime istTimestamp = EYDateUtil.toISTDateTimeFromUTC(latestDetailTimestamp);

			String formattedTimestamp = (istTimestamp != null) ? istTimestamp.format(formatter) : null;
			resultMap.put(gstin, new ImmutablePair<>(detailStatus, formattedTimestamp));

			LOGGER.debug("Determined SUPPLIER_IMS_DETAILS status for GSTIN {}: {}, Latest Timestamp: {}", gstin,
					detailStatus, latestDetailTimestamp);
		}

		return resultMap;
	}

	private String determineOverallStatus(List<GetAnx1BatchEntity> statuses) {

		if (statuses == null || statuses.isEmpty()) {
			return SUPPLIER_IMS_STS_NOTINITIATED;
		}

		List<String> distinctStatuses = statuses.stream()
				.map(s -> s.getStatus() != null ? s.getStatus().toUpperCase() : null).filter(Objects::nonNull)
				.distinct().collect(Collectors.toList());

		boolean hasInProgress = distinctStatuses.contains(SUPPLIER_IMS_STS_INPROGRESS);
		boolean hasInitiated = distinctStatuses.contains(SUPPLIER_IMS_STS_INITIATED);
		boolean hasFailed = distinctStatuses.contains(SUPPLIER_IMS_STS_FAILED);
		boolean allFailed = distinctStatuses.size() == 1 && distinctStatuses.contains(SUPPLIER_IMS_STS_FAILED);
		boolean allSuccess = distinctStatuses.size() == 1 && distinctStatuses.contains(SUPPLIER_IMS_STS_SUCCESS);
		boolean allSuccessWithNoData = distinctStatuses.size() == 1
				&& distinctStatuses.contains(SUPPLIER_IMS_STS_SUCCESSNODATA);
		boolean hasSuccessWithNoData = distinctStatuses.contains(SUPPLIER_IMS_STS_SUCCESSNODATA);

		if (hasInProgress) {
			return SUPPLIER_IMS_STS_INPROGRESS;
		}
		if (hasFailed) {
			return allFailed ? SUPPLIER_IMS_STS_FAILED : SUPPLIER_IMS_STS_PARTIALSUCCESS;
		}
		if (hasInitiated) {
			return SUPPLIER_IMS_STS_INITIATED;
		}
		if (allSuccess || allSuccessWithNoData) {
			return SUPPLIER_IMS_STS_SUCCESS;
		}
		if (hasSuccessWithNoData) {
			return SUPPLIER_IMS_STS_SUCCESS;
		}

		return SUPPLIER_IMS_STS_NOTINITIATED;
	}

	private List<String> getFilteredSections(List<String> input, List<String> masterList) {
		if (input == null || input.isEmpty() || input.contains("ALL")) {
			return new ArrayList<>(masterList);
		}
		return input.stream().filter(masterList::contains).collect(Collectors.toList());
	}

	private StoredProcedureQuery buildStoredProcedureQuerySummaryScreen(List<String> gstins, String returnPeriod,
			List<String> sections, List<String> retTypes, String level) {

		StoredProcedureQuery query; // Declare outside

		if (level.equalsIgnoreCase("summaryLevel")) {
			query = entityManager.createStoredProcedureQuery("USP_GET_SUPPLIER_IMS_SUMMARY");
		} else {
			query = entityManager.createStoredProcedureQuery("USP_GET_SUPPLIER_IMS_DETAILS");
		}

		query.registerStoredProcedureParameter("IP_GSTIN", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("IP_TABLE_TYPE", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("IP_RETURN_PERIOD", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("IP_RETURN_TYPE", String.class, ParameterMode.IN);

		query.setParameter("IP_GSTIN", String.join(",", gstins));
		query.setParameter("IP_TABLE_TYPE", String.join(",", sections));
		query.setParameter("IP_RETURN_PERIOD", GenUtil.convertTaxPeriodToInt(returnPeriod).toString());
		query.setParameter("IP_RETURN_TYPE", String.join(",", retTypes));

		return query;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getStoredProcedureResults(StoredProcedureQuery query) {
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getGstr1And1AStatus(List<String> gstins, String taxPeriod) {
		String sql = "SELECT GSTIN,API_SECTION,STATUS,CREATED_ON FROM GETANX1_BATCH_TABLE " + "WHERE GSTIN IN :gstins "
				+ "AND RETURN_PERIOD = :taxPeriod " + "AND API_SECTION IN ('GSTR1', 'GSTR1A') "
				+ "AND GET_TYPE IN ('GSTR1_GETSUM','GSTR1A_GETSUM') " + "AND IS_DELETE = false " + "ORDER BY id DESC";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("gstins", gstins);
		query.setParameter("taxPeriod", taxPeriod);

		return query.getResultList();
	}

	private SupplierImsEntitySummaryResponseDto convertToSummaryDto(Object[] row, Map<String, String> authTokenStatus,
			Map<String, String> regMap, Map<String, String> stateNames,
			Map<String, Pair<String, String>> supplierImsPair, List<Object[]> gstrSummaryStatus) {
		SupplierImsEntitySummaryResponseDto dto = new SupplierImsEntitySummaryResponseDto();

		String gstin = safeString(row[0]);
		dto.setGstin(gstin);
		dto.setAuthTokenStatus("A".equalsIgnoreCase(authTokenStatus.getOrDefault(gstin, "")) ? "Active" : "Inactive");
		dto.setState(stateNames.get(gstin));
		dto.setRegType(regMap.get(gstin));
		Pair<String, String> imsPair = supplierImsPair.get(gstin);
		if (imsPair != null) {
			dto.setImsStatus(imsPair.getLeft());
			dto.setImsTimeStamp(imsPair.getRight() != null ? imsPair.getRight() : null);
		}

		dto.setTotalRecords(getInteger(row[1]));
		dto.setAccepted(getInteger(row[2]));
		dto.setPending(getInteger(row[3]));
		dto.setRejected(getInteger(row[4]));
		dto.setNoAction(getInteger(row[5]));

		dto.setDiffWithGstr1And1A(Boolean.TRUE.equals(row[6]));

		boolean hasGstr1 = false;
		boolean hasGstr1A = false;

		for (Object[] statusRow : gstrSummaryStatus) {
			String statusGstin = safeString(statusRow[0]);
			String apiSection = safeString(statusRow[1]);
			String status = safeString(statusRow[2]);
			Timestamp createdOn = (Timestamp) statusRow[3];

			if (gstin.equals(statusGstin)) {
				if ("GSTR1".equalsIgnoreCase(apiSection)) {
					dto.setGstr1SummaryStatus(status);
					dto.setGstr1SummaryStatusTimeStamp(formatDateTime(createdOn));
					hasGstr1 = true;
				} else if ("GSTR1A".equalsIgnoreCase(apiSection)) {
					dto.setGstr1ASummaryStatus(status);
					dto.setGstr1ASummaryStatusTimeStamp(formatDateTime(createdOn));
					hasGstr1A = true;
				}
			}
		}
		// Set NOT INITIATED for whichever summary is missing
		if (!hasGstr1) {
			dto.setGstr1SummaryStatus("NOT INITIATED");
		}
		if (!hasGstr1A) {
			dto.setGstr1ASummaryStatus("NOT INITIATED");
		}

		return dto;
	}

	private int getInteger(Object obj) {
		return obj instanceof Number ? ((Number) obj).intValue() : 0;
	}

	private String safeString(Object obj) {
		return obj != null ? obj.toString() : "";
	}

	private String formatDateTime(Timestamp timestamp) {
		if (timestamp == null)
			return "-";
		LocalDateTime dateTimeInIst = EYDateUtil.toISTDateTimeFromUTC(timestamp);
		return dateTimeInIst.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	@Override
	public List<SupplierImsEntityDetailsResponseDto> getSupplierImsDetailEntityLvlData(
			SupplierImsEntityReqDto criteria) {
		LOGGER.debug("Processing SUPPLIER_IMS Details Entity Level Data for gstin: {}", criteria);

		try {
			List<String> gstins = criteria.getGstins();
			List<GSTNDetailEntity> regList = gstNDetailRepository.findRegTypeByGstinList(criteria.getGstins());
			Map<String, String> regMap = regList.stream()
					.collect(Collectors.toMap(GSTNDetailEntity::getGstin, GSTNDetailEntity::getRegistrationType));

			Map<String, String> stateNames = entityService.getStateNames(criteria.getGstins());
			Map<String, String> authTokenStatus = authTokenService.getAuthTokenStatusForGstins(gstins);
			LOGGER.debug("Fetched auth token statuses: {}", authTokenStatus);

			List<Object[]> gstrSummaryStatus = getGstr1And1AStatus(gstins, criteria.getReturnPeriod());

			Map<String, Pair<String, String>> supplierImsStatusAndTimeStamp = getSupplierImsStatusAndTimeStamp(
					gstins, criteria.getReturnPeriod());

			List<String> sections = getFilteredSections(criteria.getTableTypes(), GET_TYPES);
			List<String> retTypes = getFilteredSections(criteria.getReturnTypes(), RETURN_TYPE);

			StoredProcedureQuery storedProc = buildStoredProcedureQuerySummaryScreen(gstins, criteria.getReturnPeriod(),
					sections, retTypes, "detailsLevel");
			List<Object[]> resultList = getStoredProcedureResults(storedProc);
			LOGGER.debug("Result list fetched from stored procedure: {}", resultList);

			List<SupplierImsEntityDetailsResponseDto> retList = new ArrayList<>();

			if (resultList.isEmpty()) {
				LOGGER.info("SUPPLIER_IMS Summary Entity Proc No data found. Returning default DTOs.");
			} else {
				LOGGER.debug("SUPPLIER_IMS Summary Entity Proc result is: {}", resultList);
				retList.addAll(
						resultList.stream()
								.map(row -> convertToDetailDto(row, authTokenStatus, regMap, stateNames,
										supplierImsStatusAndTimeStamp, gstrSummaryStatus))
								.collect(Collectors.toList()));

			}

			// Get GSTINs that were returned in the result
			Set<String> resultGstins = retList.stream().map(SupplierImsEntityDetailsResponseDto::getGstin)
					.collect(Collectors.toSet());
			List<String> missingGstins = criteria.getGstins().stream().filter(gstin -> !resultGstins.contains(gstin))
					.collect(Collectors.toList());

			// Create a separate list for default DTOs
			// Add the default DTOs to the retList
			retList.addAll(buildDetailsDefaultDtos(missingGstins, stateNames, regMap, authTokenStatus,
					supplierImsStatusAndTimeStamp, gstrSummaryStatus));

			// Sort the final list by GSTIN
			retList.sort(Comparator.comparing(SupplierImsEntityDetailsResponseDto::getGstin));
			return retList;

		} catch (Exception ex) {
			LOGGER.error("Exception occurred while processing SUPPLIER IMS Details data", ex);
			throw new AppException("Exception occurred while processing SUPPLIER IMS Details data", ex);
		}
	}

	private List<SupplierImsEntityDetailsResponseDto> buildDetailsDefaultDtos(List<String> gstins,
			Map<String, String> stateNames, Map<String, String> regMap, Map<String, String> authTokenStatus,
			Map<String, Pair<String, String>> supplierImsPair, List<Object[]> gstrSummaryStatus) {

		List<SupplierImsEntityDetailsResponseDto> defaultDtos = new ArrayList<>();

		for (String gstin : gstins) {
			SupplierImsEntityDetailsResponseDto defaultDto = new SupplierImsEntityDetailsResponseDto();
			defaultDto.setGstin(gstin);
			defaultDto.setState(stateNames.get(gstin));
			defaultDto.setRegType(regMap.get(gstin));
			defaultDto.setAuthTokenStatus("A".equalsIgnoreCase(authTokenStatus.get(gstin)) ? "Active" : "Inactive");
			Pair<String, String> imsPair = supplierImsPair.get(gstin);
			if (imsPair != null) {
				defaultDto.setGetCountStatus(imsPair.getLeft());
				defaultDto.setGetCountStatusDateTime(imsPair.getRight() != null ? imsPair.getRight() : null);
			}else {
				defaultDto.setGetCountStatus("NOT INITIATED");
			}
			
			defaultDto.setTotalRecords(new SupplierEntityRecordDetail());
			defaultDto.setAcceptedRecords(new SupplierEntityRecordDetail());
			defaultDto.setPendingRecords(new SupplierEntityRecordDetail());
			defaultDto.setRejectedRecords(new SupplierEntityRecordDetail());
			defaultDto.setNoActionRecords(new SupplierEntityRecordDetail());

			boolean hasGstr1 = false;
			boolean hasGstr1A = false;

			for (Object[] statusRow : gstrSummaryStatus) {
				String statusGstin = safeString(statusRow[0]);
				String apiSection = safeString(statusRow[1]);
				String status = safeString(statusRow[2]);
				Timestamp createdOn = (Timestamp) statusRow[3];

				if (gstin.equals(statusGstin)) {
					GstrSummaryStatus summaryStatus = new GstrSummaryStatus();
					summaryStatus.setStatus(status);
					summaryStatus.setDateTime(formatDateTime(createdOn));

					if ("GSTR1".equalsIgnoreCase(apiSection)) {
						defaultDto.setGstr1Summary(summaryStatus);
						hasGstr1 = true;
					} else if ("GSTR1A".equalsIgnoreCase(apiSection)) {
						defaultDto.setGstr1aSummary(summaryStatus);
						hasGstr1A = true;
					}
				}
			}

			if (!hasGstr1) {
				defaultDto.setGstr1Summary(createNotInitiatedStatus());
			}

			if (!hasGstr1A) {
				defaultDto.setGstr1aSummary(createNotInitiatedStatus());
			}

			defaultDtos.add(defaultDto);
		}

		return defaultDtos;
	}

	private GstrSummaryStatus createNotInitiatedStatus() {
		GstrSummaryStatus status = new GstrSummaryStatus();
		status.setStatus("NOT INITIATED");
		return status;
	}

	private SupplierImsEntityDetailsResponseDto convertToDetailDto(Object[] row, Map<String, String> authTokenStatus,
			Map<String, String> regMap, Map<String, String> stateNames,
			Map<String, Pair<String, String>> supplierImsPair, List<Object[]> gstrSummaryStatus) {

		SupplierImsEntityDetailsResponseDto dto = new SupplierImsEntityDetailsResponseDto();
		int index = 0;

		dto.setGstin((String) row[index++]);
		dto.setAuthTokenStatus(
				"A".equalsIgnoreCase(authTokenStatus.getOrDefault(dto.getGstin(), "")) ? "Active" : "Inactive");
		dto.setState(stateNames.get(dto.getGstin().toString()));
		dto.setRegType(regMap.get(dto.getGstin().toString()));

		Pair<String, String> imsPair = supplierImsPair.get(dto.getGstin());
		if (imsPair != null) {
			dto.setGetCountStatus(imsPair.getLeft());
			dto.setGetCountStatusDateTime(imsPair.getRight() != null ? imsPair.getRight() : null);
		}
		dto.setTotalRecords(createRecordDetail(row, 1));
		dto.setAcceptedRecords(createRecordDetail(row, 4));
		dto.setPendingRecords(createRecordDetail(row, 7));
		dto.setRejectedRecords(createRecordDetail(row, 10));
		dto.setNoActionRecords(createRecordDetail(row, 13));

		dto.setDifferenceWithGstr1And1A(Boolean.TRUE.equals(row[16]));
		String gstin = dto.getGstin();
		setGstrSummaryStatus(dto, gstin, gstrSummaryStatus);

		return dto;
	}

	private SupplierEntityRecordDetail createRecordDetail(Object[] row, int index) {
	    Integer count = getInteger(row[index]);
	    BigDecimal taxableValue = getBigDecimal(row[index + 2]);
	    BigDecimal tax = getBigDecimal(row[index + 1]);

	    LOGGER.debug("Index: " + index);

	    SupplierEntityRecordDetail recordDetail = new SupplierEntityRecordDetail();
	    recordDetail.setCount(count);
	    recordDetail.setTotalTaxableValue(taxableValue);
	    recordDetail.setTotalTax(tax);

	    return recordDetail;
	}

	private BigDecimal getBigDecimal(Object obj) {
		return obj != null ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
	}

	private void setGstrSummaryStatus(SupplierImsEntityDetailsResponseDto dto, String gstin,
			List<Object[]> gstrSummaryStatusList) {
		if (gstrSummaryStatusList == null || gstin == null)
			return;

		boolean foundGstr1 = false;
		boolean foundGstr1A = false;

		for (Object[] summary : gstrSummaryStatusList) {
			if (gstin.equals(summary[0])) {
				String type = (String) summary[1];
				String status = (summary[2] != null) ? summary[13].toString() : "";
				String dateTime = (summary[3] != null) ? summary[7].toString() : null;

				GstrSummaryStatus statusObj = new GstrSummaryStatus();
				statusObj.setStatus(status);
				statusObj.setDateTime(dateTime);

				if ("GSTR1".equalsIgnoreCase(type)) {
					dto.setGstr1Summary(statusObj);
					foundGstr1 = true;
				} else if ("GSTR1A".equalsIgnoreCase(type)) {
					dto.setGstr1aSummary(statusObj);
					foundGstr1A = true;
				}
			}
		}
		// Set NOT INITIATED if missing(gstr1 or 1A)
		if (!foundGstr1) {
			GstrSummaryStatus notInitiated = new GstrSummaryStatus();
			notInitiated.setStatus("NOT INITIATED");
			notInitiated.setDateTime(null);
			dto.setGstr1Summary(notInitiated);
		}

		if (!foundGstr1A) {
			GstrSummaryStatus notInitiated = new GstrSummaryStatus();
			notInitiated.setStatus("NOT INITIATED");
			notInitiated.setDateTime(null);
			dto.setGstr1aSummary(notInitiated);
		}
	}

}

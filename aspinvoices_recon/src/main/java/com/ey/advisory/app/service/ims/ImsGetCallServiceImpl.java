package com.ey.advisory.app.service.ims;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Slf4j
@Component("ImsGetCallServiceImpl")
public class ImsGetCallServiceImpl implements ImsGetCallService {

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

	private final List<String> GET_TYPES = Arrays.asList(APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CN, APIConstants.IMS_TYPE_CNA, APIConstants.IMS_TYPE_DN, APIConstants.IMS_TYPE_DNA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);

	private static final String IMS_STS_SUCCESS = "SUCCESS";
	private static final String IMS_STS_SUCCESSNODATA = "SUCCESS_WITH_NO_DATA";
	private static final String IMS_STS_PARTIALSUCCESS = "PARTIAL SUCCESS";
	private static final String IMS_STS_FAILED = "FAILED";
	private static final String IMS_STS_INPROGRESS = "INPROGRESS";
	private static final String IMS_STS_INITIATED = "INITIATED";
	private static final String IMS_STS_NOTINITIATED = "NOT INITIATED";
	private static final String IMS_TAXPERIOD = "000000";

	@Override
	public List<ImsGetCallResponseDto> getImsCallSummary(ImsEntitySummaryReqDto criteria) {

		LOGGER.debug("Processing IMS Entity Summary for criteria: {}", criteria);

		List<ImsGetCallResponseDto> responseList = new ArrayList<>();

		try {
			// Fetch registration types for GSTINs
			List<GSTNDetailEntity> regList = gstNDetailRepository.findRegTypeByGstinList(criteria.getGstins());
			Map<String, String> regMap = regList.stream()
					.collect(Collectors.toMap(GSTNDetailEntity::getGstin, GSTNDetailEntity::getRegistrationType));

			// Fetch state names for GSTINs
			Map<String, String> stateNames = entityService.getStateNames(criteria.getGstins());

			// Fetch auth token statuses for GSTINs
			Map<String, String> authTokenStatus = authTokenService.getAuthTokenStatusForGstins(criteria.getGstins());
			LOGGER.debug("Fetched auth token statuses: {}", authTokenStatus);

			// Fetch IMS_COUNT status
			List<GetAnx1BatchEntity> countStatuses = batchStatusRepository
					.findStatusesByGstinInAndApiSection(criteria.getGstins(), IMS_TAXPERIOD, APIConstants.IMS_COUNT);
			LOGGER.debug("Fetched IMS_COUNT statuses: {}", countStatuses);

			Map<String, String> gstinCountStatusMap = new HashMap<>();
			boolean hasCountStatuses = !CollectionUtils.isEmpty(countStatuses);
			if (hasCountStatuses) {
				for (String gstin : criteria.getGstins()) {
					List<GetAnx1BatchEntity> gstinCountStatuses = countStatuses.stream()
							.filter(s -> gstin.equals(s.getSgstin())).collect(Collectors.toList());

					String countStatus = determineOverallStatus(gstinCountStatuses);
					gstinCountStatusMap.put(gstin, countStatus);
					LOGGER.debug("Determined IMS_COUNT status for GSTIN {}: {}", gstin, countStatus);
				}
			}

			// Fetch IMS_DETAILS status across all GET_TYPES
			Map<String, String> gstinDetailStatusMap = new HashMap<>();
			Map<String, LocalDateTime> gstinLatestTimestampMap = new HashMap<>();
			if (hasCountStatuses) {
				List<GetAnx1BatchEntity> detailStatuses = GET_TYPES.stream()
						.flatMap(getType -> batchStatusRepository.findStatusesByGstinInAndApiSectionAndGetType(
								criteria.getGstins(), IMS_TAXPERIOD, APIConstants.IMS_INVOICE, getType).stream())
						.collect(Collectors.toList());

				LOGGER.debug("Fetched IMS_DETAILS statuses: {}", detailStatuses);
				for (String gstin : criteria.getGstins()) {
					List<GetAnx1BatchEntity> gstinDetailStatuses = detailStatuses.stream()
							.filter(s -> gstin.equals(s.getSgstin())).collect(Collectors.toList());
					String detailStatus = determineOverallStatus(gstinDetailStatuses);
					gstinDetailStatusMap.put(gstin, detailStatus);

					LocalDateTime latestDetailTimestamp = gstinDetailStatuses.stream()
							.map(GetAnx1BatchEntity::getCreatedOn).filter(Objects::nonNull)
							.max(LocalDateTime::compareTo).orElse(null);
					gstinLatestTimestampMap.put(gstin, latestDetailTimestamp);

					LOGGER.debug("Determined IMS_DETAILS status for GSTIN {}: {}, Latest Timestamp: {}", gstin,
							detailStatus, latestDetailTimestamp);
				}
			}

			// Build response list
			LOGGER.debug("Building response list for GSTINs: {}", criteria.getGstins());
			for (String gstin : criteria.getGstins()) {
				ImsGetCallResponseDto dto = new ImsGetCallResponseDto();
				dto.setGstin(gstin);
				dto.setRegType(regMap.get(gstin));
				dto.setState(stateNames.get(gstin));
				dto.setAuthToken(authTokenStatus.get(gstin).equalsIgnoreCase("A") ? "Active" : "Inactive");
				dto.setInvCountStatus(gstinCountStatusMap.getOrDefault(gstin, IMS_STS_NOTINITIATED));
				dto.setInvDetailsStatus(gstinDetailStatusMap.getOrDefault(gstin, IMS_STS_NOTINITIATED));
				dto.setInvCountTimeStamp(hasCountStatuses && countStatuses != null
						? formattedDateOfUpload(countStatuses.stream().filter(s -> gstin.equals(s.getSgstin()))
								.findFirst().map(GetAnx1BatchEntity::getCreatedOn).orElse(null))
						: null);
				dto.setInvDetailsTimeStamp(formattedDateOfUpload(gstinLatestTimestampMap.get(gstin)));
				responseList.add(dto);
			}
			LOGGER.info("Processed {} entity summaries", responseList.size());
		} catch (Exception e) {
			throw new AppException("Error processing IMS GetCallScreenData : {}",e);
		}

		//sort the responseList basis gstin
		responseList.sort(Comparator.comparing(ImsGetCallResponseDto::getGstin));

		return responseList;
	}

	private String determineOverallStatus(List<GetAnx1BatchEntity> statuses) {

		if (statuses == null || statuses.isEmpty()) {
			return IMS_STS_NOTINITIATED;
		}

		List<String> distinctStatuses = statuses.stream()
				.map(s -> s.getStatus() != null ? s.getStatus().toUpperCase() : null).filter(Objects::nonNull)
				.distinct().collect(Collectors.toList());

		boolean hasInProgress = distinctStatuses.contains(IMS_STS_INPROGRESS);
		boolean hasInitiated = distinctStatuses.contains(IMS_STS_INITIATED);
		boolean hasFailed = distinctStatuses.contains(IMS_STS_FAILED);
		boolean allFailed = distinctStatuses.size() == 1 && distinctStatuses.contains(IMS_STS_FAILED);
		boolean allSuccess = distinctStatuses.size() == 1 && distinctStatuses.contains(IMS_STS_SUCCESS);
		boolean allSuccessWithNoData = distinctStatuses.size() == 1 && distinctStatuses.contains(IMS_STS_SUCCESSNODATA);
		boolean hasSuccessWithNoData = distinctStatuses.contains(IMS_STS_SUCCESSNODATA);
		
		if (hasInProgress) {
			return IMS_STS_INPROGRESS;
		}
		if (hasFailed) {
			return allFailed ? IMS_STS_FAILED : IMS_STS_PARTIALSUCCESS;
		}
		if (hasInitiated) {
			return IMS_STS_INITIATED;
		}
		if (allSuccess || allSuccessWithNoData) {
			return IMS_STS_SUCCESS;
		}
		if (hasSuccessWithNoData) {
 			return IMS_STS_SUCCESS;
 		}

		return IMS_STS_NOTINITIATED;
	}

	private String formattedDateOfUpload(LocalDateTime istDateTimeFromUTC) {
		if (istDateTimeFromUTC == null) {
			return null;
		}
		LocalDateTime dateTimeInIst = EYDateUtil.toISTDateTimeFromUTC(istDateTimeFromUTC);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		return dateTimeInIst.format(formatter);
	}

	
	@Override
	public List<ImsGetCallPopUpResponseDto> getImsDetailCallPopupData(String gstin) {

		LOGGER.debug("Processing IMS Detail Call PopUp Data for gstin: {}", gstin);

		if (gstin == null || gstin.isEmpty()) {
			LOGGER.error("GSTIN is null or empty");
			return Collections.emptyList();
		}

		List<ImsGetCallPopUpResponseDto> responseList = new ArrayList<>();
		try {
			List<GetAnx1BatchEntity> batchEntities = batchStatusRepository
					.findBySgstinAndTaxPeriodAndApiSectionAndTypeIn(gstin, IMS_TAXPERIOD, APIConstants.IMS_INVOICE,
							GET_TYPES);
			Map<String, GetAnx1BatchEntity> batchEntityMap = batchEntities.stream()
					.collect(Collectors.toMap(GetAnx1BatchEntity::getType, entity -> entity));

			for (String type : GET_TYPES) {
				GetAnx1BatchEntity entity = batchEntityMap.get(type);
				responseList.add(createImsGetCallPopUpResponseItem(type, entity));
			}
		} catch (Exception e) {
			throw new AppException("Error processing IMS Detail Call PopUp GSTIN: {}",e);
		}

		return responseList;
	}

	private ImsGetCallPopUpResponseDto createImsGetCallPopUpResponseItem(String type, GetAnx1BatchEntity entity) {
		ImsGetCallPopUpResponseDto item = new ImsGetCallPopUpResponseDto();
		item.setType(type);
		item.setStatus(entity.getStatus() != null ? entity.getStatus() : null);
		item.setTimeStamp(entity.getCreatedOn() != null ? formattedDateOfUpload(entity.getCreatedOn()) : null);
		return item;
	}

}

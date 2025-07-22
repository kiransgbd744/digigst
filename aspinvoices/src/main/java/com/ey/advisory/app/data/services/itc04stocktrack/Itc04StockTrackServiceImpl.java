package com.ey.advisory.app.data.services.itc04stocktrack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04StockTrackingComputeEntity;
import com.ey.advisory.app.data.repositories.client.Itc04DocRepository;
import com.ey.advisory.app.data.repositories.client.Itc04StockTrackCompRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Itc04StockTrackServiceImpl")
public class Itc04StockTrackServiceImpl implements Itc04StockTrackService {

	@Autowired
	@Qualifier("Itc04DocRepository")
	private Itc04DocRepository itc04DocRepository;

	@Autowired
	@Qualifier("Itc04StockTrackCompRepository")
	private Itc04StockTrackCompRepository itc04StockTrackCmpRepo;

	@Autowired
	DefaultStateCache defaultStateCache;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final ImmutableMap<String, String> immutableRetMap = ImmutableMap
			.<String, String>builder().put("Q1", "13").put("Q2", "14")
			.put("Q3", "15").put("Q4", "16").put("H1", "17").put("H2", "18")
			.build();

	@Override
	public ResponseEntity<String> getScreenDetails(ITC04RequestDto reqDto) {
		List<Itc04HeaderEntity> activeRecords = new ArrayList<>();
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		Itc04StockTrackingFinalDto response = new Itc04StockTrackingFinalDto();
		List<Itc04StockTrackingComputeEntity> initiateReportRecords = new ArrayList<>();
		try {
			reqDto = processedRecordsCommonSecParam
					.setItc04DataSecuritySearchParams(reqDto);
			List<String> gstinList = reqDto.getDataSecAttrs().getOrDefault(
					OnboardingConstant.GSTIN, Collections.emptyList());
			if (reqDto.getRequestType().equalsIgnoreCase("ChallanDate")
					&& !Strings.isNullOrEmpty(reqDto.getToChallanDate())
					&& !Strings.isNullOrEmpty(reqDto.getToChallanDate())) {
				activeRecords = itc04DocRepository.findActiveDocsByChallanDate(
						DateUtil.tryConvertUsingFormat(
								reqDto.getFromChallanDate(),
								DateUtil.SUPPORTED_DATE_FORMAT1),
						DateUtil.tryConvertUsingFormat(
								reqDto.getToChallanDate(),
								DateUtil.SUPPORTED_DATE_FORMAT1),
						reqDto.getFy(), gstinList);

				initiateReportRecords = itc04StockTrackCmpRepo
						.findActiveDocsByChallanDate(
								DateUtil.tryConvertUsingFormat(
										reqDto.getFromChallanDate(),
										DateUtil.SUPPORTED_DATE_FORMAT1),
								DateUtil.tryConvertUsingFormat(
										reqDto.getToChallanDate(),
										DateUtil.SUPPORTED_DATE_FORMAT1),
								reqDto.getFy(), gstinList);
				response = mapToResponseItem(activeRecords, gstinList,
						initiateReportRecords);
			} else if (reqDto.getRequestType().equalsIgnoreCase("ReturnPeriod")
					&& !Strings.isNullOrEmpty(reqDto.getFromReturnPeriod())
					&& !Strings.isNullOrEmpty(reqDto.getToReturnPeriod())) {
				String fromRetPeriod = reqDto.getFy().substring(0, 4)
						+ immutableRetMap.get(reqDto.getFromReturnPeriod());

				String toRetPeriod = reqDto.getFy().substring(0, 4)
						+ immutableRetMap.get(reqDto.getToReturnPeriod());

				activeRecords = itc04DocRepository.findActiveDocsByRetPeriod(
						fromRetPeriod, toRetPeriod, reqDto.getFy(), gstinList);
				initiateReportRecords = itc04StockTrackCmpRepo
						.findActiveDocsByReturnPeriod(fromRetPeriod,
								toRetPeriod, reqDto.getFy(), gstinList);
				response = mapToResponseItem(activeRecords, gstinList,
						initiateReportRecords);
			} else {
				response = mapToResponseItem(activeRecords, gstinList,
						initiateReportRecords);
			}

			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", gson.toJsonTree(response.getResp()));
			jsonObj.add("totalCnt", gson.toJsonTree(response.getTotalCnt()));
			if (!Strings.isNullOrEmpty(response.getErrMsg())) {
				jsonObj.addProperty("errMsg", response.getErrMsg());
			}
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while Retriving the ITC04 Screen Data. ",
					e);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", "Unable to retrieve the Data.");
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	private Itc04StockTrackingFinalDto mapToResponseItem(
			List<Itc04HeaderEntity> headerEntities, List<String> gstinList,
			List<Itc04StockTrackingComputeEntity> initiateRecords) {
		List<Itc04StockTrackingRespDto> respList = new ArrayList<>();
		Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
				.getGstnRegMap();
		Itc04StockTrackingFinalDto finalDto = new Itc04StockTrackingFinalDto();

		if (gstinList.isEmpty()) {
			finalDto.setErrMsg("No Data Available.");
			finalDto.setResp(respList);
			finalDto.setTotalCnt(calculateTotalCnt(respList));
			return finalDto;
		}

		if (headerEntities.isEmpty()) {
			createResponseItemsForEmptyRecords(respList, gstinList, gstnRegMap);
		} else {
			createResponseItemsForNonEmptyRecords(respList, gstinList,
					gstnRegMap, headerEntities, initiateRecords);
		}

		finalDto.setResp(respList);
		finalDto.setTotalCnt(calculateTotalCnt(respList));
		return finalDto;
	}

	private void createResponseItemsForEmptyRecords(
			List<Itc04StockTrackingRespDto> respList, List<String> gstinList,
			Pair<Map<String, String>, Map<String, String>> gstnRegMap) {
		for (String gstin : gstinList) {
			Itc04StockTrackingRespDto responseItem = createResponseItemForGstin(
					gstin, gstnRegMap);
			respList.add(responseItem);
		}
	}

	private void createResponseItemsForNonEmptyRecords(
			List<Itc04StockTrackingRespDto> respList, List<String> gstinList,
			Pair<Map<String, String>, Map<String, String>> gstnRegMap,
			List<Itc04HeaderEntity> headerEntities,
			List<Itc04StockTrackingComputeEntity> initiateRecord) {
		Map<String, Map<String, Long>> challanCounts = getChallanCountByGstinAndTableM(
				headerEntities);

		Map<String, ComputeCntDto> countOpenChallanByGstin = countOpenChallanByGstin(
				initiateRecord);

		for (String gstin : gstinList) {
			Itc04StockTrackingRespDto responseItem = createResponseItemForGstin(
					gstin, gstnRegMap);
			if (challanCounts.containsKey(gstin)) {
				responseItem.setMftojwcnt(challanCounts.get(gstin)
						.getOrDefault("4", 0L).intValue());
				responseItem.setJwtomfcnt(challanCounts.get(gstin)
						.getOrDefault("5A", 0L).intValue());
				responseItem.setJwtojwcnt(challanCounts.get(gstin)
						.getOrDefault("5B", 0L).intValue());
				responseItem.setSoldjwcnt(challanCounts.get(gstin)
						.getOrDefault("5C", 0L).intValue());
			}
			if (!initiateRecord.isEmpty()
					&& countOpenChallanByGstin.containsKey(gstin)) {
				responseItem.setIgOpenChallanGrYear(countOpenChallanByGstin
						.get(gstin).getOpenChallanGr365IG());
				responseItem.setCgOpenChallanGrYear(countOpenChallanByGstin
						.get(gstin).getOpenChallanGr365CG());
				responseItem.setIgOpenChallanLsYear(countOpenChallanByGstin
						.get(gstin).getOpenChallanLs365IG());
				responseItem.setCgOpenChallanLsYear(countOpenChallanByGstin
						.get(gstin).getOpenChallanLs365CG());
				responseItem.setReportStatus(
						countOpenChallanByGstin.get(gstin).getReportStatus());
			}
			respList.add(responseItem);
		}
	}

	private Itc04StockTrackingRespDto createResponseItemForGstin(String gstin,
			Pair<Map<String, String>, Map<String, String>> gstnRegMap) {
		Itc04StockTrackingRespDto responseItem = new Itc04StockTrackingRespDto();
		responseItem.setGstin(gstin);
		responseItem.setAuthToken(getAuthToken(gstin, gstnRegMap));
		responseItem.setState(
				defaultStateCache.getStateName(gstin.substring(0, 2)));
		responseItem.setRegType(getRegType(gstin, gstnRegMap));
		responseItem.setMftojwcnt(0);
		responseItem.setJwtomfcnt(0);
		responseItem.setJwtojwcnt(0);
		responseItem.setSoldjwcnt(0);
		responseItem.setCgOpenChallanGrYear(0);
		responseItem.setIgOpenChallanGrYear(0);
		responseItem.setCgOpenChallanLsYear(0);
		responseItem.setIgOpenChallanLsYear(0);
		responseItem.setReportStatus(APIConstants.NOT_INITIATED);

		return responseItem;
	}

	private Map<String, Map<String, Long>> getChallanCountByGstinAndTableM(
			List<Itc04HeaderEntity> entities) {
		return entities.stream().collect(Collectors.groupingBy(
				Itc04HeaderEntity::getSupplierGstin,
				Collectors.groupingBy(entity -> entity.getTableNumber(),
						Collectors.mapping(
								entity -> entity.getTableNumber().equals("5C")
										? entity.getInvNumber()
										: entity.getDeliveryChallanaNumber(),
								Collectors.counting()))));
	}

	private Map<String, ComputeCntDto> countOpenChallanByGstin(
			List<Itc04StockTrackingComputeEntity> entities) {

		if (entities.isEmpty()) {
			Map<String, ComputeCntDto> defaultDto = new HashMap<>();
			defaultDto.put(null, new ComputeCntDto(0, 0, 0, 0,
					APIConstants.NOT_INITIATED, null));
			return defaultDto;
		} else {
			return entities.stream().collect(Collectors.groupingBy(
					Itc04StockTrackingComputeEntity::getGstin,
					Collectors.collectingAndThen(Collectors.toList(), list -> {
						ComputeCntDto dto = new ComputeCntDto();
						list.forEach(entity -> {
							dto.setOpenChallanGr365IG(
									entity.getOpenChallanGr365IG() == null ? 0
											: entity.getOpenChallanGr365IG());
							dto.setOpenChallanGr365CG(
									entity.getOpenChallanGr1095CG() == null ? 0
											: entity.getOpenChallanGr1095CG());
							dto.setOpenChallanLs365IG(
									entity.getOpenChallanLs365IG() == null ? 0
											: entity.getOpenChallanLs365IG());
							dto.setOpenChallanLs365CG(
									entity.getOpenChallanLs1095CG() == null ? 0
											: entity.getOpenChallanLs1095CG());
							dto.setReportStatus(Strings
									.isNullOrEmpty(entity.getReportStatus())
											? APIConstants.NOT_INITIATED
											: entity.getReportStatus());
							dto.setInitiatedOn(
									EYDateUtil.fmtDate(entity.getCreatedOn()));
						});
						return dto;
					})));
		}
	}

	private TotalCntDto calculateTotalCnt(
			List<Itc04StockTrackingRespDto> respList) {

		if (respList == null || respList.isEmpty()) {
			return new TotalCntDto(0, 0, 0, 0);
		} else {
			int totmftojwcnt = respList.stream()
					.mapToInt(Itc04StockTrackingRespDto::getMftojwcnt).sum();
			int totjwtomfcnt = respList.stream()
					.mapToInt(Itc04StockTrackingRespDto::getJwtomfcnt).sum();
			int totjwtojwcnt = respList.stream()
					.mapToInt(Itc04StockTrackingRespDto::getJwtojwcnt).sum();
			int totsoldjwcnt = respList.stream()
					.mapToInt(Itc04StockTrackingRespDto::getSoldjwcnt).sum();
			return new TotalCntDto(totmftojwcnt, totjwtomfcnt, totjwtojwcnt,
					totsoldjwcnt);
		}
	}

	private String getAuthToken(String gstin,
			Pair<Map<String, String>, Map<String, String>> gstnRegMap) {
		return gstnRegMap.getValue0().getOrDefault(gstin, "Inactive");
	}

	private String getRegType(String gstin,
			Pair<Map<String, String>, Map<String, String>> gstnRegMap) {
		return gstnRegMap.getValue1().getOrDefault(gstin, "").toUpperCase();
	}

	@Override
	public ResponseEntity<String> triggerInitiateReport(
			ITC04RequestDto reqDto) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			postComputeJob(reqDto);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.addProperty("statusMsg",
					"Compute has been initiated successfully.");
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("errMsg",
					"Compute has been Failed, Please initiate again.");
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	private void postComputeJob(ITC04RequestDto reqDto) {

		JsonArray idsArray = new JsonArray();
		try {
			List<String> gstinList = reqDto.getDataSecAttrs().getOrDefault(
					OnboardingConstant.GSTIN, Collections.emptyList());
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";

			List<Long> ids = new ArrayList<>();
			for (String gstin : gstinList) {
				Itc04StockTrackingComputeEntity comp = new Itc04StockTrackingComputeEntity();
				comp.setGstin(gstin);
				comp.setFy(reqDto.getFy());

				if (reqDto.getRequestType().equalsIgnoreCase("ChallanDate")
						&& !Strings.isNullOrEmpty(reqDto.getToChallanDate())
						&& !Strings.isNullOrEmpty(reqDto.getToChallanDate())) {
					comp.setChallanFromDate(DateUtil.tryConvertUsingFormat(
							reqDto.getFromChallanDate(),
							DateUtil.SUPPORTED_DATE_FORMAT1));
					comp.setChallanToDate(DateUtil.tryConvertUsingFormat(
							reqDto.getToChallanDate(),
							DateUtil.SUPPORTED_DATE_FORMAT1));
				} else if (reqDto.getRequestType()
						.equalsIgnoreCase("ReturnPeriod")
						&& !Strings.isNullOrEmpty(reqDto.getFromReturnPeriod())
						&& !Strings.isNullOrEmpty(reqDto.getToReturnPeriod())) {
					comp.setFromRetPeriod(
							reqDto.getFy().substring(0, 4) + immutableRetMap
									.get(reqDto.getFromReturnPeriod()));
					comp.setToRetPeriod(reqDto.getFy().substring(0, 4)
							+ immutableRetMap.get(reqDto.getToReturnPeriod()));
				} else {
					String errMsg = "Invalid Filters.";
					LOGGER.error(errMsg);
					throw new AppException(errMsg);
				}
				comp.setIsActive(true);
				comp.setReportStatus(APIConstants.INITIATED);
				comp.setRequestType(reqDto.getRequestType());
				comp.setCreatedBy(userName);
				comp.setCreatedOn(LocalDateTime.now());
				itc04StockTrackCmpRepo.save(comp);
				ids.add(comp.getId());
				idsArray.add(comp.getId());
			}

			if (!idsArray.isEmpty()) {
				JsonObject jobParamsObj = new JsonObject();
				jobParamsObj.add("ids", idsArray);

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.ITC04_STOCK_TRACKING_INITIATE_REPORT,
						jobParamsObj.toString(), "SYSTEM", 1L, null, null);
			}

		} catch (Exception ex) {
			String msg = "Exception occured while initiating the compute.";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	@Override
	public void computeInitiateReport(List<Long> ids) {
		try {
			for (Long id : ids) {
				try {
					StoredProcedureQuery reportDataProc = entityManager
							.createStoredProcedureQuery(
									"USP_ITC04_STOCK_TRACKING_SCREEN");
					reportDataProc.registerStoredProcedureParameter(
							"P_COMPUTE_ID", Long.class, ParameterMode.IN);
					reportDataProc.setParameter("P_COMPUTE_ID", id);

					String procStatus = (String) reportDataProc
							.getSingleResult();
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Executed Stored proc to get  and "
										+ "got resultset of size: %d",
								procStatus);
						LOGGER.debug(msg);
					}
					if (procStatus.equalsIgnoreCase(APIConstants.SUCCESS)) {
						itc04StockTrackCmpRepo.updateReportStatus(
								Arrays.asList(id), APIConstants.SUCCESS);
					} else {
						itc04StockTrackCmpRepo.updateReportStatus(
								Arrays.asList(id), APIConstants.FAILED);
					}
				} catch (Exception e) {
					String errMsg = String.format(
							"Exception while executing the Proc for id %s", id);
					LOGGER.error(errMsg, e);
					itc04StockTrackCmpRepo.updateReportStatus(Arrays.asList(id),
							APIConstants.FAILED);
				}
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while triggering the compute for groupCode %s",
					TenantContext.getTenantId());
			LOGGER.error(msg);
			throw new AppException(msg, e);
		}
	}
}

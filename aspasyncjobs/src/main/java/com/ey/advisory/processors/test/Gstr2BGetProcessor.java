package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2bAutoCommEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bAutoCommRepository;
import com.ey.advisory.app.gstr2b.Gstr2BGetReqDto;
import com.ey.advisory.app.gstr2b.Gstr2BbatchUtil;
import com.ey.advisory.app.gstr2b.Gstr2bGetInvoiceReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2BGetProcessor")
public class Gstr2BGetProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Autowired
	@Qualifier("Gstr2BbatchUtil")
	private Gstr2BbatchUtil batchUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@Autowired
	private Gstr2bAutoCommRepository gstr2bAutoCommRepo;

	private static final List<String> INP_STATUS = ImmutableList
			.of(APIConstants.INPROGRESS, APIConstants.INITIATED);

	private Map<String, Object> syncMap = new ConcurrentHashMap<>();

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	private static 	DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	@Override
	public void execute(Message message, AppExecContext context) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2B Get Call Job"
							+ " executing for groupcode {} and params {}",
					groupCode, jsonString);
		}
		JsonArray gstins = new JsonArray();
		JsonArray taxPeriods = new JsonArray();
		Gson googleJson = new Gson();
		List<String> gstnsList = null;
		List<String> months = null;
		List<String> taxPeriodList = null;
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			Gstr2BGetReqDto reqDto = gson.fromJson(requestObject,
					Gstr2BGetReqDto.class);

			if ((requestObject.has("gstins"))
					&& (requestObject.getAsJsonArray("gstins").size() > 0)) {
				gstins = requestObject.getAsJsonArray("gstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstnsList = googleJson.fromJson(gstins, listType);
			}

			String fy = requestObject.has("fy")
					? requestObject.get("fy").getAsString() : null;

			if ((requestObject.has("month"))
					&& (requestObject.getAsJsonArray("month").size() > 0)) {
				taxPeriods = requestObject.getAsJsonArray("month");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				months = googleJson.fromJson(taxPeriods, listType);
				taxPeriodList = months.stream().map(o -> getRetPeriod(o, fy))
						.collect(Collectors.toList());
				LOGGER.error("taxPeriodList1 {} ", taxPeriodList);
			}

			if (requestObject.has("fromTaxPeriod")
					&& (requestObject.has("toTaxPeriod"))) {
				String fromTaxPeriod = requestObject.get("fromTaxPeriod")
						.getAsString();
				String toTaxPeriod = requestObject.get("toTaxPeriod")
						.getAsString();
				taxPeriodList = GenUtil.deriveTaxPeriodsGivenFromAndToPeriod(
						fromTaxPeriod, toTaxPeriod);
				LOGGER.error("taxPeriodList2 {} ", taxPeriodList);
			}
			List<String> activeGstins = getAllActiveGstnList(gstnsList);

			if (activeGstins.isEmpty()) {
				LOGGER.error(
						"All the Selected gstins are inactive,"
								+ " Hence not initiating GET 2B for JobId {}",
						message.getId());
				return;
			}
			List<Pair<String, String>> activePairs = new ArrayList<>();
			for (String gstin : gstnsList) {
				for (String taxPeriod : taxPeriodList) {
					if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod))
						activePairs.add(new Pair<>(gstin, taxPeriod));
					else {
						LOGGER.warn(
								"Requested TaxPeriod {} is Future TaxPeriod, "
										+ "Hence skipping this Get2B ",
								taxPeriod);
					}
				}

			}

			boolean isAutoEligible = reqDto.isAutoEligible();
			String successHandler = isAutoEligible
					? "Gstr2BGetAutoSuccessHandler" : "Gstr2BGetSuccessHandler";
			String failureHandler = isAutoEligible
					? "Gstr2BGetAutoFailureHandler" : "GSTR2BFailureHandler";
			for (Pair<String, String> pair : activePairs) {
				initiateGetCall(pair.getValue0(), pair.getValue1(),
						isAutoEligible, successHandler, failureHandler);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while initiating 2B Get call", ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private void saveOrUpdateAutoGetStatus(String gstin, String taxPeriod) {

		Gstr2bAutoCommEntity gstr2nAutoGetStatus = gstr2bAutoCommRepo
				.findByGstinAndTaxPeriodAndReturnTypeAndSection(gstin,
						taxPeriod, APIConstants.GSTR2B,
						APIConstants.GSTR2B_GET_ALL);

		Integer derivedTaxPeriod = Integer.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

		if (gstr2nAutoGetStatus == null) {
			gstr2nAutoGetStatus = new Gstr2bAutoCommEntity();
			gstr2nAutoGetStatus.setGstin(gstin);
			gstr2nAutoGetStatus.setReturnType(APIConstants.GSTR2B);
			gstr2nAutoGetStatus.setTaxPeriod(taxPeriod);
			gstr2nAutoGetStatus.setSection(APIConstants.GSTR2B_GET_ALL);
			gstr2nAutoGetStatus.setDerivedTaxPeriod(derivedTaxPeriod);
			gstr2nAutoGetStatus.setCreatedOn(LocalDateTime.now());
			gstr2nAutoGetStatus.setStatus(APIConstants.INITIATED);
			gstr2bAutoCommRepo.save(gstr2nAutoGetStatus);
		} else {
			gstr2bAutoCommRepo.updateGst2bAutoGetCallStatus(
					APIConstants.INITIATED, LocalDateTime.now(), null, gstin,
					taxPeriod, APIConstants.GSTR2B,
					APIConstants.GSTR2B_GET_ALL);
		}

	}

	// cheking authToken for active Gstins
	private List<String> getAllActiveGstnList(List<String> allGstins) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Before Filtering GSTNs {} for GSTR2B GET", allGstins);
		}
		Map<String, String> authStatusMap = authTokenService
				.getAuthTokenStatusForGstins(allGstins);
		List<String> activeGstins = authStatusMap.entrySet().stream()
				.filter(e -> "A".equalsIgnoreCase(e.getValue()))
				.map(Entry::getKey).collect(Collectors.toList());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("After Filtering Active GSTNs {} for GSTR2B GET",
					allGstins);
		}
		return activeGstins;

	}

	private void saveOrUpdateGstnGetStatus(String gstin, String taxPeriod) {

		GstnGetStatusEntity entity = gstnStatusRepo
				.findByGstinAndTaxPeriodAndReturnTypeAndSection(gstin,
						taxPeriod, APIConstants.GSTR2B,
						APIConstants.GSTR2B_GET_ALL);

		if (entity == null) {
			entity = new GstnGetStatusEntity();
			entity.setGstin(gstin);
			entity.setTaxPeriod(taxPeriod);
			entity.setDerivedTaxPeriod(
					GenUtil.convertTaxPeriodToInt(taxPeriod));
			entity.setCreatedOn(LocalDateTime.now());
			entity.setUpdatedOn(LocalDateTime.now());
			entity.setReturnType(APIConstants.GSTR2B);
			entity.setSection(APIConstants.GSTR2B_GET_ALL);
			entity.setUpdatedOn(LocalDateTime.now());
			entity.setStatus(APIConstants.INITIATED);
			gstnStatusRepo.save(entity);
		} else {
			gstnStatusRepo.updateStatus(gstin, taxPeriod, APIConstants.GSTR2B,
					APIConstants.INITIATED, null, null, LocalDateTime.now());

		}

	}

	private void initiateGetCall(String gstin, String taxPeriod,
			boolean isAutoEligible, String successHandler,
			String failureHandler) {
		GetAnx1BatchEntity batch = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr2bGetInvoiceReqDto dto = new Gstr2bGetInvoiceReqDto();
		dto.setGstin(gstin);
		dto.setReturnPeriod(taxPeriod);
		dto.setGroupcode(TenantContext.getTenantId());
		try {
			String syncKey = String.format("%s-%s", gstin, taxPeriod);

			syncMap.putIfAbsent(syncKey, new Object());
			Object syncObj = syncMap.get(syncKey);

			synchronized (syncObj) {
				Optional<GetAnx1BatchEntity> existingBatch = batchRepo
						.findByTypeAndApiSectionAndSgstinAndTaxPeriodAndStatusInAndIsDeleteFalse(
								APIConstants.GSTR2B_GET_ALL,
								APIConstants.GSTR2B, gstin, taxPeriod,
								INP_STATUS);
				if (existingBatch.isPresent()) {
					LOGGER.warn(
							"Request is already INPROGRESS for gstin{} "
									+ "and taxPeriod{}, Hence skipping "
									+ "this Gstin and taxPeriod ",
							gstin, taxPeriod);
					return;
				}
				if (isAutoEligible) {
					saveOrUpdateAutoGetStatus(gstin, taxPeriod);
					saveOrUpdateGstnGetStatus(gstin, taxPeriod);
				} else {
					saveOrUpdateGstnGetStatus(gstin, taxPeriod);
				}
				batch = batchUtil.makeBatchGstr2B(dto);
			}

			batchRepo.softlyDelete(APIConstants.GSTR2B_GET_ALL,
					APIConstants.GSTR2B, dto.getGstin(), dto.getReturnPeriod());
			// Save new Batch
			batchRepo.save(batch);
			dto.setBatchId(batch.getId());
			String queryStr = "SELECT top 1 TO_CHAR(GENDT, 'DD-MM-YYYY'), checksum FROM  "
					+ " GETGSTR2B_ITC_ITM WHERE RGSTIN = :gstin AND "
					+ " TAX_PERIOD = :taxPeriod AND IS_DELETE = false ";
			Query query = entityManager.createNativeQuery(queryStr);
			query.setParameter("gstin", gstin);
			query.setParameter("taxPeriod", taxPeriod);

			List<Object[]> queryResultList = query.getResultList();

			if (!queryResultList.isEmpty()) {
				// Check for null values

				Object[] queryResult = queryResultList.get(0);

				String maxGenDt = queryResult[0] != null
						? (String) queryResult[0] : null;
				dto.setPrevCheckSum(queryResult[1] != null
						? (String) queryResult[1] : null);
			
				LocalDate parsedDate = null;

				if (maxGenDt != null) {
					try {
						parsedDate = LocalDate.parse(maxGenDt, formatter);
					} catch (DateTimeParseException e) {
						LOGGER.error("error while parsing the gendate", e);
					}
				}

				// Set values in DTO with null checks
				dto.setPrevGenDt(
						(parsedDate != null) ? parsedDate.format(formatter) : null);
			}
			String ctxParam = gson.toJson(dto);
			APIParam param1 = new APIParam(APIConstants.GSTR2B_GSTIN, gstin);
			APIParam param2 = new APIParam(APIConstants.GSTR2B_TaxPERIOD,
					taxPeriod);
			APIParam returnPeriodParam = new APIParam("ret_period", taxPeriod);
			APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
					"GSTR2B");

			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR2B_GET, param1,
					param2, returnPeriodParam, returnTypeParam);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Before invoking apiInvoker.invoke() "
								+ "method {} params {}, ctxParam {}",
						params.toString(), ctxParam);

			APIInvocationResult result = apiInvoker.invoke(params, null,
					successHandler, failureHandler, ctxParam);

			if (!isAutoEligible) {
				gstnStatusRepo.updateStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.INPROGRESS, null,
						null, LocalDateTime.now());
			} else {
				gstr2bAutoCommRepo.updateGst2bAutoGetCallStatus(
						APIConstants.INPROGRESS, LocalDateTime.now(), null,
						gstin, taxPeriod, APIConstants.GSTR2B,
						APIConstants.GSTR2B_GET_ALL);
				gstnStatusRepo.updateStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.INPROGRESS, null,
						null, LocalDateTime.now());
			}
			batch.setStatus(APIConstants.INPROGRESS);
			batch.setRequestId(result.getTransactionId());
			batchRepo.save(batch);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Gstr2B GET successfully initiated for Gstin {}, "
								+ "TaxPeriod {} : transctionId %s {} ",
						gstin, taxPeriod, result.getTransactionId());

		} catch (Exception ex) {
			LOGGER.error(
					"Error while invocking Gstr2BGetProcessor, invoke() method",
					ex);
			if (batch != null) {
				batchUtil.updateById(batch.getId(), APIConstants.FAILED,
						batch.getErrorCode(), batch.getErrorDesc(),
						batch.isTokenResponse(), batch.getUserRequestId());

				if (isAutoEligible) {
					gstr2bAutoCommRepo.updateGstr2bAutoStatus(batch.getSgstin(),
							batch.getTaxPeriod(), APIConstants.GSTR2B,
							APIConstants.FAILED, ex.getMessage());
					gstnStatusRepo.updateStatus(batch.getSgstin(),
							batch.getTaxPeriod(), APIConstants.GSTR2B,
							APIConstants.FAILED, null, null,
							LocalDateTime.now());
				} else {
					gstnStatusRepo.updateStatus(batch.getSgstin(),
							batch.getTaxPeriod(), APIConstants.GSTR2B,
							APIConstants.FAILED, null, null,
							LocalDateTime.now());
				}
			}
		}

	}

	String getRetPeriod(String month, String finYear) {
		if (!StringUtils.isEmpty(month)) {
			if (month.equals("01") || month.equals("02")
					|| month.equals("03")) {
				month = month + finYear.substring(0, 2)
						+ finYear.substring(5, 7);
			} else {
				month = month + finYear.substring(0, 4);
			}
		}
		return month;
	}

}

package com.ey.advisory.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.VendorGstinFilingTypeEntity;
import com.ey.advisory.app.data.entities.client.VendorReturnTypeEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.VendorGstinFilingTypeRepository;
import com.ey.advisory.app.data.repositories.client.VendorReturnTypeRepository;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.ApiCallLimitExceededException;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.InvalidAPIResponseException;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.PublicApiEndPointResolver;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Component
public class GstnReturnFilingStatus {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnStatusRepo;

	@Autowired
	@Qualifier("VendorReturnTypeRepository")
	private VendorReturnTypeRepository vendorReturnTypeRepo;

	@Autowired
	@Qualifier("VendorGstinFilingTypeRepository")
	private VendorGstinFilingTypeRepository vendorGstinFilingTypeRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	@Autowired
	private PublicApiEndPointResolver endPointResolver;

	public List<ReturnFilingGstnResponseDto> callGstnApi(List<String> gstins,
			String finYear, boolean isCounterParty) {

		List<Pair<String, String>> combinationsList = new ArrayList<>();

		List<ReturnFilingGstnResponseDto> respDtoList = new ArrayList<>();
		try {
			for (String gstin : gstins)
				combinationsList.add(new Pair<>(gstin, finYear));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to Fetch Status for Combinations {}",
						combinationsList);
			}

			combinationsList.forEach(item -> {
				APIResponse resp = getReturnFillingDetails(item.getValue0(),
						item.getValue1());
				if (resp.isSuccess()) {
					populateReturnFillingDto(item, resp.getResponse(),
							respDtoList, isCounterParty);
				} else {
					if (isCounterParty) {
						if ("RET13510".equals(resp.getError().getErrorCode())) {
							Set<String> distinctReturnTypes = new HashSet<>();
							distinctReturnTypes.add("GSTR1");
							distinctReturnTypes.add("GSTR3B");
							distinctReturnTypes.add("ITC04");
							distinctReturnTypes.add("GSTR9");
							distinctReturnTypes.add("GSTR1A");
							stampVendorGstinWithReturnType(item.getValue0(),
									distinctReturnTypes);
						}
						if ("LG9002".equals(resp.getError().getErrorCode())) {
							savingInvalidGstins(item.getValue0(),
									resp.getError().getErrorDesc());
						}
					}
					String errMsg = String.format(
							"Received Error Response %s"
									+ " for combination %s",
							resp.getErrors(), item);
					LOGGER.error(errMsg);

					ReturnFilingGstnResponseDto errorDto = new ReturnFilingGstnResponseDto();
					errorDto.setGstin(item.getValue0());
					errorDto.setErrCode(resp.getErrors().get(0).getErrorCode());
					errorDto.setErrMsg(resp.getErrors().get(0).getErrorDesc());
					respDtoList.add(errorDto);

				}
			});

		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching the Return Filling Status",
					ex);
			throw new AppException(ex);
		}
		return respDtoList;
	}

	private APIResponse getReturnFillingDetails(String gstin, String finYear) {

		endPointResolver.resolveEndPoint(PublicApiConstants.RETURNS);

		PublicApiContext.setContextMap(PublicApiConstants.GSTIN, gstin);
		PublicApiContext.setContextMap(PublicApiConstants.FY, finYear);

		APIParam param1 = new APIParam("gstin", gstin);
		APIParam param2 = new APIParam("fy", finYear);
		APIParam param3 = new APIParam("type", null);
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.GET_FILLING_STATUS, param1,
				param2, param3);
		try {
			return apiExecutor.execute(params, null);
		} catch (ApiCallLimitExceededException e) {
			APIResponse errResp = new APIResponse();
			return errResp.addError(new APIError("ER-1000", e.getMessage()));
		} catch (InvalidAPIResponseException e) {
			APIResponse errResp = new APIResponse();
			return errResp.addError(new APIError("ER-2000", e.getMessage()));
		} catch (Exception e) {
			LOGGER.error(
					"Exception while Fetching the Return Filling Status Gstin {}, FY {}",
					gstin, finYear, e);
			APIResponse errResp = new APIResponse();
			return errResp.addError(new APIError("ER-3000", e.getMessage()));
		}
	}

	private void populateReturnFillingDto(Pair<String, String> combination,
			String apiResp, List<ReturnFilingGstnResponseDto> respDtoList,
			boolean isCounterParty) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Populating GSTN Reponse  for combination {} is {} ",
					combination, apiResp);
		JsonObject respObj = new JsonParser().parse(apiResp).getAsJsonObject();
		Set<String> distinctReturnTypes = new HashSet<>();
		if (respObj.has("EFiledlist")) {
			JsonArray reqArray = respObj.get("EFiledlist").getAsJsonArray();
			for (JsonElement requestObject : reqArray) {
				ReturnFilingGstnResponseDto returnFilingGstnResponseDto = new ReturnFilingGstnResponseDto();
				returnFilingGstnResponseDto.setGstin(combination.getValue0());
				returnFilingGstnResponseDto.setRetPeriod(checkForNull(
						requestObject.getAsJsonObject().get("ret_prd")));
				returnFilingGstnResponseDto.setRetType(checkForNull(
						requestObject.getAsJsonObject().get("rtntype")));
				returnFilingGstnResponseDto.setArnNo(checkForNull(
						requestObject.getAsJsonObject().get("arn")));
				returnFilingGstnResponseDto.setFilingDate(checkForNull(
						requestObject.getAsJsonObject().get("dof")));
				returnFilingGstnResponseDto.setStatus(checkForNull(
						requestObject.getAsJsonObject().get("status")));
				respDtoList.add(returnFilingGstnResponseDto);
				distinctReturnTypes.add(checkForNull(
						requestObject.getAsJsonObject().get("rtntype")));
			}
			if (isCounterParty) {
				stampVendorGstinWithReturnType(combination.getValue0(),
						distinctReturnTypes);
			}
		}

/**		 saveFilingType
		Optional<VendorGstinFilingTypeEntity> existingEntry = vendorGstinFilingTypeRepo
				.findByGstinAndFyAndReturnType(combination.getValue0(),
						combination.getValue1(), "GSTR1");
		if (!existingEntry.isPresent()) {
			List<VendorGstinFilingTypeEntity> entitiesToBePersisted = new ArrayList<>();
			persisitQuaterlyOrMonthlyVendors(respDtoList,
					combination.getValue1(), "GSTR1", entitiesToBePersisted);
			try {
				vendorGstinFilingTypeRepo.saveAll(entitiesToBePersisted);
			} catch (DataIntegrityViolationException e) {
				LOGGER.warn("Error in Vendor Filing Type ", e);
			}
		} **/
	}

	public void persistReturnFillingStatus(
			List<ReturnFilingGstnResponseDto> respDtoList,
			boolean isCounterPary) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Persisting GSTN Reponse in DB, Filed Entries are {} ",
					respDtoList);
		LocalDateTime curTime = LocalDateTime.now();
		Map<String, ReturnFilingGstnResponseDto> dtoMap = new HashMap<String, ReturnFilingGstnResponseDto>();
		Map<String, GstrReturnStatusEntity> entityMap = new HashMap<String, GstrReturnStatusEntity>();
		Map<String, ReturnFilingGstnResponseDto> gstinTaxPeriodMap = new HashMap<String, ReturnFilingGstnResponseDto>();
		List<GstrReturnStatusEntity> saveEntities = new ArrayList<>();
		List<GstrReturnStatusEntity> updateEntities = new ArrayList<>();

		List<List<ReturnFilingGstnResponseDto>> chunks = Lists
				.partition(respDtoList, 2000);
		for (List<ReturnFilingGstnResponseDto> chunk : chunks) {
			chunk.forEach(dto -> {
				dtoMap.put(
						dto.getGstin() + dto.getRetPeriod() + dto.getRetType(),
						dto);
				gstinTaxPeriodMap.put(dto.getGstin() + dto.getRetPeriod(), dto);

			});
		}

		for (String key : gstinTaxPeriodMap.keySet()) {
			ReturnFilingGstnResponseDto returnDto = gstinTaxPeriodMap.get(key);
			List<GstrReturnStatusEntity> entities = returnStatusRepo
					.findByGstinAndTaxPeriodAndIsCounterPartyGstin(
							returnDto.getGstin(), returnDto.getRetPeriod(),
							isCounterPary);
			entities.forEach(entity -> {
				entityMap.put(entity.getGstin() + entity.getTaxPeriod()
						+ entity.getReturnType(), entity);//
			});
		}

		for (String dtoKey : dtoMap.keySet()) {
			ReturnFilingGstnResponseDto dto = dtoMap.get(dtoKey);
			GstrReturnStatusEntity entity = entityMap.get(dtoKey);
			LocalDate fillingDate = null;
			if (!StringUtils.isEmpty(dto.getFilingDate())) {
				fillingDate = LocalDate.parse(dto.getFilingDate(), formatter);
			}
			if (entity == null && dto.getStatus() != null) {	
				entity = new GstrReturnStatusEntity(dto.getGstin(),
						dto.getRetPeriod(), dto.getRetType(),
						dto.getStatus().toUpperCase(), curTime);
				entity.setFilingDate(fillingDate);
				entity.setArnNo(dto.getArnNo());
				entity.setUpdatedOn(curTime);
				entity.setIsCounterPartyGstin(isCounterPary);
				try {
					saveEntities.add(entity);
				} catch (ConstraintViolationException ex) {
					LOGGER.error(
							"Return Filing Status is already available"
									+ " for gstin {}, taxperiod {}, returntype {}",
							dto.getGstin(), dto.getRetPeriod(),
							dto.getRetType());
				}
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Persisted GSTN Reponse in DB, Filed Entry is {} ",
							dto);
			} else {
				if ((entity != null && dto.getStatus() != null) && !dto
						.getStatus().equalsIgnoreCase(entity.getStatus())) {
					entity.setStatus(dto.getStatus());
					entity.setFilingDate(fillingDate);
					entity.setArnNo(dto.getArnNo());
					entity.setUpdatedOn(curTime);
					entity.setIsCounterPartyGstin(isCounterPary);
					updateEntities.add(entity);
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"Updated GSTN Filed Reponse in DB, Filed Entry is {} ",
								dto);
				}
			}
		}
		returnStatusRepo.saveAll(saveEntities);
		returnStatusRepo.saveAll(updateEntities);
	}

	private String getReturnType(String retType) {
		String val = null;
		switch (retType) {
		case "GSTR1":
			val = "R1";
			break;
		case "GSTR2":
			val = "R2";
			break;
		case "GSTR3":
			val = "R3";
			break;
		case "GSTR1A":
			val = "R1A";
			break;
		case "GSTR2A":
			val = "R2A";
			break;
		case "GSTR3B":
			val = "R3B";
			break;
		case "GSTR6":
			val = "R6";
			break;
		case "GSTR7":
			val = "R7";
			break;
		case "GSTR8":
			val = "R8";
			break;
		case "GSTR9":
			val = "R9";
			break;
		case "All":
			val = null;
			break;
		default:
			val = null;
			break;
		}
		return val;
	}

	private String checkForNull(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsString();
	}

	private void stampVendorGstinWithReturnType(String gstin,
			Set<String> distinctReturnTypes) {
		try {
			VendorReturnTypeEntity existingEntity = vendorReturnTypeRepo
					.findByGstin(gstin);
			if (existingEntity == null) {
				VendorReturnTypeEntity vendorReturnType = new VendorReturnTypeEntity();
				if (distinctReturnTypes.contains("GSTR6")
						|| distinctReturnTypes.contains("GSTR7")
						|| distinctReturnTypes.contains("GSTR8")
						|| distinctReturnTypes.contains("GSTR5")) {
					String returnTypeString = String.join(", ",
							distinctReturnTypes);
					vendorReturnType.setReturnTypes(returnTypeString);
				} else if (distinctReturnTypes.contains("CMP08")
						|| distinctReturnTypes.contains("GSTR4")
						|| distinctReturnTypes.contains("GSTR9A")) {
					String returnTypeString = "CMP08,GSTR4,GSTR9A";
					vendorReturnType.setReturnTypes(returnTypeString);
				} else if (distinctReturnTypes.contains("GSTR1")
						|| distinctReturnTypes.contains("GSTR3B")
						|| distinctReturnTypes.contains("ITC04")
						|| distinctReturnTypes.contains("GSTR9")) {
					String returnTypeString = "GSTR1,GSTR3B,ITC04,GSTR9";
					vendorReturnType.setReturnTypes(returnTypeString);
				}
				vendorReturnType.setGstin(gstin);
				vendorReturnType.setCreatedOn(LocalDateTime.now());
				vendorReturnType.setUpdatedOn(LocalDateTime.now());
				vendorReturnTypeRepo.save(vendorReturnType);
			} else {
				String returnTypes = existingEntity.getReturnTypes();
				List<String> returnTypesList = Arrays
						.asList(returnTypes.split(","));
				if (returnTypesList.contains("GSTR1")
						|| returnTypesList.contains("GSTR3B")
						|| returnTypesList.contains("ITC04")
						|| returnTypesList.contains("GSTR9")) {

					if (distinctReturnTypes.contains("CMP08")
							|| distinctReturnTypes.contains("GSTR4")
							|| distinctReturnTypes.contains("GSTR9A")) {
						existingEntity.setReturnTypes("CMP08,GSTR4,GSTR9A");
						existingEntity.setUpdatedOn(LocalDateTime.now());
						vendorReturnTypeRepo.save(existingEntity);
					} else if (distinctReturnTypes.contains("GSTR6")) {
						existingEntity.setReturnTypes("GSTR6");
						existingEntity.setUpdatedOn(LocalDateTime.now());
						vendorReturnTypeRepo.save(existingEntity);
					} else if (distinctReturnTypes.contains("GSTR7")) {
						existingEntity.setReturnTypes("GSTR7");
						existingEntity.setUpdatedOn(LocalDateTime.now());
						vendorReturnTypeRepo.save(existingEntity);
					} else if (distinctReturnTypes.contains("GSTR8")) {
						existingEntity.setReturnTypes("GSTR8");
						existingEntity.setUpdatedOn(LocalDateTime.now());
						vendorReturnTypeRepo.save(existingEntity);
					} else if (distinctReturnTypes.contains("GSTR5")) {
						existingEntity.setReturnTypes("GSTR5");
						existingEntity.setUpdatedOn(LocalDateTime.now());
						vendorReturnTypeRepo.save(existingEntity);
					}
				}
			}
		} catch (ConstraintViolationException ex) {
			String msg = "ConstraintViolationError occured while generating GstinRetrunTypes ";
			LOGGER.error(msg, ex);
		} catch (Exception ex) {
			String msg = "Error occured while generating GstinRetrunTypes ";
			LOGGER.error(msg, ex);
		}
	}

	private void savingInvalidGstins(String gstin, String response) {
		try {
			VendorReturnTypeEntity existingEntity = vendorReturnTypeRepo
					.findByGstin(gstin);
			if (existingEntity == null) {
				VendorReturnTypeEntity vendorReturnType = new VendorReturnTypeEntity();
				vendorReturnType.setGstin(gstin);
				vendorReturnType.setResponse(response);
				vendorReturnType.setCreatedOn(LocalDateTime.now());
				vendorReturnType.setUpdatedOn(LocalDateTime.now());
				vendorReturnTypeRepo.save(vendorReturnType);
			} else {
				existingEntity.setUpdatedOn(LocalDateTime.now());
				vendorReturnTypeRepo.save(existingEntity);
			}
		} catch (ConstraintViolationException ex) {
			String msg = "ConstraintViolationError occured while generating GstinRetrunTypes ";
			LOGGER.error(msg, ex);
		} catch (Exception ex) {
			String msg = "Error occured while generating GstinRetrunTypes ";
			LOGGER.error(msg, ex);
		}
	}

	public void callGstnApi(String gstin, String finYear, String returnType,
			List<VendorGstinFilingTypeEntity> entitiesToBePersisted) {

		endPointResolver.resolveEndPoint(PublicApiConstants.RETURNS);

		PublicApiContext.setContextMap(PublicApiConstants.GSTIN, gstin);
		PublicApiContext.setContextMap(PublicApiConstants.FY, finYear);

		List<ReturnFilingGstnResponseDto> respDtoList = new ArrayList<>();
		try {

			APIResponse resp = getReturnFillingDetailsForReturnType(gstin,
					finYear, returnType);
			if (resp.isSuccess()) {
				populateReturnFillingDto(gstin, finYear, resp.getResponse(),
						respDtoList);
				persisitQuaterlyOrMonthlyVendors(respDtoList, finYear,
						returnType, entitiesToBePersisted);
			} else {
				String errMsg = String.format(
						"Received Error Response %s"
								+ " for gstin %s and finYear %s",
						resp.getErrors(), gstin, finYear);
				LOGGER.error(errMsg);
			}

		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching the Return Filling Status",
					ex);
		}
	}

	private APIResponse getReturnFillingDetailsForReturnType(String gstin,
			String finYear, String returnType) {

		APIParam param1 = new APIParam("gstin", gstin);
		APIParam param2 = new APIParam("fy", finYear);
		APIParam param3 = new APIParam("type", getReturnType(returnType));
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.GET_FILLING_STATUS, param1,
				param2, param3);
		try {
			return apiExecutor.execute(params, null);
		} catch (ApiCallLimitExceededException e) {
			APIResponse errResp = new APIResponse();
			return errResp.addError(new APIError("ER-1000", e.getMessage()));
		}
	}

	private void populateReturnFillingDto(String gstin, String finYear,
			String apiResp, List<ReturnFilingGstnResponseDto> respDtoList) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Populating GSTN Reponse  for gstin {} & fy {} is {} ",
					gstin, finYear, apiResp);
		JsonObject respObj = new JsonParser().parse(apiResp).getAsJsonObject();
		Set<String> distinctReturnTypes = new HashSet<>();
		if (respObj.has("EFiledlist")) {
			JsonArray reqArray = respObj.get("EFiledlist").getAsJsonArray();
			for (JsonElement requestObject : reqArray) {
				ReturnFilingGstnResponseDto returnFilingGstnResponseDto = new ReturnFilingGstnResponseDto();
				returnFilingGstnResponseDto.setGstin(gstin);
				returnFilingGstnResponseDto.setRetPeriod(checkForNull(
						requestObject.getAsJsonObject().get("ret_prd")));
				returnFilingGstnResponseDto.setRetType(checkForNull(
						requestObject.getAsJsonObject().get("rtntype")));
				returnFilingGstnResponseDto.setArnNo(checkForNull(
						requestObject.getAsJsonObject().get("arn")));
				returnFilingGstnResponseDto.setFilingDate(checkForNull(
						requestObject.getAsJsonObject().get("dof")));
				returnFilingGstnResponseDto.setStatus(checkForNull(
						requestObject.getAsJsonObject().get("status")));
				respDtoList.add(returnFilingGstnResponseDto);
				distinctReturnTypes.add(checkForNull(
						requestObject.getAsJsonObject().get("rtntype")));

			}
			stampVendorGstinWithReturnType(gstin, distinctReturnTypes);
		}

	}

	private void persisitQuaterlyOrMonthlyVendors(
			List<ReturnFilingGstnResponseDto> respDtoList, String fy,
			String returnType,
			List<VendorGstinFilingTypeEntity> entitiesToBePersisted) {

		String filingType = findQuaterlyorMonthlyFilingType(respDtoList, fy);
		if (filingType != null) {
			VendorGstinFilingTypeEntity entity = new VendorGstinFilingTypeEntity();
			entity.setGstin(respDtoList.get(0).getGstin());
			entity.setFy(fy);
			entity.setReturnType(returnType);
			entity.setFilingType(filingType);
			entity.setCreatedOn(LocalDateTime.now());
			entitiesToBePersisted.add(entity);
		}
	}

	private String findQuaterlyorMonthlyFilingType(
			List<ReturnFilingGstnResponseDto> respDtoList, String fy) {
		List<String> returnPeriods = GenUtil.extractTaxPeriodsFromFY(fy, "");
		List<ReturnFilingGstnResponseDto> reqDto = new ArrayList<>();
		for (ReturnFilingGstnResponseDto dto : respDtoList) {
			if (returnPeriods.contains(dto.getRetType()))
				reqDto.add(dto);
		}
		if (reqDto.isEmpty())
			return null;

		if (respDtoList.size() > 4)
			return TypeOfGstFiling.MONTHLY.toString();

		List<String> quarterlyList = Arrays.asList("06", "09", "12", "03");
		List<String> monthlyList = Arrays.asList("04", "05", "07", "08", "10",
				"11", "01", "02");
		String filingType = null;
		for (ReturnFilingGstnResponseDto dto : reqDto) {
			if (!quarterlyList.contains(dto.getRetPeriod().substring(0, 2))) {
				return TypeOfGstFiling.MONTHLY.toString();
			} else {
				if (monthlyList.contains(dto.getRetPeriod().substring(0, 2)))
					filingType = TypeOfGstFiling.MONTHLY.toString();
				else
					filingType = TypeOfGstFiling.QUARTERLY.toString();
			}
		}
		return filingType;
	}

}

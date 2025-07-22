package com.ey.advisory.controller;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SaveOnBoardingParamsUtility;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.client.domain.B2COnBoardingConfigEntity;
import com.ey.advisory.common.client.domain.B2CQRAmtConfigEntity;
import com.ey.advisory.common.client.repositories.B2COnBoardingConfigRepo;
import com.ey.advisory.common.client.repositories.B2CQRAmtConfigRepo;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class B2COnboardParamController {

	@Autowired
	@Qualifier("B2COnBoardingConfigRepo")
	B2COnBoardingConfigRepo b2cOnBoardingRepo;

	@Autowired
	@Qualifier("B2CQRAmtConfigRepo")
	B2CQRAmtConfigRepo b2CQRAmtConfigRepo;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepository;

	@Autowired
	SaveOnBoardingParamsUtility saveOnBoardingUtility;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	// New API's
	@PostMapping(value = "/ui/saveB2CQROnboardingParams")
	public ResponseEntity<String> saveB2CQROnboardingParams(
			@RequestBody String jsonString, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String groupCode = TenantContext.getTenantId();
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}

			String entityId = reqJson.get("entityId").getAsString();
			String optionSelected = reqJson.get("option").getAsString();
			JsonArray dataArray = reqJson.get("data").getAsJsonArray();
			EntityInfoEntity entityInfo = entityInfoDetailsRepository
					.findEntityByEntityId(Long.valueOf(entityId));
			String entityName = entityInfo.getEntityName();
			String panStr = entityInfoRepository
					.findPanByEntityId(Long.valueOf(entityId)).get(0);
			reqJson.addProperty("pan", panStr);
			reqJson.addProperty("option", optionSelected);
			reqJson.addProperty("groupCode", groupCode);
			reqJson.addProperty("createdUser", userName);
			reqJson.addProperty("entityName", entityName);
			reqJson.addProperty("updatedBy", userName);
			LOGGER.debug("Updated Request Json {}", reqJson);

			String persistStatus = saveOnBoardingUtility
					.saveQROnBoardingData(reqJson, dataArray);
			if (persistStatus.equalsIgnoreCase("Success")) {
				String savStatus = "Data Saved Successfully";
				boolean isEligibleforBcAPIPush = bcAPIPushReq(groupCode);
				if (isEligibleforBcAPIPush) {

					String bcAPIPushStatus = callSaveOnBoardingBCAPI(reqJson,
							groupCode, "bcapi.save.url");
					if (bcAPIPushStatus.equalsIgnoreCase("Success")) {
						LOGGER.debug("Records Saved in BCAPI", reqJson);
					}
				}
				resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp", savStatus);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else {
				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("Failed to Save Records"));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);

			}
		} catch (Exception ex) {
			LOGGER.error("Exception while saveB2COnboardingParams ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Failed to Save Records"));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/ui/getB2COnBoardingParams", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getB2COnBoardingParams(
			HttpServletRequest request, HttpServletResponse response) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		try {
			String entityId = request.getParameter("entityId");
			String optionSelected = request.getParameter("option");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Id ", entityId);
			}
			List<B2COnBoardingConfigEntity> onBoardingConfigEntity = b2cOnBoardingRepo
					.findByEntityIdAndOptionSelectedAndIsActiveTrue(
							Long.valueOf(entityId), optionSelected);

			if (onBoardingConfigEntity.isEmpty()) {
				String errMsg = String.format(
						"No Data Available in OnBoarding Entity for the Entity %s",
						entityId);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			for (B2COnBoardingConfigEntity entity : onBoardingConfigEntity) {

				if (Strings.isNullOrEmpty(entity.getPaymentInfo())) {
					entity.setPaymentInfo("I");
				}
				entity.setCreatedOn(
						EYDateUtil.toISTDateTimeFromUTC(entity.getCreatedOn()));
			}
			dataObj.add("data", gson.toJsonTree(onBoardingConfigEntity));
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", dataObj);
			LOGGER.debug("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/saveB2CQRAmtConfigParams")
	public ResponseEntity<String> saveB2CQRAmtConfigParams(
			@RequestBody String jsonString, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String groupCode = TenantContext.getTenantId();
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}

			String entityId = reqJson.get("entityId").getAsString();
			EntityInfoEntity entityInfo = entityInfoDetailsRepository
					.findEntityByEntityId(Long.valueOf(entityId));
			String entityName = entityInfo.getEntityName();
			String panStr = entityInfoRepository
					.findPanByEntityId(Long.valueOf(entityId)).get(0);
			reqJson.addProperty("pan", panStr);
			reqJson.addProperty("groupCode", groupCode);
			reqJson.addProperty("createdUser", userName);
			reqJson.addProperty("entityName", entityName);
			reqJson.addProperty("updatedBy", userName);
			LOGGER.debug("Updated Request Json {}", reqJson);

			String persistStatus = saveOnBoardingUtility
					.saveQRAmtConfigData(reqJson);
			if (persistStatus.equalsIgnoreCase("Success")) {
				String savStatus = "Data Saved Successfully";
				boolean isEligibleforBcAPIPush = bcAPIPushReq(groupCode);
				if (isEligibleforBcAPIPush) {

					String bcAPIPushStatus = callSaveOnBoardingBCAPI(reqJson,
							groupCode, "bcapi.amtsave.url");
					if (bcAPIPushStatus.equalsIgnoreCase("Success")) {
						LOGGER.debug("Records Saved in BCAPI", reqJson);
					}
				}
				resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp", savStatus);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else {
				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("Failed to Save Records"));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);

			}
		} catch (Exception ex) {
			LOGGER.error("Exception while saveB2COnboardingParams ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Failed to Save Records"));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/ui/getB2CAmtConfigParams", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getB2CAmtConfigParams(
			HttpServletRequest request, HttpServletResponse response) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		try {
			String entityId = request.getParameter("entityId");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Id ", entityId);
			}
			B2CQRAmtConfigEntity amtOnBoardingConfigEntity = b2CQRAmtConfigRepo
					.findByEntityIdAndIsActiveTrue(Long.valueOf(entityId));
			if (amtOnBoardingConfigEntity == null) {
				String errMsg = String.format(
						"No Data Available in OnBoarding Entity for the Entity %s",
						entityId);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			amtOnBoardingConfigEntity
					.setCreatedOn(EYDateUtil.toISTDateTimeFromUTC(
							amtOnBoardingConfigEntity.getCreatedOn()));

			dataObj.add("data", gson.toJsonTree(amtOnBoardingConfigEntity));
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", dataObj);
			LOGGER.debug("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	private String callSaveOnBoardingBCAPI(JsonObject reqJson, String groupCode,
			String apiIdentifer) {
		String apiStatus = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}
			if (!env.containsProperty(apiIdentifer)) {
				String msg = "Bcapi save url is not configured";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			HttpPost httpPost = new HttpPost(env.getProperty(apiIdentifer));
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("groupCode", TenantContext.getTenantId());
			StringEntity entity = new StringEntity(reqJson.toString());
			httpPost.setEntity(entity);

			HttpResponse resp = httpClient.execute(httpPost);

			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("BCAPI Return Response  {}", apiResp);
			}
			if (httpStatusCd == 200) {
				apiStatus = "SUCCESS";
			} else {
				apiStatus = "FAILED";
			}
			return apiStatus;
		} catch (Exception ex) {
			LOGGER.error("Exception while calling BCAPI ", ex);
			return "FAILED";
		}

	}

	private boolean bcAPIPushReq(String groupCode) {

		Map<String, Config> configMap = configManager.getConfigs("BCAPI",
				"non.sap.clients", "DEFAULT");

		if (!configMap.containsKey("non.sap.clients")) {
			return false;
		}
		String groupsAvailable = configMap.get("non.sap.clients").getValue();

		return groupsAvailable.contains(groupCode);
	}
}

package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.common.AuthOtpCommonUtility;
import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.Data;

@RestController
public class GstinOtpAuthTokenController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GstinOtpAuthTokenController.class);

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private AuthOtpCommonUtility commUtility;

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@PostMapping(value = "/ui/getOtp", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOtpUI(@RequestBody String jsonString) {
		return getOtp(jsonString);
	}

	@PostMapping(value = "/api/getOtp", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOtpAPI(@RequestBody String jsonString) {
		return getOtp(jsonString);
	}

	private ResponseEntity<String> getOtp(String jsonString) {
		JsonObject detResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupCode = TenantContext.getTenantId();

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"About to execute the OTP api, Request is '%s'",
						json.toString());
				LOGGER.debug(msg);
			}
			GstnData gstnData = gson.fromJson(json, GstnData.class);
			String gstin = gstnData.getGstin();

			APIResponse resp = commUtility.getOtpApiResp(gstin, groupCode);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executed the OTP API Request, Response is '%s'", resp);
				LOGGER.debug(msg);
			}
			if (resp.isSuccess()) {
				LOGGER.debug("Api Response is {} ", resp.getResponse());
				detResp.add("resp",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				return new ResponseEntity<>(detResp.toString(), HttpStatus.OK);
			} else {
				LOGGER.error("Generating OTP Failed for Gstin {}", gstin);
				detResp.add("resp",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				return new ResponseEntity<>(detResp.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while generating OTP ", ex);
			detResp.add("resp", gson.toJsonTree(APIRespDto.creatErrorResp()));
			return new ResponseEntity<>(detResp.toString(), HttpStatus.OK);
		} finally {
			TenantContext.clearTenant();
		}

	}

	@PostMapping(value = "/ui/getAuthToken", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAuthTokenUI(@RequestBody String jsonString) {
		return getAuthToken(jsonString);
	}

	@PostMapping(value = "/api/getAuthToken", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAuthTokenAPI(
			@RequestBody String jsonString) {
		return getAuthToken(jsonString);
	}

	private ResponseEntity<String> getAuthToken(String jsonString) {
		String groupCode = TenantContext.getTenantId();
		JsonObject detResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		try {
			TenantContext.setTenantId(groupCode);
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("About to execute the AuthToken Generator, "
								+ "Request is '%s'", json.toString());
				LOGGER.debug(msg);
			}
			OtpData otpData = gson.fromJson(json, OtpData.class);
			String gstin = otpData.getGstin();
			String otp = otpData.getOtpCode();
			APIResponse resp = commUtility.getAuthTokenResp(gstin, otp);
			if (resp.isSuccess()) {
				detResp.add("resp",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				return new ResponseEntity<>(detResp.toString(), HttpStatus.OK);
			} else {
				List<APIError> errorList = resp.getErrors();
				detResp.add("resp",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				detResp.add("errors", gson.toJsonTree(errorList));
				LOGGER.error("Auth Token generation Failed due to {}", "");
				return new ResponseEntity<>(detResp.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Auth Token ", ex);
			detResp.add("resp", gson.toJsonTree(APIRespDto.creatErrorResp()));
			return new ResponseEntity<>(detResp.toString(), HttpStatus.OK);

		} finally {
			TenantContext.clearTenant();
		}

	}

	@PostMapping(value = "/api/getGstinStatus", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstinStatus(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonArray gstins = requestObject.getAsJsonArray("req");
		Gson googleJson = new Gson();
		Gson gson = GsonUtil.newSAPGsonInstance();

		Type listType = new TypeToken<ArrayList<String>>() {
		}.getType();
		ArrayList<String> gstinList = googleJson.fromJson(gstins, listType);

		Map<String, String> authTokenStatusMap = authTokenService
				.getAuthTokenStatusForGstins(gstinList);

		List<GstinStatus> gstinStatusList = new ArrayList<>();

		for (Map.Entry<String, String> map : authTokenStatusMap.entrySet()) {
			GstinStatus obj = new GstinStatus();
			obj.setGstin(map.getKey());
			obj.setStatus(map.getValue());
			gstinStatusList.add(obj);
		}

		JsonObject gstinDetResp = new JsonObject();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(gstinStatusList);
		gstinDetResp.add("gstinStatus", respBody);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", gstinDetResp);
		if (LOGGER.isDebugEnabled()) {
			String msg = "End Gstin Status, before returning response";
			LOGGER.debug(msg);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
	
	@Data
	public class GstnData {
		private String gstin;
		private boolean einvApplicability;
	}

	@Data
	public class OtpData {
		private String gstin;
		private String otpCode;

	}

	@Data
	public class GstinStatus {
		private String gstin;
		private String status;
	}
}

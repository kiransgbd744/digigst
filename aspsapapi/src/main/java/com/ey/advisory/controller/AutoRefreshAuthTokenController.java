package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.services.common.AuthOtpCommonUtility;
import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.controller.GstinOtpAuthTokenController.GstnData;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AutoRefreshAuthTokenController {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	private AuthOtpCommonUtility commUtility;

	@Autowired
	private GstinAPIAuthInfoRepository gstinAuthInfo;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@PostMapping(value = "/api/generateOtp", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateOtp(@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		RespDto respDto = new RespDto();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			if (!requestObject.has("req")) {
				respDto.setStatus(0);
				respDto.setErrorDetails("Invalid request payload.");
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			GstnData gstnData = gson.fromJson(reqJson, GstnData.class);
			if (reqJson.get("gstin") == null) {
				respDto.setStatus(0);
				respDto.setErrorDetails("GSTIN is mandatory.");
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			String gstin = gstnData.getGstin().toUpperCase();
			if (gstin.length() != 15 || !gstin.matches("[A-Za-z0-9]+")
					|| gstin.matches("[A-Za-z]+") || gstin.matches("[0-9]+")) {
				respDto.setStatus(0);
				respDto.setErrorDetails("GSTIN is not valid.");
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			APIResponse resp = commUtility.getOtpApiResp(gstin,
					TenantContext.getTenantId());
			if (resp.isSuccess()) {
				LOGGER.debug("Api Response is {} ", resp.getResponse());
				respDto.setStatus(1);
			} else {
				respDto.setStatus(0);
				respDto.setErrorDetails(resp.getErrors().get(0).getErrorDesc());
			}
			return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
					HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while Generating OTP ", e);
			respDto.setStatus(0);
			respDto.setErrorDetails(String.format("OTP Generation Failed."));
			return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
					HttpStatus.OK);
		}
	}

	@PostMapping(value = "/api/generateAuthToken", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateAuthToken(
			@RequestBody String jsonString) {
		RespDto respDto = new RespDto();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			if (!requestObject.has("req")) {
				respDto.setStatus(0);
				respDto.setErrorDetails("Invalid request payload.");
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			if (reqJson.get("gstin") == null || reqJson.get("otp") == null) {
				respDto.setStatus(0);
				respDto.setErrorDetails("GSTIN and OTP are mandatory.");
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			String gstin = reqJson.get("gstin").getAsString();
			if (gstin.length() != 15 || !gstin.matches("[A-Za-z0-9]+")
					|| gstin.matches("[A-Za-z]+") || gstin.matches("[0-9]+")) {
				respDto.setStatus(0);
				respDto.setErrorDetails("GSTIN is not valid.");
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			String otp = reqJson.get("otp").getAsString();
			if (otp.length() != 6 || !otp.matches("^[0-9]{6}$")) {
				respDto.setStatus(0);
				respDto.setErrorDetails("OTP is not valid.");
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			APIResponse resp = commUtility.getAuthTokenResp(gstin, otp);
			if (resp.isSuccess()) {
				LOGGER.debug("Api Response is {} ", resp.getResponse());
				respDto.setStatus(1);
			} else {
				respDto.setStatus(0);
				respDto.setErrorDetails(resp.getErrors().get(0).getErrorDesc());
			}
			return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
					HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while Authenticating OTP ", e);
			respDto.setStatus(0);
			respDto.setErrorDetails(
					String.format("GSTIN AuthToken Generation Failed."));
			return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
					HttpStatus.OK);
		}
	}

	@GetMapping(value = "/api/getInActiveGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInActiveGstins() {
		RespDto respDto = new RespDto();
		String groupCode = TenantContext.getTenantId();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<String> onboardedGstins = gSTNDetailRepository
					.findAllGstin();
			if (onboardedGstins.isEmpty()) {
				LOGGER.error("There are no active gstins for group: {}",
						groupCode);
				respDto.setStatus(0);
				respDto.setCount(0);
				respDto.setErrorDetails(String
						.format("No GSTINs Onboarded for group %s", groupCode));
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			// List<GstnAPIAuthInfo> allAuthInfoEntries = gstinAuthInfo
			// .findAllByProviderNameAndGroupCode(
			// APIProviderEnum.GSTN.name(), groupCode);
			//// if (allAuthInfoEntries.isEmpty()) {
			// LOGGER.error("There are no active gstins or group: {}",
			// TenantContext.getTenantId());
			// respDto.setStatus(1);
			// respDto.setCount(0);
			// respDto.setErrorDetails(String.format(
			// "No Active Gstins available for group %s", groupCode));
			// return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
			// HttpStatus.OK);
			// }
			Date expiryStTime = new Date();
			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(expiryStTime); // sets calendar time/date
			cal.add(Calendar.MINUTE, 1);
			Date expiryEndTime = cal.getTime();
			List<GstnAPIAuthInfo> refreshableAuthInfoList = gstinAuthInfo
					.findAllByProviderNameAndGroupCodeAndGstinInAndGstnTokenExpiryTimeLessThanOrGstnTokenExpiryTimeBetween(
							APIProviderEnum.GSTN.name(), groupCode,
							onboardedGstins, expiryStTime, expiryStTime,
							expiryEndTime);

			if (refreshableAuthInfoList.isEmpty()) {
				LOGGER.error(
						"Either All GSTIN's are active or never manually generated an auth token,"
								+ " for group: {}",
						groupCode);
				respDto.setStatus(1);
				respDto.setCount(0);
				return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
						HttpStatus.OK);
			}
			List<GstinStatus> gstDto = new ArrayList<>();
			for (GstnAPIAuthInfo Inacgstin : refreshableAuthInfoList) {
				GstinStatus gstnDtls = new GstinStatus();
				gstnDtls.setGstin(Inacgstin.getGstin());
				gstDto.add(gstnDtls);
			}
			respDto.setResp(gstDto);
			respDto.setStatus(1);
			respDto.setCount(refreshableAuthInfoList.size());
			return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
					HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while fetching InActive GSTINs", e);
			respDto.setStatus(0);
			respDto.setCount(0);
			respDto.setErrorDetails(String
					.format("Internal Error while Fetching InActive GSTINs."));
			return new ResponseEntity<>(gson.toJson(respDto, RespDto.class),
					HttpStatus.OK);
		}
	}

	@ToString
	@Data
	public class RespDto {
		private Integer status;
		private Integer count;
		private String errorDetails;
		@Expose
		@SerializedName("resp")
		private List<GstinStatus> resp;
	}

	@ToString
	@Data
	public class GstinStatus {
		private String gstin;
		private String status;
	}

}

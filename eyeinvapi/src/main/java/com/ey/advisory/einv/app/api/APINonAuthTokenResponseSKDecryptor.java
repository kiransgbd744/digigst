package com.ey.advisory.einv.app.api;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("APINonAuthTokenResponseSKDecryptor")
public class APINonAuthTokenResponseSKDecryptor
		implements APIResponseDecryptor {

	@Autowired
	@Qualifier("EINVAPIErrorMsgConvertor")
	APIErrorMsgConvertor apiErrorMsgConvertor;

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Autowired
	ERPReqRespLogHelper reqLogHelper;

	public APINonAuthTokenResponseSKDecryptor() {
	}

	@Override
	public APIResponse decrypt(APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, String respJson) {
		if (Strings.isNullOrEmpty(respJson)) {
			LOGGER.error(APIConstants.IRP_BLANK_RESP);
			throw new InvalidAPIResponseException(APIConstants.IRP_BLANK_RESP);
		}
		String sk = "";
		String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
		// Get the JSON object. If NIC returns an invalid Json, throw
		// an exception.
		JsonObject jsonObject = null;
		try {
			jsonObject = (JsonObject) JsonParser.parseString(respJson);
		} catch (Exception ex) {
			String msg = String.format(
					"IRP returned Invalid JSON as Response - %s", respJson);
			LOGGER.error(msg, ex);
			throw new InvalidAPIResponseException(msg);
		}
		String apiProvider = CommonUtil.getApiProviderEnum(params).name();
		GstnAPIAuthInfo authInfo = persistenceManager.loadAPIAuthInfo(gstin,
				apiProvider);
		if (authInfo != null) {
			sk = authInfo.getSessionKey();
		} else {
			throw new AuthTokenExpiredException(
					"E-Invoice AuthToken Details Not Found");
		}
		byte[] respBytes = null;
		APIResponse apiResp = new APIResponse();
		if (jsonObject.has("status_cd")
				&& "0".equals(jsonObject.get("status_cd").getAsString())) {
			APIError errObj = new APIError();
			errObj.setErrorCode(jsonObject.get("errorCode").getAsString());
			errObj.setErrorDesc(jsonObject.get("errorDesc").getAsString());
			apiResp.addError(errObj);
			if (!com.ey.advisory.einv.app.api.APIIdentifiers.GET_EINV_AUTH_TOKEN
					.equalsIgnoreCase(params.getApiAction())) {
				reqLogHelper.updateNicErrRespPayload(respJson);
			}
			return apiResp;
		}
		if (APIIdentifiers.CANCEL_EWB.equals(params.getApiAction())) {
			if (jsonObject.get("data") != null) {
				respBytes = CryptoUtils.decrypt(
						jsonObject.get("data").getAsString(),
						Base64.decodeBase64(sk));
				String resp = new String(respBytes);
				apiResp.setResponse(resp);
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("CAN EWB BY IRN Success Response is :{}", resp);
				}
			} else {
				respBytes = Base64
						.decodeBase64(jsonObject.get("error").getAsString());

				String resp = new String(respBytes);
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("CAN EWB BY IRN Error Response is : {}", resp);
				}
				JsonObject errorJson = (JsonObject) JsonParser
						.parseString(resp);
				String errorCode = errorJson.get("errorCodes").getAsString();

				// NIC doesn't use a consistent error code format. Following are
				// the 3 formats that are known to us currently.
				// 1 - <error message> : <single error code or multiple
				// errorcodes separated by comma>
				// 2 - <single error code or multiple
				// error codes separated by comma>
				// 3 - <error message>
				// The following code determines which of the above cases
				// we've encountered and extracts the error-code/error message
				// from the returned JSON.
				if (!com.ey.advisory.einv.app.api.APIIdentifiers.GET_EINV_AUTH_TOKEN
						.equalsIgnoreCase(params.getApiAction())) {
					reqLogHelper.updateNicErrRespPayload(respJson);
				}
				List<APIError> errorList = apiErrorMsgConvertor
						.getErrorList(errorCode);
				if (errorList != null && !errorList.isEmpty()) {
					for (APIError errObj : errorList) {
						apiResp.addError(errObj);
					}
				}
			}
		} else {
			if (jsonObject.get("Status").getAsString().equals("1")) {
				respBytes = CryptoUtils.decrypt(
						jsonObject.get("Data").getAsString(),
						Base64.decodeBase64(sk));
				String resp = new String(respBytes);
				JsonObject jsonResponse = JsonParser.parseString(resp)
						.getAsJsonObject();
				if (jsonObject.has("InfoDtls")
						&& jsonObject.get("InfoDtls") != JsonNull.INSTANCE)
					jsonResponse.addProperty("InfoDtls", jsonObject
							.get("InfoDtls").getAsJsonArray().toString());

				if (jsonObject.has("Alert")
						&& jsonObject.get("Alert") != JsonNull.INSTANCE)
					jsonResponse.addProperty("Alert",
							jsonObject.get("Alert").getAsString());

				apiResp.setResponse(jsonResponse.toString());
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Success Response is : {}", resp);
				}
			} else {
				if (!com.ey.advisory.einv.app.api.APIIdentifiers.GET_EINV_AUTH_TOKEN
						.equalsIgnoreCase(params.getApiProvider().name())) {
					reqLogHelper.updateNicErrRespPayload(respJson);
				}
				JsonArray array = jsonObject.get("ErrorDetails")
						.getAsJsonArray();
				Gson gson = new GsonBuilder().create();
				Type type = new TypeToken<List<APIError>>() {
				}.getType();
				List<APIError> errList = gson.fromJson(array, type);

				if (jsonObject.has("InfoDtls")
						&& jsonObject.get("InfoDtls") != JsonNull.INSTANCE) {
					APIError errObj = new APIError();
					JsonObject infoObj = jsonObject.get("InfoDtls")
							.getAsJsonArray().get(0).getAsJsonObject();
					JsonObject desc = infoObj.get("Desc").getAsJsonObject();
					String ackNo = "-";
					String ackDt = "-";
					if (desc.has("AckNo") && !desc.get("AckNo").isJsonNull()) {
						ackNo = desc.get("AckNo").getAsString();
					}
					if (desc.has("AckDt") && !desc.get("AckDt").isJsonNull()) {
						ackDt = desc.get("AckDt").getAsString();
					}
					String irn = desc.get("Irn").getAsString();
					errObj.setErrorCode(infoObj.get("InfCd").getAsString());
					errObj.setErrorDesc(String.format(
							"AckNo=%s,AckDt=%s,Irn=%s", ackNo, ackDt, irn));
					if (desc.has("IrnDtl")) {
						JsonObject irnDtls = desc.get("IrnDtl")
								.getAsJsonObject();
						if (irnDtls.has("RegIrp")
								&& !irnDtls.get("RegIrp").isJsonNull()) {
							errObj.setRegIrp(
									irnDtls.get("RegIrp").getAsString());
						}
					}
					errList.add(errObj);
				}
				if (jsonObject.has("Alert")
						&& jsonObject.get("Alert") != JsonNull.INSTANCE) {
					APIError errObj = new APIError();
					errObj.setErrorCode("Alert");
					errObj.setErrorDesc(jsonObject.get("Alert").getAsString());
					errList.add(errObj);
				}

				errList.stream().forEach(err -> apiResp.addError(err));
			}
		}
		return apiResp;

	}

}
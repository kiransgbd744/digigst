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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("APINonAuthTokenResponseRKDecryptor")
public class APINonAuthTokenResponseRKDecryptor
		implements APIResponseDecryptor {

	@Autowired
	@Qualifier("EINVAPIErrorMsgConvertor")
	APIErrorMsgConvertor apiErrorMsgConvertor;

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Autowired
	ERPReqRespLogHelper reqLogHelper;

	public APINonAuthTokenResponseRKDecryptor() {
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
		} catch (JsonParseException ex) {
			String msg = String.format(
					"IRP returned Invalid JSON as Response, Response is %s",
					respJson);
			LOGGER.error(msg, ex);
			throw new InvalidAPIResponseException(msg);
		}
		String apiProvider = CommonUtil.getApiProviderEnum(params).name();
		GstnAPIAuthInfo authInfo = persistenceManager.loadAPIAuthInfo(gstin,
				apiProvider);
		if (authInfo != null) {
			sk = authInfo.getSessionKey();
		} else {
			// TODO throw Exception
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
		if (jsonObject.get("Status").getAsString().equals("1")) {
			respBytes = CryptoUtils.decrypt(
					jsonObject.get("Data").getAsString(),
					Base64.decodeBase64(sk));
			String resp = new String(respBytes);
			apiResp.setResponse(resp);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("GET API Success Response is : {}", resp);
			}
		} else {
			if (!com.ey.advisory.einv.app.api.APIIdentifiers.GET_EINV_AUTH_TOKEN
					.equalsIgnoreCase(params.getApiAction())) {
				reqLogHelper.updateNicErrRespPayload(respJson);
			}
			JsonArray array = jsonObject.get("ErrorDetails").getAsJsonArray();
			Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<List<APIError>>() {
			}.getType();
			List<APIError> errList = gson.fromJson(array, type);
			errList.stream().forEach(err -> apiResp.addError(err));
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("GET API Error Response is : {}", apiResp);
			}
		}
		return apiResp;
	}
}

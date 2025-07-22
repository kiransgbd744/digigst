package com.ey.advisory.ewb.app.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Component("EWBAPINonAuthTokenResponseRKDecryptor")
@Slf4j
public class APINonAuthTokenResponseRKDecryptor
		implements APIResponseDecryptor {

	@Autowired
	@Qualifier("EWBAPIErrorMsgConvertor")
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
		JsonParser jsonParser = new JsonParser();
		// Get the JSON object. If NIC returns an invalid Json, throw
		// an exception.
		JsonObject jsonObject = null;
		try {
			jsonObject = (JsonObject) jsonParser.parse(respJson);
		} catch (JsonParseException ex) {
			String msg = String.format(
					"NIC returned Invalid JSON as Response, Response is %s",
					respJson);
			LOGGER.error(msg, ex);
			throw new InvalidAPIResponseException(msg);
		}
		GstnAPIAuthInfo authInfo = persistenceManager.loadAPIAuthInfo(gstin,
				APIProviderEnum.EWB.name());
		if (authInfo != null) {
			sk = authInfo.getSessionKey();
		} else {
			// TODO throw Exception
		}

		String data = null;
		if (jsonObject.has("data")) {
			data = jsonObject.get("data").getAsString();
		}
		byte[] rk = null;
		byte[] respBytes = null;
		APIResponse apiResp = new APIResponse();
		if (data != null) {
			String rek = jsonObject.get("rek").getAsString();
			rk = CryptoUtils.decrypt(rek, Base64.decodeBase64(sk));
			respBytes = CryptoUtils.decrypt(data, rk);
			String resp = new String(respBytes);
			apiResp.setResponse(resp);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Success Response is :" + resp);
			}
		} else {
			if (APIIdentifiers.GET_AUTH_TOKEN
					.equalsIgnoreCase(params.getApiAction())) {
				reqLogHelper.updateNicErrRespPayload(respJson);
			}
			respBytes = Base64
					.decodeBase64(jsonObject.get("error").getAsString());
			String resp = new String(respBytes);
			JsonObject errorJson = (JsonObject) jsonParser.parse(resp);
			String errorCode = errorJson.get("errorCodes").getAsString();
			
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Error Response is :" + resp);
			}
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
			List<APIError> errorList = apiErrorMsgConvertor
					.getErrorList(errorCode);
			if (errorList != null && !errorList.isEmpty()) {
				for (APIError errObj : errorList) {
					apiResp.addError(errObj);
				}
			}
		}
		return apiResp;

	}

}

package com.ey.advisory.einv.app.api;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EINVAPIAuthTokenResponseDecryptor")
public class APIAuthTokenResponseDecryptor implements APIResponseDecryptor {

	@Autowired
	@Qualifier("EINVAPIErrorMsgConvertor")
	APIErrorMsgConvertor apiErrorMsgConvertor;

	public APIAuthTokenResponseDecryptor() {
	}

	@Override
	public APIResponse decrypt(APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, String respJson) {
		if (Strings.isNullOrEmpty(respJson)) {
			String msg = "IRP returned Blank Response";
			LOGGER.error(msg);
			throw new InvalidAPIResponseException(msg);
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(respJson);
		APIResponse apiResp = new APIResponse();
		if (jsonObject.has("status_cd")
				&& "0".equals(jsonObject.get("status_cd").getAsString())) {
			APIError errObj = new APIError();
			errObj.setErrorCode(jsonObject.get("errorCode").getAsString());
			errObj.setErrorDesc(jsonObject.get("errorDesc").getAsString());
			apiResp.addError(errObj);
			return apiResp;
		}
		String status = jsonObject.get("Status").getAsString();
		if ("1".equals(status)) {
			JsonObject jsonObjectAuth = (JsonObject) jsonParser
					.parse(jsonObject.get("Data").toString());
			apiResp.setResponse(respJson);
			String appKeyString = (String) context
					.get(APIContextConstants.APP_KEY);
			String sek = jsonObjectAuth.get("Sek").getAsString();
			byte[] sk = CryptoUtils.decrypt(sek,
					Base64.decodeBase64(appKeyString));
			String strSk = Base64.encodeBase64String(sk);
			context.put(APIContextConstants.SESSION_KEY, strSk);
		} else {
			JsonArray array = jsonObject.get("ErrorDetails").getAsJsonArray();
			Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<List<APIError>>() {
			}.getType();
			List<APIError> errList = gson.fromJson(array, type);
			errList.stream().forEach(err -> apiResp.addError(err));
		}
		return apiResp;
	}

}

package com.ey.advisory.einv.app.api;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("APIResponseProcessImpl")
public class APIResponseProcessImpl implements APIResponseProcessor {

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Override
	public APIResponse processResponse(APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, APIResponse response) {
		String id = params.getApiAction();
		String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
		if (!id.equals((APIIdentifiers.GET_EINV_AUTH_TOKEN))
				|| !response.isSuccess()) {
			return response;
		}
		String jsonResponse = response.getResponse();
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonAuth = (JsonObject) jsonParser.parse(jsonResponse);
		JsonObject jsonObject = (JsonObject) jsonParser
				.parse(jsonAuth.get("Data").toString());
		String sek = jsonObject.get("Sek").getAsString();
		String authToken = jsonObject.get("AuthToken").getAsString();
		String appKeyString = (String) context.get(APIContextConstants.APP_KEY);
		String sk = (String) context.get(APIContextConstants.SESSION_KEY);
		String apiProvider = CommonUtil.getApiProviderEnum(params).name();
		NICAPIAuthInfo authTokenInfo = new NICAPIAuthInfo(appKeyString, sek, sk,
				authToken,apiProvider);
		GstnAPIAuthInfo apiAuthInfo = persistenceManager.loadAPIAuthInfo(gstin,
				apiProvider);
		Date curDate = new Date();
		if (apiAuthInfo == null) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"AuthToken Entry is not available for GSTIN {}, Provider Name {} combination",
						gstin, apiProvider);
			String groupCode = TenantContext.getTenantId();
			apiAuthInfo = new GstnAPIAuthInfo();
			apiAuthInfo.setGstin(gstin);
			apiAuthInfo.setProviderName(apiProvider);
			apiAuthInfo.setGroupCode(groupCode);
			apiAuthInfo.setCreatedDate(curDate);
		}
		apiAuthInfo.setAppKey(appKeyString);
		apiAuthInfo.setGstnToken(authToken);
		apiAuthInfo.setSessionKey(sk);
		apiAuthInfo.setUpdatedDate(curDate);
		apiAuthInfo.setGstnTokenGenTime(curDate);
		apiAuthInfo
				.setGstnTokenExpiryTime(authTokenInfo.getAuthTokenExpiryTime());
		persistenceManager.saveAPIAuthInfo(apiAuthInfo);
		return response;
	}

}

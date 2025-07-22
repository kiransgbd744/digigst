package com.ey.advisory.ewb.app.api;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EWBAPIResponseProcessImpl")
public class APIResponseProcessImpl implements APIResponseProcessor {

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Override
	public APIResponse processResponse(APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, APIResponse response) {

		if (params.getApiProvider() != APIProviderEnum.EWB) {
			// Handle the GSTN API case here and remove the following
			// throw statement.
			throw new APIException(
					"GSTN API support is not included currently");
		}
		String id = params.getApiAction();
		String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
		if (!id.equals((APIIdentifiers.GET_AUTH_TOKEN))
				|| !response.isSuccess()) {
			return response;
		}
		String jsonResponse = response.getResponse();
		JsonObject jsonAuth = (JsonObject) JsonParser.parseString(jsonResponse);
		JsonObject jsonObject = (JsonObject) JsonParser
				.parseString(jsonAuth.get("Data").toString());
		String sek = jsonObject.get("Sek").getAsString();
		String authToken = jsonObject.get("AuthToken").getAsString();
		String appKeyString = (String) context.get(APIContextConstants.APP_KEY);
		String sk = (String) context.get(APIContextConstants.SESSION_KEY);
		NICAPIAuthInfo authTokenInfo = new NICAPIAuthInfo(appKeyString, sek, sk,
				authToken);
		GstnAPIAuthInfo apiAuthInfo = persistenceManager.loadAPIAuthInfo(gstin,
				APIProviderEnum.EWB.name());
		Date curDate = new Date();
		if (apiAuthInfo == null) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"AuthToken Entry is not available for GSTIN {},"
								+ " Provider Name {} combination",
						gstin, APIProviderEnum.EWB.name());
			apiAuthInfo = new GstnAPIAuthInfo();
			String groupCode = TenantContext.getTenantId();
			apiAuthInfo.setGstin(gstin);
			apiAuthInfo.setProviderName(APIProviderEnum.EWB.name());
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

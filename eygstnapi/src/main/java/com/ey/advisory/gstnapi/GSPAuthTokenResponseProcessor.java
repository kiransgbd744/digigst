package com.ey.advisory.gstnapi;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIResponseProcessor;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;

@Component("GSPAuthTokenResponseProcessor")
public class GSPAuthTokenResponseProcessor implements APIResponseProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GSPAuthTokenResponseProcessor.class);

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Override
	public APIResponse processResponse(APIParams params, APIConfig config,
			APIExecParties parties, APIAuthInfo authInfo, APIResponse response,
			Map<String, Object> context) {

		// The Auth Token Details should be persisted here, for later use.
		GSTNAPIAuthTokenResponse authResp = (GSTNAPIAuthTokenResponse) response;
		GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
		String groupCode = endUser.getGroupConfig().getGroupCode();
		String gstin = endUser.getGstinConfig().getGstin();
		Date curDate = new Date();
		GstnAPIAuthInfo apiAuthInfo = (GstnAPIAuthInfo) authInfo;
		String appKeyString = (String) context.get(APIContextConstants.APP_KEY);
		/*
		 * APIAuthInfo Record will get created only for the first time of the
		 * Public Auth Token API invocation
		 */
		if (authInfo == null) {
			apiAuthInfo = new GstnAPIAuthInfo();
			apiAuthInfo.setProviderName(APIProviderEnum.GSTN.name());
			apiAuthInfo.setGstin(gstin);
			apiAuthInfo.setGroupCode(groupCode);
			apiAuthInfo.setCreatedDate(curDate);
		}
		apiAuthInfo.setAppKey(appKeyString);
		apiAuthInfo.setGstnToken(authResp.getAuthToken());
		apiAuthInfo.setSessionKey(authResp.getSk());
		apiAuthInfo.setUpdatedDate(curDate);
		apiAuthInfo.setGstnTokenGenTime(curDate);
		apiAuthInfo.setGstnTokenExpiryTime(
				java.sql.Timestamp.valueOf(authResp.getExpiryTime()));
		persistenceManager.saveAPIAuthInfo(apiAuthInfo);
		return response;
	}

}

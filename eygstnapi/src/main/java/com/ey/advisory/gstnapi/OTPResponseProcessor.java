package com.ey.advisory.gstnapi;

import java.util.Date;
import java.util.Map;

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

@Component("OTPResponseProcessor")
public class OTPResponseProcessor implements APIResponseProcessor {

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Override
	public APIResponse processResponse(
						APIParams params, 
						APIConfig config, 
						APIExecParties parties, 
						APIAuthInfo authInfo,
						APIResponse response, 
						Map<String, Object> context) {
		String appKey = (String) context.get(GSTNAPIConstants.APP_KEY);
		GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
		String gstin = endUser.getGstinConfig().getGstin();
		String groupCode = endUser.getGroupConfig().getGroupCode();
		GstnAPIAuthInfo apiAuthInfo = null;
		Date curDate = new Date();
		if (authInfo != null) {
			apiAuthInfo = (GstnAPIAuthInfo) authInfo;
			apiAuthInfo.setAppKey(appKey);
			apiAuthInfo.setUpdatedDate(curDate);
		} else {
			apiAuthInfo = new GstnAPIAuthInfo();
			apiAuthInfo.setProviderName(APIProviderEnum.GSTN.name());
			apiAuthInfo.setAppKey(appKey);
			apiAuthInfo.setGstin(gstin);
			apiAuthInfo.setGroupCode(groupCode);
			apiAuthInfo.setCreatedDate(curDate);
			apiAuthInfo.setUpdatedDate(curDate);
		}
		persistenceManager.saveAPIAuthInfo(apiAuthInfo);

		return response;
	}

}

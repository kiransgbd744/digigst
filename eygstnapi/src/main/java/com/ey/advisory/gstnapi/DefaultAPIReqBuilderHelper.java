package com.ey.advisory.gstnapi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.GSTNAPIUtil;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.domain.master.GstnAPIGroupConfig;

@Component("DefaultAPIReqBuilderHelper")
public class DefaultAPIReqBuilderHelper implements APIReqBuilderHelper {

	@Autowired
	@Qualifier("GSTNTxnIdGeneratorImpl")
	private GSTNTxnIdGenerator txnIdGenerator;
	
	@Override
	public Map<String, String> setAPIHeaders(APIParams params, APIConfig config,
			APIExecParties parties, APIAuthInfo authInfo,
			Map<String, Object> context) {
	

		// Get the GSTIN from the API params.
		Map<String, String> reqHeadersMap = new HashMap<>();
		EYGSTNChannel channel = (EYGSTNChannel) parties.getApiExecChannel();
		GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
		GstnAPIGroupConfig groupConfig = endUser.getGroupConfig();
		GstnAPIGstinConfig gstnConfig = endUser.getGstinConfig();
		String gstin = gstnConfig.getGstin();
		String identifier = params.getApiIdentifier();
		String returnType = params.getAPIParamValue(APIConstants.RETURN_TYPE);
		// Headers required for GSP Gateway authentication
		reqHeadersMap.put("digigst_username", groupConfig.getDigiGstUserName());
		reqHeadersMap.put("api_key", groupConfig.getApiKey());
		reqHeadersMap.put("api_secret", groupConfig.getApiSecret());
		reqHeadersMap.put("access_token", groupConfig.getGspToken());
		reqHeadersMap.put("group_code", groupConfig.getGroupCode());
		if (!GSTNAPIUtil.isPublicApiRelatedRequest(identifier)) {
			// The first 2 characters of the GSTIN will represent the state code
			// of the GSTIN invoking this API.
			String stateCode = gstin.substring(0, 2);
			reqHeadersMap.put("state-cd", stateCode);
			reqHeadersMap.put("ip-usr", channel.getEYPublicIp());
		}
		if(GSTNAPIUtil.isNewReturnApiRequest(identifier)) {
			reqHeadersMap.put("clientid", channel.getClientId());
			reqHeadersMap.put("userrole", returnType);
			reqHeadersMap.put("api_version",
					config.getCurVersion().substring(1));
			reqHeadersMap.put("rtn_typ", returnType);
		}
		String txnId = txnIdGenerator.generateTxnId(params);
		reqHeadersMap.put("txn", txnId);
		context.put(APIContextConstants.TXN_ID_KEY, txnId);

		return reqHeadersMap;
	}

}

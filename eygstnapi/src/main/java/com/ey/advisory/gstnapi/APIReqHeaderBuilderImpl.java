package com.ey.advisory.gstnapi;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIParamType;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.GSTNAPIUtil;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIHttpReqParamConfig;
import com.ey.advisory.core.api.impl.APIReqHeaderBuilder;
import com.ey.advisory.core.api.impl.APIVersionConfig;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;

@Component("APIReqHeaderBuilderImpl")
public class APIReqHeaderBuilderImpl implements APIReqHeaderBuilder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(APIReqHeaderBuilderImpl.class);

	@Autowired
	@Qualifier("DefaultAPIReqBuilderHelper")
	private APIReqBuilderHelper reqBuilderHelper;

	@Override
	public Map<String, String> buildReqHeaderMap(APIParams params,
			APIConfig config, APIExecParties parties, APIAuthInfo authInfo,
			Map<String, Object> context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "About to build the Request Header Map";
			LOGGER.debug(msg);
		}
		String identifier = params.getApiIdentifier();
		GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
		GstnAPIGstinConfig gstnConfig = endUser.getGstinConfig();

		Map<String, String> reqHeadersMap = reqBuilderHelper
				.setAPIHeaders(params, config, parties, authInfo, context);

		if (!GSTNAPIUtil.isAuthRelatedRequest(identifier)) {
			GstnAPIAuthInfo apiAuthInfo = (GstnAPIAuthInfo) authInfo;
			String authTokenKey = GSTNAPIUtil.isPublicApiRelatedRequest(
					identifier) ? "auth_token" : "auth-token";
			reqHeadersMap.put(authTokenKey, apiAuthInfo.getGstnToken());
			if (!GSTNAPIUtil.isPublicApiRelatedRequest(identifier)) {
				reqHeadersMap.put("username", gstnConfig.getGstinUserName());
			}
		}

		APIVersionConfig versionConfig = config
				.getConfigForVersion(params.getApiVersion());
		List<APIHttpReqParamConfig> paramsList = versionConfig
				.getExpectedHeaders();
		for (APIHttpReqParamConfig paramConfig : paramsList) {
			String paramVal = params.getAPIParamValue(paramConfig.getName(),
					APIParamType.HEADER);
			if (paramConfig.isMandatory() && paramVal == null) {
				throw new APIException("Mandatory Request Header is "
						+ "Missing " + paramConfig.getName());
			}
			if(paramVal != null)
				reqHeadersMap.put(paramConfig.getName(), paramVal);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = "Built the Request Header Map.";
			LOGGER.debug(msg);
		}

		return reqHeadersMap;
	}

}

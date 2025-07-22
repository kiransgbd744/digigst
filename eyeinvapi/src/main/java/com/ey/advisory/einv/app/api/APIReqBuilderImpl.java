package com.ey.advisory.einv.app.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;

@Component
public class APIReqBuilderImpl implements APIReqBuilder {

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public Map<String, String> buildReqHeaderMap(APIParams params,
			String version, APIConfig config, APIExecParties parties,
			Map<String, Object> context) {

		// Get the expected header list from the configuration.
		// Validate if the mandatory header parameters are present in the
		// APIParams object. If not throw exception.

		// At this point all required headers that need to be provided by the
		// caller will be present in the APIParams object

		// Get the GSTIN from the API params.
		String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
		Map<String, String> reqHeadersMap = new HashMap<>();
		String actionIdentifier = params.getApiAction();
		String apiProvider = CommonUtil.getApiProvider(params);
		Pair<String, String> channelPair = getClientIdAndSecret(parties,
				apiProvider);
		Map<String, Config> confMap = configManager.getConfigs(apiProvider,
				"nic.gsp.api");
		String apiKey = confMap.containsKey("nic.gsp.api.key")
				? confMap.get("nic.gsp.api.key").getValue() : null;
		String apiSecret = confMap.containsKey("nic.gsp.api.secret")
				? confMap.get("nic.gsp.api.secret").getValue() : null;
		String username = confMap.containsKey("nic.gsp.api.username")
				? confMap.get("nic.gsp.api.username").getValue() : null;
		if (apiKey == null || apiSecret == null || username == null)
			throw new APIException(
					"ASP Gateway's API key,Secret,UserName are not configured");
		reqHeadersMap.put("username", username);
		reqHeadersMap.put("api_key", apiKey);
		reqHeadersMap.put("api_secret", apiSecret);
		reqHeadersMap.put("client_id", channelPair.getValue0());
		reqHeadersMap.put("client_secret", channelPair.getValue1());
		reqHeadersMap.put("Content-Type", "application/json");
		reqHeadersMap.put("Connection", "Keep-Alive");
		APIVersionConfig versionConfig = config.getConfigForVersion(version);
		NICAPIEndUser endUser = (NICAPIEndUser) parties.getApiEndUser();
		boolean isEwbUserIdReq = versionConfig.isEwbUserIdReq();
		List<APIHttpReqParamConfig> paramsList = versionConfig.expectedHeaders;
		for (APIHttpReqParamConfig paramConfig : paramsList) {
			if (paramConfig.isMandatory()) {
				if (params.getApiParamValue(paramConfig.getName()) == null) {
					throw new APIException("Mandatory Request Header is "
							+ "Missing " + paramConfig.getName());
				}
			}
			reqHeadersMap.put(paramConfig.getName(),
					params.getApiParamValue(paramConfig.getName()));
		}
		if (actionIdentifier.equals(APIIdentifiers.GET_EINV_AUTH_TOKEN)) {
			if (isEwbUserIdReq) {
				reqHeadersMap.put("ewb-user-id", "");
			}
		} else {
			GstnAPIAuthInfo apiAuthInfo = persistenceManager
					.loadAPIAuthInfo(gstin, CommonUtil.getApiProviderEnum(params).name());
			if (apiAuthInfo != null && !apiAuthInfo.isExpired()) {
				reqHeadersMap.put("authtoken", apiAuthInfo.getGstnToken());
				reqHeadersMap.put("user_name", endUser.getNicUserName());
			} else {
				throw new AuthTokenExpiredException("Auth Token has expired");
			}
		}
		return reqHeadersMap;
	}

	@Override
	public Map<String, String> buildQueryParamsMap(APIParams params,
			String version, APIConfig config, APIExecParties parties,
			Map<String, Object> context) {
		Map<String, String> queryParamsMap = null;
		APIVersionConfig versionConfig = config.getConfigForVersion(version);
		List<APIHttpReqParamConfig> paramsList = versionConfig.expectedUrlParams;
		queryParamsMap = new HashMap<>();
		for (APIHttpReqParamConfig paramConfig : paramsList) {
			if (paramConfig.isMandatory()) {
				if (params.getApiParamValue(paramConfig.getName()) == null) {
					throw new APIException("Mandatory Request Param is "
							+ "Missing " + paramConfig.getName());
				}
			}
			queryParamsMap.put(paramConfig.getName(),
					params.getApiParamValue(paramConfig.getName()));
		}
		return queryParamsMap;
	}

	private Pair<String, String> getClientIdAndSecret(APIExecParties parties,
			String apiProvider) {
		Config config = configManager.getConfig(apiProvider,
				"api.nic.id(all).act_as_enduser");
		boolean isActingAsEndUSer = config != null
				&& config.getValue().equalsIgnoreCase("true");
		if (isActingAsEndUSer) {
			NICAPIEndUser endUser = (NICAPIEndUser) parties.getApiEndUser();
			return new Pair<>(endUser.getClientId(), endUser.getClientSecret());
		} else {
			EYNICChannel channel = (EYNICChannel) parties.getApiExecChannel();
			return new Pair<>(channel.getClientId(), channel.getClientSecret());
		}
	}

	@Override
	public Map<String, String> buildPathParamsMap(APIParams params,
			String version, APIConfig config, APIExecParties parties,
			Map<String, Object> context) {
		Map<String, String> pathParamsMap = null;
		APIVersionConfig versionConfig = config.getConfigForVersion(version);
		List<APIHttpReqParamConfig> paramsList = versionConfig.expectedPathParams;
		pathParamsMap = new HashMap<>();
		for (APIHttpReqParamConfig paramConfig : paramsList) {
			pathParamsMap.put(paramConfig.getName(),
					params.getApiParamValue(paramConfig.getName()));
		}
		return pathParamsMap;
	}
}

package com.ey.advisory.gstnapi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIConfigBuilder;
import com.ey.advisory.core.api.impl.APIVersionConfig;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

@Component("APIConfigBuilderImpl")
public class APIConfigBuilderImpl implements APIConfigBuilder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(APIConfigBuilderImpl.class);

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;
	
	private String loadAPILevelProperty(String apiId, String propName,
			Map<String, Config> genMap, Map<String, Config> specMap) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(
					"Generic Map for Identifier {} is {}, Specific Map is {}",
					apiId, genMap, specMap);

		// Frame the key specific to the specified API.
		String apiSpecificKey = String.format("api.gstn.id(%s).%s", apiId,
				propName);
		// Frame the key that is generic to all APIs.
		String genKey = String.format("api.gstn.id(all).%s", propName);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(
					"Generic Key for Identifier {} is {}, Specific Key is {}",
					apiId, genKey, apiSpecificKey);
		// Get the key specific to the specified API.
		Config val = specMap.get(apiSpecificKey);
		if (val != null) {
			return val.getValue();
		}

		// Get the generic key.
		val = genMap.get(genKey);
		return val != null ? val.getValue() : null;
	}

	private String loadVersionLevelProperty(String apiId, String version,
			String propName, Map<String, Config> genMap,
			Map<String, Config> specMap) {
		String versionSpecificKey = String.format("api.gstn.id(%s).v(%s).%s",
				apiId, version, propName);
		// Frame the key specific to the specified API.
		String apiSpecificKey = String.format(
				"api.gstn.id(%s).v(all).%s", apiId, propName);

		// Get the key for the specific version of all apis.
		String genVersionKey = String.format("api.gstn.id(all).v(%s).%s",
				version, propName);

		// Frame the key that is generic to all APIs.
		String genKey = String.format("api.gstn.id(all).v(all).%s", propName);

		// Get the key specific to the specified API and the specified version.
		Config val = specMap.get(versionSpecificKey);
		if (val != null) {
			return val.getValue();
		}

		// Get the key specific to the specified API, for all versions.
		val = specMap.get(apiSpecificKey);
		if (val != null) {
			return val.getValue();
		}

		// Get the key generic to all APIs, for the specified version.
		// This might be a very rare case.
		val = genMap.get(genVersionKey);
		if (val != null) {
			return val.getValue();
		}

		// Get the key generic to all APIs and all versions.
		val = genMap.get(genKey);
		return val != null ? val.getValue() : null;
	}

	@Override
	public APIConfig build(APIParams params) {

		String id = params.getApiIdentifier();

		// First get the generic map for all apis. This call will
		// return API level configuration as well as API version level
		// configuration, but only those that are applicable for all
		// apis.
		Map<String, Config> genMap = this.configManager.getConfigs("GSTNAPI",
				"api.gstn.global");

		// Now, get the map specific to the current API that we are executing.
		// This map contains all configurations specific to different versions
		// of the API that we are currently executing.
		String startsWith = String.format("api.gstn.id(%s)", id);
		Map<String, Config> specMap = this.configManager.getConfigs("GSTNAPI",
				startsWith);

		String action = loadAPILevelProperty(id, "action", genMap, specMap);
		String activeVersions = loadAPILevelProperty(id, "active_versions",
				genMap, specMap);
		String curVersion = loadAPILevelProperty(id, "current_version", genMap,
				specMap);

		// check if both are not null and empty. Otherwise throw an exception
		if ((curVersion == null) || curVersion.trim().isEmpty()) {
			throw new APIConfigException(String.format(
					"curVersion is not configured for the API: %s", id));
		}

		if ((activeVersions == null) || activeVersions.trim().isEmpty()) {
			throw new APIConfigException(String.format(
					"activeVersions is not configured for the API: %s", id));
		}

		// Build a new API configuration object from the properties loaded.
		APIConfig apiConfig = new APIConfig(id, curVersion, action);

		if ((action == null) || action.trim().isEmpty()) {
			if (LOGGER.isInfoEnabled()) {
				String msg = String
						.format("API Action not configured for ApiId = '%s'. "
								+ "Using Identifier as Action", id);
				LOGGER.info(msg);
			}
			apiConfig.setAction(id);
		}
		// Get all the active versions.
		List<String> versions = Arrays.asList(activeVersions.split(";"));

		for (String version : versions) {

			String baseUrl = loadVersionLevelProperty(id, version, "base_url",
					genMap, specMap);
			String expectedHdrs = loadVersionLevelProperty(id, version,
					"expected_hdrs", genMap, specMap);
			String expUrlParams = loadVersionLevelProperty(id, version,
					"expected_urlparams", genMap, specMap);
			String httpMethod = loadVersionLevelProperty(id, version,
					"http_method", genMap, specMap);

			if ((baseUrl == null) || baseUrl.trim().isEmpty()) {
				throw new APIConfigException(String.format(
						"baseUrl is not configured for the API: %s", id));
			}

			if ((httpMethod == null) || httpMethod.trim().isEmpty()) {
				throw new APIConfigException(String.format(
						"httpMethod is not configured for the API: %s", id));
			}

			APIVersionConfig vConfig = new APIVersionConfig(baseUrl.trim(),
					httpMethod.trim());

			if ((expectedHdrs != null) && !expectedHdrs.trim().isEmpty()) {

				List<String> expHdrList = Arrays
						.asList(expectedHdrs.split(","));
				for (String expHdr : expHdrList) {
					String[] arr = expHdr.split(":");
					if (arr.length == 1) {
						vConfig.addExpectedHeader(arr[0], false);
					} else {
						vConfig.addExpectedHeader(arr[0],
								arr[1].trim().equalsIgnoreCase("M"));
					}
				}
			}

			if ((expUrlParams != null) && !expUrlParams.trim().isEmpty()) {

				List<String> expUrlParamsList = Arrays
						.asList(expUrlParams.split(","));
				for (String expUrlParam : expUrlParamsList) {
					String[] arr = expUrlParam.split(":");
					if (arr.length == 1) {
						vConfig.addExpectedUrlParam(arr[0], false);
					} else {
						vConfig.addExpectedUrlParam(arr[0],
								arr[1].trim().equalsIgnoreCase("M"));
					}
				}
			}

			// Add the version config to the API Config object.
			apiConfig.addVersionConfig(version, vConfig);

		}

		return apiConfig;

	}

}

package com.ey.advisory.einv.app.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class APIExecConfigBuilderImpl implements APIExecConfigBuilder {

	private APIEINVChannelAndEndUserBuilder channelAndEndUserBuilder;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public APIExecConfigBuilderImpl() {
	}

	@Override
	public APIExecParties buildAPIPartiesInfo(APIParams params) {

		BufferedReader bufferedReader = null;
		APIExecParties apiExecParties = null;
		FileInputStream fileInputStream = null;

		try {
			String apiProvider = CommonUtil.getApiProviderEnum(params).name();
			ClassLoader classLoader = getClass().getClassLoader();
			URL templateDir = classLoader.getResource(
					apiProvider.equalsIgnoreCase(APIProviderEnum.EYEINV.name())
							? "certificates/eyeinv_sandbox.pem"
							: "certificates/einv_sandbox.pem");
			File file = new File(templateDir.toURI());
			fileInputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(
					new InputStreamReader(fileInputStream));
			StringBuilder builder = new StringBuilder();
			String temp = null;
			while ((temp = bufferedReader.readLine()) != null) {
				builder.append(temp);
			}
			temp = builder.toString();
			LOGGER.info("public key before replace: " + temp + " @path: "
					+ file.getAbsolutePath());
			String publicKeyPEM = temp.replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "").replace("\n", "");
			LOGGER.info("public key: " + publicKeyPEM);
			String apiCategory = CommonUtil.getApiProvider(params);
			Config config = configManager.getConfig(apiCategory,
					"api.nic.id(all).act_as_enduser");
			boolean isActingAsEndUSer = config != null
					&& config.getValue().equalsIgnoreCase("true");
			if (isActingAsEndUSer) {
				channelAndEndUserBuilder = StaticContextHolder.getBean(
						"EmptyChannelAndValidEndUserBuilder",
						APIEINVChannelAndEndUserBuilder.class);
			} else {
				channelAndEndUserBuilder = StaticContextHolder.getBean(
						"ValidChannelAndValidEndUserBuilder",
						APIEINVChannelAndEndUserBuilder.class);
			}
			NICAPIEndUser nicApiEndUSer = channelAndEndUserBuilder
					.buildEndUser(params);
			if (isActingAsEndUSer && (nicApiEndUSer.getClientId() == null
					|| nicApiEndUSer.getClientSecret() == null)) {
				throw new APIConfigException(
						"ClientID and Secret Cannot be NULL,"
								+ " when Act as End User Flag is set to true");
			}
			NICAPIProvider nicApiProvider = new NICAPIProvider(publicKeyPEM);

			EYNICChannel eyNIcChannel = channelAndEndUserBuilder
					.buildChannel(params);
			apiExecParties = new APIExecParties(nicApiProvider, eyNIcChannel,
					nicApiEndUSer);
		} catch (Exception ex) {
			String errorMsg = "Exception while building auth info";
			LOGGER.error(errorMsg, ex);
			throw new APIException(ex.getMessage());
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
					fileInputStream.close();
				} catch (IOException e) {
					String errorMsg = "Exception while closing buffered Reader";
					LOGGER.error(errorMsg, e);
				}
			}
		}
		return apiExecParties;

	}

	private String loadAPILevelProperty(String apiId, String propName,
			Map<String, Config> genMap, Map<String, Config> specMap) {
		// Frame the key specific to the specified API.
		String apiSpecificKey = String.format("api.nic.id(%s).%s", apiId,
				propName);
		// Frame the key that is generic to all APIs.
		String genKey = String.format("api.nic.id(all).%s", propName);

		// Get the key specific to the specified API.
		String val = specMap.get(apiSpecificKey).getValue();
		if (val != null) {
			return val;
		}

		// Get the generic key.
		val = genMap.get(genKey).getValue();
		return val;
	}

	private String loadVersionLevelProperty(String apiId, String version,
			String propName, Map<String, Config> genMap,
			Map<String, Config> specMap) {
		String versionSpecificKey = String.format("api.nic.id(%s).v(%s).%s",
				apiId, version, propName);
		// Frame the key specific to the specified API.
		String apiSpecificKey = String.format("api.nic.id(%s).v(all).%s", apiId,
				propName);

		// Get the key for the specific version of all apis.
		String genVersionKey = String.format("api.nic.id(all).v(%s).%s",
				version, propName);

		// Frame the key that is generic to all APIs.
		String genKey = String.format("api.nic.id(all).v(all).%s", propName);

		// Get the key specific to the specified API and the specified version.
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
		return val.getValue();
	}

	@Override
	public APIConfig buildAPIConfig(APIParams params) {

		String id = params.getApiAction();
		String apiProvider = CommonUtil.getApiProvider(params);
		// First get the generic map for all apis. This call will
		// return API level configuration as well as API version level
		// configuration, but only those that are applicable for all
		// apis.

		Map<String, Config> genMap = this.configManager.getConfigs(apiProvider,
				"api.nic.id(all)");

		// Now, get the map specific to the current API that we are executing.
		// This map contains all configurations specific to different versions
		// of the API that we are currently executing.
		String startsWith = String.format("api.nic.id(%s)", id);
		Map<String, Config> specMap = this.configManager.getConfigs(apiProvider,
				startsWith);

		String action = "";
		String activeVersions = loadAPILevelProperty(id, "active_versions",
				genMap, specMap);
		String curVersion = loadAPILevelProperty(id, "current_version", genMap,
				specMap);

		// check if both are not null and empty. Otherwise throw an exception
		/*
		 * if ((curVersion == null) || curVersion.trim().isEmpty()) { throw new
		 * APIConfigException(String.format(
		 * "curVersion is not configured for the API: %s", id)); }
		 * 
		 * if ((activeVersions == null) || activeVersions.trim().isEmpty()) {
		 * throw new APIConfigException(String.format(
		 * "activeVersions is not configured for the API: %s", id)); }
		 */

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
			String expPathParams = loadVersionLevelProperty(id, version,
					"expected_pathparams", genMap, specMap);
			/*
			 * String urlSuffix = loadVersionLevelProperty(id, version,
			 * "url_suffix", genMap, specMap);
			 */
			String isVersionIncludedInUrl = loadVersionLevelProperty(id,
					version, "include_version_in_url", genMap, specMap);
			String isEwbUserIdReq = loadVersionLevelProperty(id, version,
					"is_ewb_userid_req", genMap, specMap);

			if ((baseUrl == null) || baseUrl.trim().isEmpty()) {
				throw new APIConfigException(String.format(
						"baseUrl is not configured for the API: %s", id));
			}

			/*
			 * if ((urlSuffix == null) || urlSuffix.trim().isEmpty()) { throw
			 * new APIConfigException(String.format(
			 * "urlSuffix is not configured for the API: %s", id)); }
			 */

			if ((isVersionIncludedInUrl == null)
					|| isVersionIncludedInUrl.trim().isEmpty()) {
				throw new APIConfigException(String
						.format("isVersionIncludedInUrl is not configured "
								+ "for the API: %s", id));
			}

			if ((isEwbUserIdReq == null) || isEwbUserIdReq.trim().isEmpty()) {
				throw new APIConfigException(String.format(
						"isEwbUserIdReq is not configured for the API: %s",
						id));
			}

			if ((httpMethod == null) || httpMethod.trim().isEmpty()) {
				throw new APIConfigException(String.format(
						"httpMethod is not configured for the API: %s", id));
			}

			APIVersionConfig vConfig = new APIVersionConfig(baseUrl.trim(), "",
					httpMethod.trim(),
					Boolean.parseBoolean(
							isVersionIncludedInUrl.trim().toLowerCase()),
					Boolean.parseBoolean(isEwbUserIdReq.trim().toLowerCase()));

			if ((expectedHdrs != null) && !expectedHdrs.trim().isEmpty()) {
				List<String> expHdrList = Arrays
						.asList(expectedHdrs.split(";"));
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
						.asList(expUrlParams.split(";"));
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
			if ((expPathParams != null) && !expPathParams.trim().isEmpty()) {

				List<String> expPathParamsList = Arrays
						.asList(expPathParams.split(";"));
				for (String expPathUrlParam : expPathParamsList) {
					String[] arr = expPathUrlParam.split(":");
					if (arr.length == 1) {
						vConfig.addExpectedPathParam(arr[0], false);
					} else {
						vConfig.addExpectedPathParam(arr[0],
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

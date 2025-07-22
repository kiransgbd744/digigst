package com.ey.advisory.ewb.app.api;

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

import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;
import com.ey.advisory.gstnapi.repositories.client.EWBNICUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EWBAPIExecConfigBuilderImpl")
public class EWBAPIExecConfigBuilderImpl implements APIExecConfigBuilder {

	@Autowired
	private EWBNICUserRepository eWBNICUserRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public EWBAPIExecConfigBuilderImpl() {
	}

	@Override
	public APIExecParties buildAPIPartiesInfo(APIParams params) {

		BufferedReader bufferedReader = null;
		APIExecParties apiExecParties = null;
		FileInputStream fileInputStream = null;

		try {
			String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Fetching EWB GSTIN Details for GSTIN {}", gstin);
			EWBNICUser nicUser = eWBNICUserRepository.findByGstin(gstin);
			ClassLoader classLoader = getClass().getClassLoader();
			if (nicUser == null) {
				String errMsg = String.format(
						"GSTIN User Details for '%s' are not configured",
						gstin);
				LOGGER.error(errMsg);
				throw new APIException(errMsg);
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Fetched EWB GSTIN Details for GSTIN {}", gstin);
			URL templateDir = classLoader
					.getResource("certificates/einv_sandbox.pem");
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
			String publicKeyPEM = temp.replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "").replace("\n", "");
			String nicUserName = nicUser.getNicUserName();
			String nicPassword = nicUser.getNicPassword();
			String clientId = nicUser.getClientId();
			String clientSecret = nicUser.getClientSecret();
			NICAPIEndUser nicApiEndUSer = new NICAPIEndUser(gstin, nicUserName,
					nicPassword);
			NICAPIProvider nicApiProvider = new NICAPIProvider(publicKeyPEM);

			EYNICChannel eyNIcChannel = new EYNICChannel(clientId, clientSecret,
					null);
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
	public APIConfig buildAPIConfig(APIParams params) {

		String id = params.getApiAction();

		Map<String, Config> genMap = configManager.getConfigs("EWBAPI",
				"api.nic.id(all)");

		// Now, get the map specific to the current API that we are executing.
		// This map contains all configurations specific to different versions
		// of the API that we are currently executing.
		String startsWith = String.format("api.nic.id(%s)", id);
		Map<String, Config> specMap = configManager.getConfigs("EWBAPI",
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
			String urlSuffix = loadVersionLevelProperty(id, version,
					"url_suffix", genMap, specMap);
			String isVersionIncludedInUrl = loadVersionLevelProperty(id,
					version, "include_version_in_url", genMap, specMap);
			String isEwbUserIdReq = loadVersionLevelProperty(id, version,
					"is_ewb_userid_req", genMap, specMap);

			if ((baseUrl == null) || baseUrl.trim().isEmpty()) {
				throw new APIConfigException(String.format(
						"baseUrl is not configured for the API: %s", id));
			}

			if ((urlSuffix == null) || urlSuffix.trim().isEmpty()) {
				throw new APIConfigException(String.format(
						"urlSuffix is not configured for the API: %s", id));
			}

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

			APIVersionConfig vConfig = new APIVersionConfig(baseUrl.trim(),
					urlSuffix.trim(), httpMethod.trim(),
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
								arr[1].trim().equals("M"));
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

			// Add the version config to the API Config object.
			apiConfig.addVersionConfig(version, vConfig);

		}

		return apiConfig;

	}
}

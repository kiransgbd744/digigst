package com.ey.advisory.einv.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommonUtil {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private final String EYIRP = APIProviderEnum.EYEINV.name();
	private final String NIC = APIProviderEnum.EINV.name();

	public static String getApiProvider(APIParams params) {
		APIProviderEnum apiProviderEnum = params.getApiProvider();
		String apiProvider = apiProviderEnum.name();
		return apiProvider.equalsIgnoreCase(APIReqParamConstants.EYEINVAPI)
				? APIReqParamConstants.EY_EINV_CATEGORY
				: APIReqParamConstants.EINV_CATEGORY;
	}

	public static APIProviderEnum getApiProviderEnum(APIParams params) {
		APIProviderEnum apiProviderEnum = params.getApiProvider();
		String apiProvider = apiProviderEnum.name();
		return apiProvider.equalsIgnoreCase(APIReqParamConstants.EYEINVAPI)
				? APIProviderEnum.EYEINV : APIProviderEnum.EINV;
	}

	
	public static APIProviderEnum getApiProviderEnum(String param) {
		String apiProvider = param;
		return apiProvider.equalsIgnoreCase(APIReqParamConstants.EYEINVAPI)
				? APIProviderEnum.EYEINV : APIProviderEnum.EINV;
	}

	public APIProviderEnum getSource(String gstin) {
		APIProviderEnum apiENum = APIProviderEnum.EINV;
		Map<String, Config> configMap = configManager.getConfigs("EINVSource",
				"einv.source", TenantContext.getTenantId());

		String gstinSpecificKey = String.format("einv.source.enabled.%s",
				gstin);
		String groupSpecificKey = "einv.source.enabled";
		String apiIdentifier = null;
		if (configMap.containsKey(gstinSpecificKey)) {
			apiIdentifier = configMap.get(gstinSpecificKey).getValue();

		} else if (configMap.containsKey(groupSpecificKey)) {
			apiIdentifier = configMap.get(groupSpecificKey).getValue();
		} else {
			String msg = String.format(
					"No Configuration available either GSTN %s or Group %s Specific,"
					+ " Hence going with the default source",
					gstin, TenantContext.getTenantId());
			LOGGER.debug(msg);
			apiIdentifier = APIProviderEnum.EINV.name();
		}

		if (APIProviderEnum.EYEINV.name().equalsIgnoreCase(apiIdentifier)) {
			apiENum = APIProviderEnum.EYEINV;
		}
		return apiENum;
	}

	public List<String> getErrorCodes(String providerName, String apiType) {

		Map<String, Config> configMap = configManager.getConfigs("GETEWB",
				"eligible.getewb", "DEFAULT");

		List<String> errorCodeList = new ArrayList<>();

		String configKey = null;

		if (apiType.equalsIgnoreCase(APIIdentifiers.CANCEL_EINV)
				&& EYIRP.equalsIgnoreCase(providerName)) {

			configKey = "eyirp.can.errorcodes";
		} else if (apiType.equalsIgnoreCase(APIIdentifiers.CANCEL_EINV)
				&& NIC.equalsIgnoreCase(providerName)) {

			configKey = "nic.can.errorcodes";
		}

		if (EYIRP.equalsIgnoreCase(providerName)) {

			String errorCodes = "2148,2143";
			errorCodeList = Arrays.asList(errorCodes.split(","));
		} else {
			// String errorCodes = configMap != null
			// && configMap.get(configKey) != null
			// ? configMap.get(configKey).getValue() : null;
			String errorCodes = "2148,2143";

			errorCodeList = Arrays.asList(errorCodes.split(","));
		}
		return errorCodeList;

	}

}

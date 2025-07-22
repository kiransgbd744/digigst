package com.ey.advisory.common.counter.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

@Component
public class GSTNPublicApiLimitFetcher {

	public static final Integer DEFAULT_LIMIT = 1000;

	public static final String CONF_KEY = "api.gstn.publicapi.limit";

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public int getDefaultLimit(String groupCode) {
		Map<String, Config> configMap = configManager.getConfigs("GSTNAPI",
				CONF_KEY, groupCode);
		return configMap.get(CONF_KEY) == null ? DEFAULT_LIMIT
				: Integer.valueOf(configMap.get(CONF_KEY).getValue());
	}

}

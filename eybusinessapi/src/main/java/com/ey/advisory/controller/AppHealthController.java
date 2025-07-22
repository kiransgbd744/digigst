package com.ey.advisory.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

/**
 * @author Siva.Reddy
 *
 */
@RestController
public class AppHealthController {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@RequestMapping("/api/healthCheck")
	public String handler() {

		Map<String, Config> regionconfigMap = configManager.getConfigs("BCAPI",
				"auto.drafting", "DEFAULT");
		String activeRegion = regionconfigMap
				.containsKey("auto.drafting.region")
						? regionconfigMap.get("auto.drafting.region").getValue()
						: "AMD";
		activeRegion = activeRegion.equalsIgnoreCase("FF") ? "Frankfurt"
				: "Amsterdam";
		return "{\"msg\": \"App is UP and RUNNING\",\"region\": \""
				+ activeRegion + "\"}";
	}

}

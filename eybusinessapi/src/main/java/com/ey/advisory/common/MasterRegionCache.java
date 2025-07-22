package com.ey.advisory.common;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Component("MasterRegionCache")
@Slf4j
public class MasterRegionCache {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, String> regionMap = new HashMap<String, String>();

	@PostConstruct
	private void init() {
		loadRegion();
	}

	private void loadRegion() {
		try {
			Map<String, Config> regionconfigMap = configManager
					.getConfigs("BCAPI", "auto.drafting", "DEFAULT");
			String activeRegion = regionconfigMap
					.containsKey("auto.drafting.region")
							? regionconfigMap.get("auto.drafting.region")
									.getValue()
							: "FF";
			regionMap.put("Region", activeRegion);
		} catch (Exception ex) {
			String msg = "Error occurred while loading the region. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public String findActiveRegion() {
		return regionMap.getOrDefault("Region", "FF");
	}
}

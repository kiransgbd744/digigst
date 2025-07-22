package com.ey.advisory.core.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 
 * This class is responsible for checking if a config property is overrided
 * using the local property file. If so, this class returns the overridden 
 * value. Otherwise, it returns the original value. This class uses both the
 * base config manager (responsible for getting the original values) and the
 * override config manager (responsible for reading overridden config values)
 * from the local property file. 
 * 
 * The overrides will be important in developer systems, as each developer 
 * may want to try a different config value than the centralized one. Values
 * of usernames/passwords can be an example. Even though this will be frequently
 * used in DEV systems, this can be made use of, in the PROD systems as well
 * to have individual deployment instance have individual set of overridden
 * properties (althouth this is not so common, practically).
 * 
 * @author Sai.Pakanati
 *
 */
@Component("ConfigManagerImpl")
public class ConfigManagerImpl implements ConfigManager {

	@Autowired
	@Qualifier("BaseConfigManager")
	private ConfigManager baseConfigMngr;
	
	@Autowired
	@Qualifier("ConfigOverrider")
	private ConfigManager overrideConfigMngr;
	
	@Override
	public Config getConfig(String category, String key) {
		return getConfig(category, key, ConfigConstants.DEFAULT_GROUP);
	}

	@Override
	public Config getConfig(String category, String key, String groupCode) {
		
		// First load the override configuration. If it is non-null, then
		// return it. 
		Config overrideCfg = overrideConfigMngr.getConfig(
						category, key, groupCode);
		if (overrideCfg != null) {
			return overrideCfg;
		}
		
		// If the override configuration doesn't have any value attached, then
		// load the base configuration and return it.
		return baseConfigMngr.getConfig(category, key, groupCode);		

	}

	@Override
	public Map<String, Config> getConfigs(String category,
			String keyStartsWith) {
		
		return getConfigs(category, keyStartsWith,
				ConfigConstants.DEFAULT_GROUP);
	}

	@Override
	public Map<String, Config> getConfigs(String category, String keyStartsWith,
			String groupCode) {
		
		// First load the Configs from the base config manager. 
		Map<String, Config> baseConfigs = baseConfigMngr.getConfigs(
					category, keyStartsWith, groupCode);
		
		// Then load the configs from the override config manager.
		Map<String, Config> overrideConfigs = overrideConfigMngr.getConfigs(
				category, keyStartsWith, groupCode);
		
		// Merge all values from override configs, into to Base Config. This
		// will override the values that are already present in the base config
		// and replace it with the value in the override config.
		overrideConfigs.forEach(
				  (key, value) -> baseConfigs.merge(
						  key, value, (baseVal, overrideVal) -> overrideVal));
		
		return baseConfigs;
	}



}

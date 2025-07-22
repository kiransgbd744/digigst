package com.ey.advisory.core.config;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.async.domain.master.EYRegularConfig;
import com.ey.advisory.core.async.domain.master.EYSensitiveConfig;
import com.ey.advisory.core.async.repositories.master.EYConfigRepository;
import com.ey.advisory.core.async.repositories.master
						.EYSensitiveConfigRepository;

/**
 * This configuration manager loads the configuration properties from the 
 * database. Currently, caching of the config values are not implemented. 
 * 
 * @author Sai.Pakanati
 *
 */

@Component("BaseConfigManager")
public class BaseConfigManager implements ConfigManager {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConfigManagerImpl.class);

	@Autowired
	private EYConfigRepository eyConfigRepository;

	@Autowired
	private EYSensitiveConfigRepository eySensitiveConfigRepository;

	@Override
	public Config getConfig(String category, String key) {

		EYSensitiveConfig sConfig = eySensitiveConfigRepository
				.findByCategoryAndKey(category, key);
		if (sConfig != null) {
			return new Config(sConfig.getId(), ConfigType.Sensitive, 
					sConfig.getGroupCode(), sConfig.getCategory(), 
					sConfig.getKey(), sConfig.getValue());
		}
		EYRegularConfig rConfig = eyConfigRepository
				.findByCategoryAndKey(category, key);
				
		if (rConfig != null) {
			return new Config(rConfig.getId(), ConfigType.Regular, 
					rConfig.getGroupCode(), rConfig.getCategory(), 
					rConfig.getKey(), rConfig.getValue());	
		}

		return null;
	}

	@Override
	public Config getConfig(String category, String key, String groupCode) {

		Config config = null;
		EYSensitiveConfig sConfig = null; // Sensitive Configuration.
		EYRegularConfig rConfig = null; // Regular Confuguration (Not sensitive)
		sConfig = eySensitiveConfigRepository
				.findByCategoryAndKeyAndGroupCode(category, key, groupCode);
		if (sConfig != null) {
			config = new Config();
			config.setId(sConfig.getId());
			config.setCategory(sConfig.getCategory());
			config.setKey(sConfig.getKey());
			config.setValue(sConfig.getValue());
			config.setType(ConfigType.Sensitive);
		} else {
			rConfig = eyConfigRepository
					.findByCategoryAndKeyAndGroupCode(category, key, groupCode);
			if (rConfig != null) {
				config = new Config();
				config.setId(rConfig.getId());
				config.setCategory(rConfig.getCategory());
				config.setKey(rConfig.getKey());
				config.setValue(rConfig.getValue());
				config.setType(ConfigType.Regular);
			}
		}
		return config;
	}

	@Override
	public Map<String, Config> getConfigs(String category,
			String keyStartsWith) {
		return getConfigs(category, keyStartsWith,
				ConfigConstants.DEFAULT_GROUP);
	}

	@Override
	public Map<String, Config> getConfigs(String category, 
				String keyStartsWith, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Loading Non-Sensitive Configurations "
					+ "with Config Key Prefix  = '%s'", keyStartsWith));					
		}
		
		Stream<Config> ewbConfigList = eyConfigRepository
				.findEWBConfigsByKeyPatternAndGroupCode(
						category, keyStartsWith, groupCode)
					.stream()
					.map(item -> 
						new Config(item.getId(), ConfigType.Regular,
								item.getGroupCode(), 
								item.getCategory(), item.getKey(),
								item.getValue()));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Loading Sensitive Configurations "
					+ "with Config Key Prefix  = '%s'", keyStartsWith));					
		}
		Stream<Config> ewbSensitiveConfigList = eySensitiveConfigRepository
				.findEWBConfigsByKeyPatternAndGroupCode(
						category, keyStartsWith, groupCode)
				.stream()
				.map(item -> 
					new Config(item.getId(), ConfigType.Sensitive,
						item.getGroupCode(), 
						item.getCategory(), item.getKey(),
						item.getValue()));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Merged Non-Sensitive and Sensitive "
					+ "Configurations with Config Key Prefix  = '%s'", 
					keyStartsWith));					
		}
		
		return Stream.concat(ewbConfigList, ewbSensitiveConfigList)
				.collect(Collectors.toMap(
						Config::getKey, 
						Function.identity(),
						(firstVal, secondVal) -> secondVal));
	}
}

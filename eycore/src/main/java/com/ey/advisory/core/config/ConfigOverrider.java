package com.ey.advisory.core.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

/**
 * This class is responsible for loading configurations from a property file
 * named config-overrides.properties. This is usually required in a 
 * development environment where the central database values need to be 
 * overridden using local values. For example, there is a proxy user name and
 * proxy password property which is required for executing the GSTN API. 
 * Instead of entering the username/password in the shared database, the 
 * developer can change the config-overrides.properties file to include these
 * values and execute the api. 
 * 
 * @author Sai.Pakanati
 *
 */
@Component("ConfigOverrider")
public class ConfigOverrider implements ConfigManager {

	private static final Logger LOGGER = 
				LoggerFactory.getLogger(ConfigOverrider.class);
	
	/**
	 * The configuration overrides property file name relative to the
	 * classpath.
	 */
	private static final String CONFIG_ORVERRIDES_PATH = 
				"config-overrides.properties";
	
	/**
	 * Configuration overrides.
	 */
	private Properties properties = new Properties();
	
	/**
	 * Load the properties after post construct.
	 */
	@PostConstruct
	public void loadProperties() {		
        try (InputStream is = this.getClass().getClassLoader()
        			.getResourceAsStream(CONFIG_ORVERRIDES_PATH)) {
            this.properties = new Properties();
            properties.load(is);
        } catch (FileNotFoundException ex) {
            String msg = String.format("The configuration override "
            		+ "file '%s' not found!!", CONFIG_ORVERRIDES_PATH);
            LOGGER.error(msg);
            throw new AppException(msg, ex);
        } catch (IOException ex) {
        	
            String msg = String.format("Unable to read from the "
            		+ "configuration override properties file '%s'!!", 
            			CONFIG_ORVERRIDES_PATH);
            LOGGER.error(msg);
            throw new AppException(msg, ex);
        }
	}
	
	@Override
	public Config getConfig(String category, String key) {
		return getConfig(category, key, ConfigConstants.DEFAULT_GROUP);
	}

	@Override
	public Config getConfig(String category, String key, String groupCode) {
		// Frame the key to lookup in the override properties.
		String lookupKey = String.format("%s.%s.%s", groupCode, category, key);
		String val = properties.getProperty(lookupKey);
		
		// If a value is present in the properties file, create a new Config
		// object with details available and return. The ID of the config 
		// loaded from override properties will always be 0.
		return (val != null) ? new Config(
				0L, ConfigType.Regular, groupCode, category, key, val) : null;
	}

	@Override
	public Map<String, Config> getConfigs(String category,
			String keyStartsWith) {
		return getConfigs(category, 
				keyStartsWith, ConfigConstants.DEFAULT_GROUP);
	}

	@Override
	public Map<String, Config> getConfigs(String category, String keyStartsWith,
			String groupCode) {
		// Frame the keyStart pattern to lookup in the override properties.
		String lookupKeyStartsWith = String.format("%s.%s.%s", groupCode, 
					category, keyStartsWith);
		
		// Filter out the properties that match the startsWith pattern, create
		// a config map from the keys/values obtained and 
		return properties.entrySet().stream()
			.filter(entry -> ((String)entry.getKey())
					.startsWith(lookupKeyStartsWith))
			.collect(Collectors.toMap(e -> extractKey((String) e.getKey()), 
					e -> createConfig(groupCode, category, 
							(String) e.getKey(), (String) e.getValue())));

	}
	
	private String extractKey(String key) {
		int firstDotPos = key.indexOf('.');
		int secondDotPos = key.indexOf('.', firstDotPos + 1);
		return key.substring(secondDotPos + 1);		
	}

	private Config createConfig(String groupCode, String category, 
				String key, String val) {
		
		// First get the key substring.
		int firstDotPos = key.indexOf('.');
		int secondDotPos = key.indexOf('.', firstDotPos + 1);
		String actualKey = key.substring(secondDotPos + 1);
		
		return new Config(0L, ConfigType.Regular, 
				groupCode, category, actualKey, val);
	}
	
}

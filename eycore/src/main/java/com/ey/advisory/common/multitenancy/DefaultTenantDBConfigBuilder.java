package com.ey.advisory.common.multitenancy;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.CommonCryptoUtils;
import com.ey.advisory.core.async.domain.master.GroupConfig;

@Component("DefaultTenantDBConfigBuilder")
public class DefaultTenantDBConfigBuilder implements TenantDBConfigBuilder {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(DefaultTenantDBConfigBuilder.class);
	
	@Autowired
	private Environment env;
	
	public TenantDBConfig buildDBConfig(
			String groupCode, List<GroupConfig> configs) {
		
		// Iterate over the properties obtained for the group, and build the 
		// DBConfig object with relevant DB property names and values in the
		// list of properties.
		TenantDBConfig dbConfig = new TenantDBConfig();
		configs.forEach(cfg -> 
				populateDBConfigParamValue(cfg.getConfigCode(), 
						cfg.getConfigValue(), dbConfig));
		
		// Check if the params are null. If so return null.
		if(!validatePoolConfig(groupCode, dbConfig)) {
			String msg = String.format("Invalid DB Config parameters "
					+ "detected for the Group: '%s'", groupCode);
			LOGGER.error(msg);
			return null;
		}
		
		// Return the DBConfig created based on the DB configuration 
		// parameters.
		return dbConfig;		
	
	}

	private TenantDBConfig populateDBConfigParamValue(
			String propName, String propValue, 
			TenantDBConfig config) {
		if (propName == null) {
			return config;
		}
		String encAESKey = env.getProperty("aes.internal.security.key");
		String pName = propName.trim();
		String pVal = (propValue != null) ? propValue.trim() : null;

		if (TenantConstant.DB_USER_NAME.equalsIgnoreCase(pName)) {
			config.setUsername(pVal);
		} else if (TenantConstant.DB_PASSWORD.equalsIgnoreCase(pName)) {
//			String decVal = CommonCryptoUtils.decrypt(pVal,
//					Base64.decodeBase64(encAESKey));
			config.setPassword(pVal);
		} else if (TenantConstant.DB_URL.equalsIgnoreCase(pName)) {
			config.setUrl(pVal);
		}

		return config;
	}	
	
	private boolean validatePoolConfig(
			String groupCode, TenantDBConfig config) {
		
		String dbUrl = config.getUrl();
		String dbUserName = config.getUsername();

		String templateStr = "The property '%s' is not "
				+ "configured for the Group: '%s'";
		
		boolean isConfigValid = true;
		
		if(dbUrl == null || dbUrl.isEmpty()) {
			String msg = String.format(templateStr, 
						TenantConstant.DB_URL, groupCode);
			LOGGER.error(msg);
			isConfigValid = false;
		}
				
		if(dbUserName == null || dbUserName.isEmpty()) {
			// Not marking the config as invalid as username can be actually
			// empty.
			String msg = String.format("The property '%s' is not "
					+ "configured for the Group: '%s'", 
					TenantConstant.DB_USER_NAME, groupCode);			
			LOGGER.warn(msg);
		}		
		
		return isConfigValid;
	}
	
}

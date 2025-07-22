package com.ey.advisory.core.async;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

/**
 * This is the default implementation of the ProfilePropsLoader interface
 * and is responsible for loading the profile based on the value of the
 * system parameter named 'ewbmsAsyncProfile'. If there is no system  parameter
 * by this name, then 'default' is used as the profile name. This class then
 * loads the core pool size and max pool size for the specified profile.
 * Additional properties can be added in future.
 * 
 * @author Sai.Pakanati
 *
 */

@Component("PropertyLoader")
@Profile("propsFromDB")
public class DBPropertyLoader implements ProfilePropsLoader {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(DBPropertyLoader.class);
	
	
	private Map<String, Object> propMap;
	
	@Override
	public Map<String, Object> loadProps() {
		try {/*
			
			// Get all properties from the DB that starts with the name
			// 'asyncexec'.
			Map<String, String> configMap =
					configManager.getConfigs("ASYNCEXEC", "asyncexec");
			String profileName = System.getProperty(Constants.EWBMS_PROFILE);

			String corePoolKey = null;
			String maxPoolKey = null;

			// If a profile name is not passed as a system parameter, then
			// use 'default' as the profile name. Create the keys for the
			// profile that need to be loaded from the DB.
			if(profileName != null) {
				corePoolKey = "asyncexec." + profileName + ".corepool";
				maxPoolKey = "asyncexec." + profileName + ".maxpool";
			} else {
				corePoolKey = "asyncexec.default.corepool";
				maxPoolKey = "asyncexec.default.maxpool";
			}

			// Get the core pool size and the max pool size. If no values
			// are configured in the DB
			String corePoolSize = configMap.containsKey(corePoolKey) ? 
					configMap.get(corePoolKey) : DEFAULT_CORE_POOL_SIZE;
			String maxPoolSize = configMap.containsKey(maxPoolKey) ? 
					configMap.get(maxPoolKey) : DEFAULT_MAX_POOL_SIZE;
					
			propMap = new HashMap<>();
			propMap.put(
					Constants.EWBMS_CORE_POOL_SIZE, 
					Integer.valueOf(corePoolSize));
			propMap.put(
					Constants.EWBMS_MAX_POOL_SIZE, 
					Integer.valueOf(maxPoolSize));
		*/} catch(Exception ex) {
			// If an error occurs, throw an EWBException so that the loading of the
			// SpringBoot app itself will fail. This ensures that the app is
			// loaded with a valid thread pool configuration.
			String msg = "Unexpected error while loading "
					+ "the Executor Thread Pool Configuration";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		
		return propMap;
	}

}

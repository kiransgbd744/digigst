package com.ey.advisory.core.async;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppStartUpConstants;

@Component("PropertyLoader")
@Profile("!propsFromDB")
@PropertySource("classpath:application.properties")
public class AppPropertiesFilePropertyLoader implements ProfilePropsLoader {

	@Value("${async.pool.coresize}")
	private int corePoolSize;

	@Value("${async.pool.maxsize}")
	private int maxPoolSize;
	
	@Value("${async.IO.pool.maxsize}")
	private int maxIOPoolSize;
	
	@Value("${async.URL.pool.maxsize}")
	private int maxURLPoolSize;
	

	private Map<String, Object> propMap = null;

	/**
	 * Making the method synchronized as we're not sure if Spring loads its
	 * beans sequentially in a single thread.
	 */
	@Override
	public synchronized Map<String, Object> loadProps() {
		// if the properties map is already loaded, return it.
		if (propMap != null) {
			return propMap;
		}

		// If not, create a new map and load the required properties.
		propMap = new HashMap<>();

		propMap.put(AppStartUpConstants.ASYNC_CORE_POOL_SIZE,
				Integer.valueOf(corePoolSize));
		propMap.put(AppStartUpConstants.ASYNC_MAX_POOL_SIZE,
				Integer.valueOf(maxPoolSize));
		propMap.put(AppStartUpConstants.ASYNC_IO_POOL_SIZE,
				Integer.valueOf(maxIOPoolSize));
		propMap.put(AppStartUpConstants.ASYNC_URL_POOL_SIZE,
				Integer.valueOf(maxURLPoolSize));

		return propMap;
	}

}

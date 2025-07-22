package com.ey.advisory.app.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ey.advisory.common.AppStartUpConstants;
import com.ey.advisory.core.async.ProfilePropsLoader;

/**
 * Spring Configuration class responsible for loading the Executor Thread Pool.
 * It also initializes the Spring Data Beans and Connection Pool.
 * 
 * @author Sai.Pakanati
 *
 */

@Configuration
@EnableAsync
public class AsyncTasksExecutorSpringConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			AsyncTasksExecutorSpringConfig.class);
	
	@Autowired
	@Qualifier("PropertyLoader")
	private ProfilePropsLoader profilePropsLoader;


	/**
	 * Factory method to initialize the Executor Thread Pool. Currently, the
	 * core pool size and max pool size are loaded using the default
	 * ProfilePropsLoader.
	 * 
	 * @return
	 */
	@Bean("EyAsyncMiscIOPool")
	public ThreadPoolTaskExecutor getIOPoolInstance() {
		Map<String, Object> propMap = profilePropsLoader.loadProps();
		
		int corePoolSize = AppStartUpConstants.DEFAULT_IO_POOL_SIZE;
		int maxPoolSize = AppStartUpConstants.DEFAULT_IO_POOL_SIZE;
		
		if (propMap.containsKey(AppStartUpConstants.ASYNC_IO_POOL_SIZE)) {
			corePoolSize = (int) propMap
					.get(AppStartUpConstants.ASYNC_IO_POOL_SIZE);
			maxPoolSize = corePoolSize;
		} else {
			String msg = String.format(
					"The property '%s' is not configured for the application. "
					+ "Setting IO Pool size = %d. It's better to configure "
					+ "this value explicitly based on the server "
					+ "hardware configuration.", 
					AppStartUpConstants.ASYNC_IO_POOL_SIZE, corePoolSize);
			LOGGER.warn(msg);
		}
		
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setThreadNamePrefix("AsyncIOPool-");
		executor.initialize();
		return executor;				
	}	
	
	
}

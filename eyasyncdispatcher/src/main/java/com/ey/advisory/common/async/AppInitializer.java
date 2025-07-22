package com.ey.advisory.common.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;

@Component
public class AppInitializer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppInitializer.class);

	@EventListener
	//@Async
	public void onApplicationEventRefresh(ContextRefreshedEvent event) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ApplicationContext is initialized");
		}
		TaskFetcherAndDistributorFactory factory = StaticContextHolder.getBean(
				"DefaultTaskFetcherAndDistributorFactory",
				TaskFetcherAndDistributorFactory.class);

		// Get the TaskFetcherAndDistributor instance from the factory.
		TaskFetcherAndDistributor fad = factory.getTaskFetcherAndDistributor();
		// Fetch and allocate jobs. The TaskFetcherAndDistributor
		// implementation should use a background thread to do this
		// activity, as this main method has to return immediately, as part
		// of APP startup.
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to Invoke the FetchAndAllocateTasks"
					+ " method with Async thread");
		}
		fad.fetchAndAllocateTasks();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Invoked the FetchAndAllocateTasks"
					+ " method with Async thread");
		}

	}
}

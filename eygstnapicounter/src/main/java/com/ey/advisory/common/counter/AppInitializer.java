package com.ey.advisory.common.counter;

import java.time.LocalDateTime;

import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.counter.services.GSTNPublicApiBackUpService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppInitializer {

	@Autowired
	@Qualifier("GSTNPublicApiBackUpServiceImpl")
	private GSTNPublicApiBackUpService bckUpservice;

	@EventListener
	public void onApplicationEventRefresh(ContextRefreshedEvent event) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loading usage and limits on App Start {}",
					LocalDateTime.now());
		}
		long stTime = System.currentTimeMillis();
		bckUpservice.loadBackUp();
		long endTime = System.currentTimeMillis();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loading usage and limits took {} ms",
					endTime - stTime);
			LOGGER.debug("Loaded usage and limits successfully {}",
					LocalDateTime.now());
		}
	}

	@PreDestroy
	public void backUpDataOnShutDown() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" About to start Graceful shutdown backUpLimitAndUsageMap at {}",
					LocalDateTime.now());
		}
		long stTime = System.currentTimeMillis();
		bckUpservice.createBackUp();
		long endTime = System.currentTimeMillis();
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Loading usage and limits took {} ms",
					endTime - stTime);
			LOGGER.debug(
					"Graceful shutdown backUpLimitAndUsageMap successful at {}",
					LocalDateTime.now());
		}
	}
}

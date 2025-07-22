package com.ey.advisory.common.counter;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.counter.services.GSTNPublicApiBackUpService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultPeriodicJobRunner implements PeriodicJobRunner {

	@Autowired
	private CounterExecControlParams controlParams;

	@Autowired
	@Qualifier("GSTNPublicApiBackUpServiceImpl")
	private GSTNPublicApiBackUpService bckUpservice;

	/* Runs at 1st of every month and resets the usage map */
	@Override
	@Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Kolkata")
	public void resetUsageMap() {
		LOGGER.error("UsageMap Reset Start at " + LocalDateTime.now());

		controlParams.resetUsageMap();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("UsageMap Reset successful at " + LocalDateTime.now());
		}
		LOGGER.error("UsageMap Reset successful at " + LocalDateTime.now());
	}

	/* Runs at 6 12 18 24 hrs */
	@Override
	@Scheduled(cron = "0 0 0/6 * * ?")
	public void backUpLimitAndUsageMapPeriodically() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" About to start Periodical backUpLimitAndUsageMap at {}",
					LocalDateTime.now());
		}
		bckUpservice.createBackUp();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Periodical backUpLimitAndUsageMap successful at {}",
					LocalDateTime.now());
		}
	}

}

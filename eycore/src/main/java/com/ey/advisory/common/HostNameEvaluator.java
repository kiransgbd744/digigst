package com.ey.advisory.common;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class HostNameEvaluator {

	@Autowired
	@Qualifier("FallBackHostNameResolver")
	private HostNameResolver hostNameResolver;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HostNameEvaluator.class);

	private String hostName;

	@PostConstruct
	public void fetchHostName() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Fetching the HostName.....");
		}
		hostName = hostNameResolver.getLocalHostName();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched the HostName....., HostName is '%s'", hostName);
			LOGGER.debug(msg);
		}
	}

	public String getHostName() {
		return hostName;
	}

}

package com.ey.advisory.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("DefaultContextBuilder")
public class DefaultContextBuilder implements ContextBuilder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultContextBuilder.class);
	
	@Override
	public AppExecContext createAppContext(String userName) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Building the AppExecContext...");
		}
		AppExecContext context = new AppExecContext();
		context.setUserName(userName);
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Built the AppExecContext!!");
		}		
		return context;
	}

	
}

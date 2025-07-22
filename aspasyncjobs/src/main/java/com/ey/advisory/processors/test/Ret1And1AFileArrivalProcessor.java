package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.Ret1And1AFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

@Component("Ret1And1AFileArrivalProcessor")
public class Ret1And1AFileArrivalProcessor implements TaskProcessor {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(Ret1And1AFileArrivalProcessor.class);
	
	@Autowired
	@Qualifier("Ret1And1AFileArrivalHandler")
	private Ret1And1AFileArrivalHandler ret1And1AFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		LOGGER.debug("Ret1And1A file Arrived");	
		ret1And1AFileArrivalHandler.processRet1And1AFile(message, context);
		LOGGER.debug("Ret1And1A file Arrival processed");
		
	}

}

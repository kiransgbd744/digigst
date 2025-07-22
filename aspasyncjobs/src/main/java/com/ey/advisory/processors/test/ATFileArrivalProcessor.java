package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.ATFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

@Component("ATFileArrivalProcessor")
public class ATFileArrivalProcessor implements TaskProcessor {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(ATFileArrivalProcessor.class);
	@Autowired
	@Qualifier("ATFileArrivalHandler")
	private ATFileArrivalHandler aTFileArrivalHandler;
	
	
	@Override
	public void execute(Message message, AppExecContext context) {
		LOGGER.debug("Advance Received file Arrived");	
		aTFileArrivalHandler.processATsFile(message, context);
		LOGGER.debug("Advance Received file Arrival processed");			
	}

}

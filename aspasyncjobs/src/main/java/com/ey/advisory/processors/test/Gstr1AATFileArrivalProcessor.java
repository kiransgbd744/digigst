package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.doc.gstr1a.Gstr1AATFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

@Component("Gstr1AATFileArrivalProcessor")
public class Gstr1AATFileArrivalProcessor implements TaskProcessor {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AATFileArrivalProcessor.class);
	@Autowired
	@Qualifier("Gstr1AATFileArrivalHandler")
	private Gstr1AATFileArrivalHandler aTFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		LOGGER.debug("Gstr1A Advance Received file Arrived");
		aTFileArrivalHandler.processATsFile(message, context);
		LOGGER.debug("Gstr1A Advance Received file Arrival processed");
	}

}

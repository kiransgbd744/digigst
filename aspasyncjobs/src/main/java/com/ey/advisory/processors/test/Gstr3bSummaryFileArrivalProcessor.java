package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.Gstr3bSummaryFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("Gstr3bSummaryFileArrivalProcessor")
public class Gstr3bSummaryFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3bSummaryFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr3bSummaryFileArrivalHandler")
	private Gstr3bSummaryFileArrivalHandler gstr3bSummaryFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		// TODO Auto-generated method stub

		LOGGER.debug("Gstr3b Summary upload file Arrived");
		gstr3bSummaryFileArrivalHandler.processProductFile(message, context);
		LOGGER.debug("Gstr3b Summary upload file Arrival processed");

	}

}

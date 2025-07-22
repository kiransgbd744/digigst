package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.gstr2b.Gstr2bB2bFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Ravindra V S
 *
 */

@Component("Gstr2bB2bFileArrivalProcessor")
public class Gstr2bB2bFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2bB2bFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr2bB2bFileArrivalHandler")
	private Gstr2bB2bFileArrivalHandler gstr2bB2bFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Gstr2b file arrived");

			gstr2bB2bFileArrivalHandler.processGstr2bB2bFile(message, context);
			LOGGER.debug("Gstr2b file arrival processed");

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr2b file ",
					e.getMessage());
			throw e;
		}
	}
}

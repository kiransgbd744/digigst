package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.gstr2a.Gstr1FileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr1FileArrivalProcessor")
public class Gstr1FileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1FileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr1FileArrivalHandler")
	private Gstr1FileArrivalHandler gstr1FileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Gstr1 file arrived");

			gstr1FileArrivalHandler.processGstr1File(message, context);
			LOGGER.debug("Gstr1 file arrival processed");

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr1 file ",
					e.getMessage());
			throw e;
		}
	}
}

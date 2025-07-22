package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.gstr8.Gstr8aFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Ravindra V S
 *
 */

@Component("Gstr8aFileArrivalProcessor")
public class Gstr8aFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr8aFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr8aFileArrivalHandler")
	private Gstr8aFileArrivalHandler gstr8aFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr8a file arrived");
			}

			gstr8aFileArrivalHandler.processGstr8aFile(message, context);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr8a file arrival processed");
			}

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr8a file ",
					e.getMessage());
			throw e;
		}
	}
}

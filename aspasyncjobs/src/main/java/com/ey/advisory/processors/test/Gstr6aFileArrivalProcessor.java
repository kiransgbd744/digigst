package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.jobs.gstr6.Gstr6aFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Ravindra V S
 *
 */

@Component("Gstr6aFileArrivalProcessor")
public class Gstr6aFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6aFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr6aFileArrivalHandler")
	private Gstr6aFileArrivalHandler gstr6aFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a file arrived");
			}

			gstr6aFileArrivalHandler.processGstr6aFile(message, context);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a file arrival processed");
			}

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr6a file ",
					e.getMessage());
			throw e;
		}
	}
}

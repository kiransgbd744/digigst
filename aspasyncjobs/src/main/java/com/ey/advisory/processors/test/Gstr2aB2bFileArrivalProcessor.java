package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.gstr2a.Gstr2aB2bFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Anand3.M
 *
 */

@Component("Gstr2aB2bFileArrivalProcessor")
public class Gstr2aB2bFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2aB2bFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr2aB2bFileArrivalHandler")
	private Gstr2aB2bFileArrivalHandler gstr2aB2bFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr2a file arrived");
			}

			gstr2aB2bFileArrivalHandler.processGstr2aB2bFile(message, context);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr2a file arrival processed");
			}

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr2a file ",
					e.getMessage());
			throw e;
		}
	}
}

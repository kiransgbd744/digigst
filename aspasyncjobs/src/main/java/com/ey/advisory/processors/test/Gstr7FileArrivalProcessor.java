package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.gstr7fileupload.Gstr7FileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr7FileArrivalProcessor")
public class Gstr7FileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr7FileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr7FileArrivalHandler")
	private Gstr7FileArrivalHandler gstr7FileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Gstr7 file arrived");

			gstr7FileArrivalHandler.processGstr7File(message, context);
			LOGGER.debug("Gstr7 file arrival processed");

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr7 file ", e.getMessage());
			throw e;
		}
	}
}

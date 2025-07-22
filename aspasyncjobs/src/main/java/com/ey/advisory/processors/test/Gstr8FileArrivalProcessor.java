package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.Gstr8FileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("Gstr8FileArrivalProcessor")
public class Gstr8FileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr8FileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr8FileArrivalHandler")
	private Gstr8FileArrivalHandler gstr8FileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		// TODO Auto-generated method stub

		try {
			LOGGER.debug("Gstr8 upload file Arrived");
			gstr8FileArrivalHandler.processProductFile(message, context);
			LOGGER.debug("Gstr8 upload file Arrival processed");
		} catch (Exception e) {
			throw new AppException(
					"Error occurred during Gstr8 File Processing ", e);
		}

	}

}

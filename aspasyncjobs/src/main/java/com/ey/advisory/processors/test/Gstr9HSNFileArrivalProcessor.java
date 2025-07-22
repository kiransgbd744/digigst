package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.gstr9.Gstr9HsnFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9HSNFileArrivalProcessor")
public class Gstr9HSNFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr9HSNFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr9HsnFileArrivalHandler")
	private Gstr9HsnFileArrivalHandler gstr9FileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Gstr9 Hsn file arrived");

			gstr9FileArrivalHandler.processGstr9HsnFile(message, context);
			LOGGER.debug("Gstr9 Hsn file arrival processed");

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr9 Hsn file ",
					e.getMessage());
			throw e;
		}
	}
}

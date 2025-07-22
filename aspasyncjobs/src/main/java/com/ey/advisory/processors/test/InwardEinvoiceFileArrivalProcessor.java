package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.einvoice.InwardEinvoiceFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Ravindra V S
 *
 */

@Component("InwardEinvoiceFileArrivalProcessor")
public class InwardEinvoiceFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardEinvoiceFileArrivalProcessor.class);

	@Autowired
	@Qualifier("InwardEinvoiceFileArrivalHandler")
	private InwardEinvoiceFileArrivalHandler inwardEinvoiceFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inward Einvoice file arrived");
			}

			inwardEinvoiceFileArrivalHandler.processInwardEinvoiceFile(message, context);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inward Einvoice file arrival processed");
			}

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Inward Einvoice file ",
					e.getMessage());
			throw e;
		}
	}
}

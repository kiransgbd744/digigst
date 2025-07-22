package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.gstr1.sales.register.Gstr1SalesRegisterFileArrivalHandler;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1SalesRegisterFileArrivalProcessor")
public class Gstr1SalesRegisterFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SalesRegisterFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr1SalesRegisterFileArrivalHandler")
	private Gstr1SalesRegisterFileArrivalHandler gstr1FileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Gstr1 file arrived");

			gstr1FileArrivalHandler.processProductFile(message, context);
			LOGGER.debug("Gstr1 file arrival processed");

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr1 file ",
					e.getMessage());
			throw e;
		}
	}
}

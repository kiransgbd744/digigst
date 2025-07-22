package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.OutwardB2cFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("OutwardShippingBillFileArrivalProcessor")
public class OutwardShippingBillFileArrivalProcessor implements TaskProcessor {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardShippingBillFileArrivalProcessor.class);

	@Autowired
	@Qualifier("OutwardB2cFileArrivalHandler")
	private OutwardB2cFileArrivalHandler inwardB2cFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Shipping bill  file Arrived");
		inwardB2cFileArrivalHandler.processB2csFile(message, context);
		LOGGER.debug("Shipping bill file Arrival processed");

	}

}

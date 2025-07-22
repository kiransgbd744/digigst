package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.InterestAndLateFeeFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
/**
 * Mahesh Golla
 */
@Component("InterestAndLateFeeFileArrivalProcessor")
public class InterestAndLateFeeFileArrivalProcessor implements TaskProcessor {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InterestAndLateFeeFileArrivalProcessor.class);

	@Autowired
	@Qualifier("InterestAndLateFeeFileArrivalHandler")
	private InterestAndLateFeeFileArrivalHandler interestAndLateFeeFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("InterestAndLateFee file Arrived");
		interestAndLateFeeFileArrivalHandler
				.processInterestAndLateFeeFile(message, context);
		LOGGER.debug("InterestAndLateFee file Arrival processed");

	}

}

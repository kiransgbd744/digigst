package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.RefundsFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("RefundsFileArrivalProcessor")
public class RefundsFileArrivalProcessor implements TaskProcessor {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(RefundsFileArrivalProcessor.class);
	
	@Autowired
	@Qualifier("RefundsFileArrivalHandler")
	private RefundsFileArrivalHandler refundsFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		LOGGER.debug("Refunds file Arrived");	
		refundsFileArrivalHandler.processRefundsFile(message, context);
		LOGGER.debug("Refunds file Arrival processed");
		
	}

}

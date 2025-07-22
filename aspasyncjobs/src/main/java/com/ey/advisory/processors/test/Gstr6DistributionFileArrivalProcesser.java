package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.Gstr6DistrbtnFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr6DistributionFileArrivalProcesser")
public class Gstr6DistributionFileArrivalProcesser implements TaskProcessor {

	private static final Logger LOGGER = 
		    LoggerFactory.getLogger(Gstr6DistributionFileArrivalProcesser.class);

	
	@Autowired
	@Qualifier("Gstr6DistrbtnFileArrivalHandler")
	private Gstr6DistrbtnFileArrivalHandler gstr6FileArrivalHandler;


	@Override
	public void execute(Message message, AppExecContext context) {
		// TODO Auto-generated method stub
	
		LOGGER.debug("Inward GSTR6 Distribution file Arrived");
		gstr6FileArrivalHandler.processProductFile(message, context);
		LOGGER.debug("Gstr6 Distribution file Arrival processed");
		
	}
	
	
	
}

package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.OutwardB2cFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("OutwardB2cFileArrivalProcesser")
@Slf4j
public class OutwardB2cFileArrivalProcesser implements TaskProcessor {
	@Autowired
	@Qualifier("OutwardB2cFileArrivalHandler")
	private OutwardB2cFileArrivalHandler inwardB2cFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try{
			LOGGER.debug("Inward B2c file Arrived");	
			inwardB2cFileArrivalHandler.processB2csFile(message, context);
			LOGGER.debug("Inward B2c file Arrival processed");
		}
		catch(AppException e){
			LOGGER.debug("Exception occured while Outward B2c Processor",e);
			throw new AppException();
		}
	}

}

package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.B2csFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("B2csFileArrivalProcessor")
@Slf4j
public class B2csFileArrivalProcessor implements TaskProcessor {
	
	@Autowired
	@Qualifier("B2csFileArrivalHandler")
	private B2csFileArrivalHandler b2csFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try{
			LOGGER.debug("B2cs file Arrived");	
			b2csFileArrivalHandler.processB2csFile(message, context);
			LOGGER.debug("B2cs file Arrival processed");
		}
		catch(AppException e){
			LOGGER.debug("Exception occured while B2cs Processor",e);
			throw new AppException();
		}
	}

}

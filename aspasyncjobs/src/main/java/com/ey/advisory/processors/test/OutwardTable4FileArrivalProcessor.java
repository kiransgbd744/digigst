package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.OutwardTable4FileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("OutwardTable4FileArrivalProcessor")
@Slf4j
public class OutwardTable4FileArrivalProcessor implements TaskProcessor {
	@Autowired
	@Qualifier("OutwardTable4FileArrivalHandler")
	private OutwardTable4FileArrivalHandler outwardTable4FileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try{
			LOGGER.debug("Table4 file Arrived");	
			outwardTable4FileArrivalHandler.processTable4File(message, context);
			LOGGER.debug("Table4 file Arrival processed");	
		}
		catch(AppException e){
			LOGGER.debug("Exception Occured while Table 4 processor",e);
			throw new AppException();
			
		}
		
	}

}

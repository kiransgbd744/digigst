package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.TxpdFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("ATAFileArrivalProcessor")
@Slf4j
public class ATAFileArrivalProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("TxpdFileArrivalHandler")
	private TxpdFileArrivalHandler txpdFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try{
		LOGGER.debug("B2cs file Arrived");	
		txpdFileArrivalHandler.processAtaFile(message, context);
		LOGGER.debug("B2cs file Arrival processed");
		}
		catch(AppException e){
			LOGGER.debug("Exception occured while Advance Received Processor",e);
			throw new AppException();
		}
		
	}

}

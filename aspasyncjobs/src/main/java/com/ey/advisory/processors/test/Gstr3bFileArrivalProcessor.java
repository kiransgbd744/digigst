package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.Gstr3bFileArrivalHandler;
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
@Component("Gstr3bFileArrivalProcessor")
@Slf4j
public class Gstr3bFileArrivalProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr3bFileArrivalHandler")
	private Gstr3bFileArrivalHandler gstr3bFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		try{
			LOGGER.debug("Gstr3bFile file Arrived");
			gstr3bFileArrivalHandler
					.processGstr3BFile(message, context);
			LOGGER.debug("Gstr3bFile file Arrival processed");	
		}
		catch(AppException e){
			LOGGER.debug("Exception occured during file processor",e);
			throw new AppException();
		}

	}

}

package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.InwardTable3H3IFileArrivalHandler;
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

@Component("InwardTable3H3IFileArrivalProcessor")
@Slf4j
public class InwardTable3H3IFileArrivalProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("InwardTable3H3IFileArrivalHandler")
	private InwardTable3H3IFileArrivalHandler table3H3IFileArrivalHandler;

	public void execute(Message message, AppExecContext context) {
		try{
			LOGGER.debug("Table3H3I File arrived");
			table3H3IFileArrivalHandler.processTable3H3IFile(message, context);
			LOGGER.debug("Table3H3I File Arrival Processed");
		}
		catch(AppException e){
			LOGGER.debug("Exception occured while Inward 3H and 3I Processors",e);
			throw new AppException();
		}


	}
}

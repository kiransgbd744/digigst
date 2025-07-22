package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.CrossItcFileArrivalHandler;
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
@Component("CrossItcFileArrivalProcessor")
@Slf4j
public class CrossItcFileArrivalProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("CrossItcFileArrivalHandler")
	private CrossItcFileArrivalHandler crossItcFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Cross ITC file Arrived");
			}
			crossItcFileArrivalHandler.processB2csFile(message, context);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Cross ITC file Arrival processed");
			}
		} catch (AppException e) {
			LOGGER.debug("Exception occured while Cross ITC Processor", e);
			throw new AppException();
		}
	}

}

package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.Itc04FileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Itc04FileArrivalProcessor")
@Slf4j
public class Itc04FileArrivalProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Itc04FileArrivalHandler")
	private Itc04FileArrivalHandler itc04FileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			LOGGER.debug("Itc04 file Arrived");
			
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"ITC04_SR_FILE_ARRIVAL_PROCESSOR_START",
					"Itc04FileArrivalHandler", "execute", null);
			
			itc04FileArrivalHandler.processItc04File(message, context);
			
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"ITC04_SR_FILE_ARRIVAL_PROCESSOR_END",
					"Itc04FileArrivalHandler", "execute", null);
			
			LOGGER.debug("Itc04 file Arrival processed");
		} catch (AppException e) {
			LOGGER.debug("Exception occured during file processor", e);
			throw new AppException();
		}

	}

}

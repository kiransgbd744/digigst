package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.CewbFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Mahesh Golla
 */
@Component("CewbFileUploadProcessor")
@Slf4j
public class CewbFileUploadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("CewbFileArrivalHandler")
	private CewbFileArrivalHandler cewbFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("cewbFile file Arrived");
		}
		cewbFileArrivalHandler.processCewbFileFile(message, context);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("cewbFile file Arrival processed");
		}

	}

}

package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.gstr6.Gstr6DigiGstProcessedComputeServiceImpl;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant Shukla
 *
 */
@Slf4j
@Component("Gstr6DigiGstProcessedComputeProcessor")
public class Gstr6DigiGstProcessedComputeProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr6DigiGstProcessedComputeServiceImpl")
	Gstr6DigiGstProcessedComputeServiceImpl gstr6DigiGstProcessedComputeServiceImpl;

	@Override
	public void execute(Message message, AppExecContext context) {
		LOGGER.debug("Gstr6DigiGst Processed Computation Started");
		gstr6DigiGstProcessedComputeServiceImpl.execute(message, context);
		LOGGER.debug("Gstr6DigiGst Processed Computation End");
	}

}

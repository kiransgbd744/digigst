package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.gstr9.Gstr9DigiGstProcessedComputeServiceImpl;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant Shukla
 *
 */
@Slf4j
@Component("Gstr9DigiGstProcessedComputeProcessor")
public class Gstr9DigiGstProcessedComputeProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr9DigiGstProcessedComputeServiceImpl")
	Gstr9DigiGstProcessedComputeServiceImpl gstr9DigiGstProcessedComputeServiceImpl;

	@Override
	public void execute(Message message, AppExecContext context) {
		LOGGER.debug("Gstr9DigiGst Processed Computation Started");
		gstr9DigiGstProcessedComputeServiceImpl.execute(message, context);
		LOGGER.debug("Gstr9DigiGst Processed Computation End");
	}

}

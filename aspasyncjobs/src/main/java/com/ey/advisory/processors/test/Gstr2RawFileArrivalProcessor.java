package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.gstr2.Gstr2RawFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr2RawFileArrivalProcessor")
public class Gstr2RawFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2RawFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr2RawFileArrivalHandler")
	private Gstr2RawFileArrivalHandler gstr2RawFileArrivalHandler;

	public void execute(Message message, AppExecContext context) {
		LOGGER.debug("GSTR2 RAW File arrived");
		gstr2RawFileArrivalHandler.processGstr2RawFile(message, context);
		LOGGER.debug("GSTR2 RAW File Arrival Processed");
	}
}

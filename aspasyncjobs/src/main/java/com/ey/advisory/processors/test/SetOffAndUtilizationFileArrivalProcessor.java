package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.SetOffAndUtilFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("SetOffAndUtilizationFileArrivalProcessor")
public class SetOffAndUtilizationFileArrivalProcessor implements TaskProcessor {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SetOffAndUtilizationFileArrivalProcessor.class);

	@Autowired
	@Qualifier("SetOffAndUtilFileArrivalHandler")
	private SetOffAndUtilFileArrivalHandler setOffAndUtilFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("SetOffAndUtil file Arrived");
		setOffAndUtilFileArrivalHandler.processTableSetOffAndUtilFile(message,
				context);
		LOGGER.debug("SetOffAndUtil file Arrival processed");

	}

}

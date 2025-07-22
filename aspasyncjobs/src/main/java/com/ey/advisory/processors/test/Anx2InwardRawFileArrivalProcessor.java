package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.gstr2.Anx2InwardRawFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

@Component("Anx2InwardRawFileArrivalProcessor")
public class Anx2InwardRawFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2InwardRawFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Anx2InwardRawFileArrivalHandler")
	private Anx2InwardRawFileArrivalHandler anx2InwardRawFileArrivalHandler;

	public void execute(Message message, AppExecContext context) {
		LOGGER.debug("Anx2 inward raw file arrived");
		anx2InwardRawFileArrivalHandler.processAnx2InwardRawFile(message,
				context);
		LOGGER.debug("Anx2 inward raw file arrival processed");
	}
}

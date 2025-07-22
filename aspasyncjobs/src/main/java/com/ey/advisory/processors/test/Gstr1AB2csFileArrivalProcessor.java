package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.doc.gstr1a.Gstr1AB2csFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr1AB2csFileArrivalProcessor")
@Slf4j
public class Gstr1AB2csFileArrivalProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1AB2csFileArrivalHandler")
	private Gstr1AB2csFileArrivalHandler b2csFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			LOGGER.debug("Gstr1a B2cs file Arrived");
			b2csFileArrivalHandler.processB2csFile(message, context);
			LOGGER.debug(" Gstr1a B2cs file Arrival processed");
		} catch (AppException e) {
			LOGGER.debug("Exception occured while Gstr1a B2cs Processor", e);
			throw new AppException();
		}
	}

}

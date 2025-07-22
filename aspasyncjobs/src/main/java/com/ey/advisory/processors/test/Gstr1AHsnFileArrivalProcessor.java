package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.doc.gstr1a.Gstr1AHsnFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr1AHsnFileArrivalProcessor")
@Slf4j
public class Gstr1AHsnFileArrivalProcessor implements TaskProcessor {
	@Autowired
	@Qualifier("Gstr1AHsnFileArrivalHandler")
	private Gstr1AHsnFileArrivalHandler hsnFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			LOGGER.debug("Hsn file arrived");
			hsnFileArrivalHandler.processHsnFile(message, context);
			LOGGER.debug("Hsn file arrival processed");
		} catch (AppException e) {
			LOGGER.debug("Exception occured while HSN file processor", e);
			throw new AppException();
		}

	}

}

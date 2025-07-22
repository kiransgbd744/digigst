package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.doc.gstr1a.Gstr1ANilFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr1ANilFileArrivalProcessor")
@Slf4j
public class Gstr1ANilFileArrivalProcessor implements TaskProcessor {
	@Autowired
	@Qualifier("Gstr1ANilFileArrivalHandler")
	private Gstr1ANilFileArrivalHandler nilFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			LOGGER.debug("Nil/exempted/non file has Arrived");
			nilFileArrivalHandler.processNilFile(message, context);
			LOGGER.debug("Nil/exempted/non file has arrival process");
		} catch (AppException e) {
			LOGGER.error("Exception while in Processor Nil Non Ext", e);
			throw new AppException();

		}
	}

}

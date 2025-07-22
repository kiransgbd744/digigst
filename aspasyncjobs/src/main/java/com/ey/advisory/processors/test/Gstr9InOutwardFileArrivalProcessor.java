package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.Gstr9InOutwardFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr9InOutwardFileArrivalProcessor")
@Slf4j
public class Gstr9InOutwardFileArrivalProcessor implements TaskProcessor {
	
	@Autowired
	@Qualifier("Gstr9InOutwardFileArrivalHandler") 
	private Gstr9InOutwardFileArrivalHandler gstr9InOutwardFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InOutward file has Arrived");
			}
			gstr9InOutwardFileArrivalHandler.processTdsTcsFile(message, context);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InOutward file has arrival process");
			}
		} catch (AppException e) {
			LOGGER.error("Exception while in Processor Gstr9InOutward", e);
			throw new AppException();

		}
	}

}

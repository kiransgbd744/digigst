package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.TcsAndTdsFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("TcsAndTdsFileArrivalProcessor")
@Slf4j
public class TcsAndTdsFileArrivalProcessor implements TaskProcessor {
	@Autowired
	@Qualifier("TcsAndTdsFileArrivalHandler")
	private TcsAndTdsFileArrivalHandler tcsAndTdsFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("TcsAndTds file has Arrived");
			}
			tcsAndTdsFileArrivalHandler.processTdsTcsFile(message, context);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("TcsAndTds file has arrival process");
			}
		} catch (AppException e) {
			LOGGER.error("Exception while in Processor TcsAndTds", e);
			throw new AppException();

		}
	}

}

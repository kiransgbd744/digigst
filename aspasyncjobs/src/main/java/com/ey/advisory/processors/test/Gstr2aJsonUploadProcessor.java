package com.ey.advisory.processors.test;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Component("Gstr2aJsonUploadProcessor")
@Slf4j
public class Gstr2aJsonUploadProcessor implements TaskProcessor{

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Gstr2a json file arrived");

			//gstr2aB2bFileArrivalHandler.processGstr2aB2bFile(message, context);
			LOGGER.debug("Gstr2a json file arrival processed");

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Gstr2a json file ", e.getMessage());
			throw e;
		}
	}
}
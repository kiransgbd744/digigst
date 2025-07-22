package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.gstr2.Anx2GetAnx2FileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
/**
 * 
 * @author Anand3.M
 *
 */
@Component("GetAnx2FileArrivalProcessor")
public class GetAnx2FileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetAnx2FileArrivalProcessor.class);

	@Autowired
	@Qualifier("Anx2GetAnx2FileArrivalHandler")
	private Anx2GetAnx2FileArrivalHandler anx2GetAnx2FileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Get anx2 file arrived");

			anx2GetAnx2FileArrivalHandler.processGetAnx2File(message,
					context);
			LOGGER.debug("Get anx2 file arrival processed");

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Get anx2 file ",
					e.getMessage());
			throw e;
		}
	}
}
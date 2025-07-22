package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr8GstnGetCallJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr8GstnGetProcessor")
@Slf4j
public class Gstr8GstnGetProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr8GstnGetCallJobHandlerImpl")
	private Gstr8GstnGetCallJobHandler gstr8JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1 Get Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			if (jsonString != null && groupCode != null) {
				gstr8JobHandler.gstr8GstnGetCall(jsonString, groupCode);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 Get Gstn Processed with args {} ",
							jsonString);
				}
			}
		} catch (Exception ex) {
			String msg = "Gstr1GstnGetProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);

		}

	}
}

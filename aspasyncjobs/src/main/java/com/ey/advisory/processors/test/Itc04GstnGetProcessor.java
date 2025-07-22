package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Itc04GstnGetJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Itc04GstnGetProcessor")
@Slf4j
public class Itc04GstnGetProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Itc04GstnGetJobHandlerImpl")
	private Itc04GstnGetJobHandler itc04JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Itc04 Get Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			if (jsonString != null && groupCode != null) {
				itc04JobHandler.itc04GstnGetCall(jsonString, groupCode);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Itc04 Get Gstn Processed with args {} ",
							jsonString);
				}
			}
		} catch (Exception ex) {
			String msg = "Itc04GstnGetProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}
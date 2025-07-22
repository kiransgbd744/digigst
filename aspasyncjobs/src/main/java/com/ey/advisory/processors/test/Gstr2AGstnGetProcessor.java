package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr2AGstnGetJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Gstr2AGstnGetProcessor")
@Slf4j
public class Gstr2AGstnGetProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2AGstnGetJobHandlerImpl")
	private Gstr2AGstnGetJobHandler gstr2AJobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2A Get Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			if (jsonString != null && groupCode != null) {
				gstr2AJobHandler.gstr2AGstnGetCall(jsonString, groupCode);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr2A Get Gstn Processed with args {} ",
							jsonString);
				}
			}
		} catch (Exception ex) {
			String msg = "Gstr2aGstnGetProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}

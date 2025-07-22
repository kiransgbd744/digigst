/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.processors.handler.Gstr6CalculateR6Handler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Component("Gstr6CalculateR6Processor")
@Slf4j
public class Gstr6CalculateR6Processor implements TaskProcessor {

	@Autowired
	private Gstr6CalculateR6Handler gstr6JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		String userName = message.getUserName();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6CalculateR6Processor Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}

		if (jsonString != null && groupCode != null) {
			try {
				gstr6JobHandler.calculateR6(jsonString, groupCode, userName);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info(
							"Gstr6CalculateR6Processor Processed with args {} ",
							jsonString);
				}

			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}

}

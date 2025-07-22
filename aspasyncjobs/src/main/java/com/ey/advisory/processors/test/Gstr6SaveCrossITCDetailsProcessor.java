/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.processors.handler.Gstr6SaveCrossITCDetailsHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Component("Gstr6SaveCrossITCDetailsProcessor")
@Slf4j
public class Gstr6SaveCrossITCDetailsProcessor implements TaskProcessor {

	@Autowired
	private Gstr6SaveCrossITCDetailsHandler gstr6JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		String userName = message.getUserName();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6SaveCrossITCDetailsProcessor Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}

		if (jsonString != null && groupCode != null) {

			gstr6JobHandler.saveCrossItc(jsonString, groupCode, userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr6SaveCrossITCDetailsProcessor Processed with args {} ",
						jsonString);
			}

		}

	}

}

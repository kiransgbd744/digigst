/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.RetGstnGetJobHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("RetGstnGetProcessor")
@Slf4j
public class RetGstnGetProcessor  implements TaskProcessor {

	@Autowired
	@Qualifier("RetGstnGetJobHandlerImpl")
	private RetGstnGetJobHandler retJobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		LOGGER.debug("Ret Get Gstn Data Execute method is ON with "
				+ "groupcode {} and params {}", groupCode, jsonString);
		if (jsonString != null && groupCode != null) {
			retJobHandler.retGstnGetCall(jsonString, groupCode);
			LOGGER.info("Ret Get Gstn Processed with args {} ", jsonString);
		}
	}
}

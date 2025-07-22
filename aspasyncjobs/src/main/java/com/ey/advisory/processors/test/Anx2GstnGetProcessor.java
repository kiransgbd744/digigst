/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Anx2GstnGetJobHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx2GstnGetProcessor")
@Slf4j
public class Anx2GstnGetProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Anx2GstnGetJobHandlerImpl")
	private Anx2GstnGetJobHandler anx2JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		LOGGER.debug("Anx2 Get Gstn Data Execute method is ON with "
				+ "groupcode {} and params {}", groupCode, jsonString);
		if (jsonString != null && groupCode != null) {
			anx2JobHandler.anx2GstnGetCall(jsonString, groupCode);
			LOGGER.info("Anx2 Get Gstn Processed with args {} ", jsonString);
		}
	}
}

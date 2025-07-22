package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr6SubmitToGstnJobHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */

@Slf4j
@Service("Gstr6SubmitToGstnProcessor")
public class Gstr6SubmitToGstnProcessor implements TaskProcessor {

	@Autowired
	private Gstr6SubmitToGstnJobHandler submitHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		String userName = message.getUserName();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6 SubmitToGstn Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}
		if (jsonString != null && groupCode != null) {			
				submitHandler.submitGstinAndPeriod(jsonString, groupCode, userName);
				LOGGER.info("SubmitToGstn Processed with args {} ", jsonString);			
		}
	}

}

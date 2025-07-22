package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.processors.handler.Itc04SaveToGstnJobHandler;
import com.ey.advisory.common.AppException;
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
@Component("Itc04SaveToGstnProcessor")
public class Itc04SaveToGstnProcessor implements TaskProcessor{
	@Autowired
	@Qualifier("Itc04SaveToGstnJobHandler")
	private Itc04SaveToGstnJobHandler itc04JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Itc04 SaveToGstn Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}
		if (jsonString != null && groupCode != null) {
			try {
				itc04JobHandler.saveCancelledInvoices(jsonString, groupCode);
				itc04JobHandler.saveActiveInvoices(jsonString, groupCode);
				LOGGER.info("Save to Gstn Processed with args {} ", jsonString);
			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}
	}
}

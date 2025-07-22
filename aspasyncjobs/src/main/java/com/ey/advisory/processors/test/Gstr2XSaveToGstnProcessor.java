package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.processors.handler.Gstr2XSaveToGstnJobHandler;
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
@Component("Gstr2XSaveToGstnProcessor")
public class Gstr2XSaveToGstnProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2XSaveToGstnJobHandler")
	private Gstr2XSaveToGstnJobHandler gstr2XJobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2X SaveToGstn Data Execute method is ON with " + "groupcode {} and params {}", groupCode,
					jsonString);
		}
		if (jsonString != null && groupCode != null) {
			try {
				gstr2XJobHandler.saveCancelledInvoices(jsonString, groupCode);
				gstr2XJobHandler.saveActiveInvoices(jsonString, groupCode);
				LOGGER.info("Save to Gstn Processed with args {} ", jsonString);
			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}
}
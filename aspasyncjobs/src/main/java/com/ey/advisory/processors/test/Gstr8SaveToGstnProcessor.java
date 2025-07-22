package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.processors.handler.Gstr8SaveToGstnJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Slf4j
@Component("Gstr8SaveToGstnProcessor")
public class Gstr8SaveToGstnProcessor implements TaskProcessor {
	
	@Autowired
	@Qualifier("Gstr8SaveToGstnJobHandler")
	private Gstr8SaveToGstnJobHandler gstr8JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr8 SaveToGstn Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}
		if (jsonString != null && groupCode != null) {
			try {
				gstr8JobHandler.saveCancelledInvoices(jsonString, groupCode);
				gstr8JobHandler.saveActiveInvoices(jsonString, groupCode);
				LOGGER.info("Save to Gstn Processed with args {} ", jsonString);
			} catch (Exception ex) {
				String msg = "Exception while Saving GSTR8.";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}
	}

}

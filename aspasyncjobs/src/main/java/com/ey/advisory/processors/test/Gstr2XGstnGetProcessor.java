package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr2XGstnGetJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Gstr2XGstnGetProcessor")
@Slf4j
public class Gstr2XGstnGetProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2XGstnGetJobHandlerImpl")
	private Gstr2XGstnGetJobHandler gstr2XJobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2X Get Gstn Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}
		try {
			if (jsonString != null && groupCode != null) {
				gstr2XJobHandler.gstr2XGstnGetCall(jsonString, groupCode);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr2X Get Gstn Processed with args {} ",
							jsonString);
				}
			}
		} catch (Exception e) {
			LOGGER.error(
					"Exception occured during Api processing  in processor class",
					e);
			throw new AppException();
		}
	}
}

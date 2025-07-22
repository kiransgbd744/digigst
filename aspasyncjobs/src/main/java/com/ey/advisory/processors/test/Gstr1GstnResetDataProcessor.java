package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr1GstnResetDataJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1GstnResetDataProcessor")
public class Gstr1GstnResetDataProcessor implements TaskProcessor {

	@Autowired
	private Gstr1GstnResetDataJobHandler gstr1JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 Delete Data Save to Gstn Execute method is ON with "
		+ "groupcode {} and params {}", groupCode,
					jsonString);
		}

		if (jsonString != null && groupCode != null) {
			try {
				gstr1JobHandler.gstnReset(jsonString, groupCode);
				LOGGER.info("Gstr1 Delete Data Save to Gstn Processed with args {} ", jsonString);

			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}


}

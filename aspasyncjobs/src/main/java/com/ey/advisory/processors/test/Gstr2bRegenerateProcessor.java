package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.gstr2b.regenerate.GetGstr2bRegenerateJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Service("Gstr2bRegenerateProcessor")
@Slf4j
public class Gstr2bRegenerateProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GetGstr2bRegenerateJobHandlerImpl")
	private GetGstr2bRegenerateJobHandler gstr2bRegenerateHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get Gstr2B regenerate Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			gstr2bRegenerateHandler.getGstr2bRegenerateCall(jsonString, groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get Gstr2b REGENERATE Gstn Processed with args {} ",
						jsonString);

			}
		} catch (Exception ex) {
			String msg = "Gstr2bRegenerateProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}

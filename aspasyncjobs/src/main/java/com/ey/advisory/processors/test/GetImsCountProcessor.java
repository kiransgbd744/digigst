package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.ims.handlers.GetImsCountJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Service("GetImsCountProcessor")
@Slf4j
public class GetImsCountProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GetImsCountJobHandlerImpl")
	private GetImsCountJobHandler imsCountJobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get IMS Count Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			imsCountJobHandler.getImsCountGetCall(jsonString, groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get IMS Count Gstn Processed with args {} ",
						jsonString);

			}
		} catch (Exception ex) {
			String msg = "GetImsCountProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}

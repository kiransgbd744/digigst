package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.inward.einvoice.GetIrnListJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Service("GetIrnListProcessor")
@Slf4j
public class GetIrnListProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GetIrnListJobHandlerImpl")
	private GetIrnListJobHandler irnListJobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get IRN LIst Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			irnListJobHandler.getIrnListCall(jsonString, groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get IRN LIst Gstn Processed with args {} ",
						jsonString);

			}
		} catch (Exception ex) {
			String msg = "GetIrnListProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}

package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.ims.handlers.GetImsInvoicesJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Service("GetImsInvoicesProcessor")
@Slf4j
public class GetImsInvoicesProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GetImsInvoicesJobHandlerImpl")
	private GetImsInvoicesJobHandler imsInvoicesJobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get IMS Invoices Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			imsInvoicesJobHandler.getImsInvoicesCall(jsonString, groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get IMS Invoices Gstn Processed with args {} ",
						jsonString);

			}
		} catch (Exception ex) {
			String msg = "GetImsInvoicesProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}

/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr1SaveToGstnDependentRetryHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1SaveToGstnDependentRetryProcessor")
public class Gstr1SaveToGstnDependentRetryProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1SaveToGstnDependentRetryHandler")
	private Gstr1SaveToGstnDependentRetryHandler gstr1SaveDependencyRetryJobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format(
							"Gstr1 SaveToGstn Retry Data Execute method is ON with "
									+ "groupcode {} and params {}"),
					groupCode, jsonString);
		}

		if (jsonString != null && groupCode != null) {
			try {
				gstr1SaveDependencyRetryJobHandler
						.reSaveSpecificErorrCodesOfBatchInvoices(jsonString,
								groupCode);
				LOGGER.info("Save to Gstn Retry Processed with args {} ",
						jsonString);

			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}

}

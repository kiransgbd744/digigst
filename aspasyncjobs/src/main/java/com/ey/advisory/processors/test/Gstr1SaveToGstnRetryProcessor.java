/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr1SaveToGstnRetryJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1SaveToGstnRetryProcessor")
public class Gstr1SaveToGstnRetryProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1SaveToGstnRetryJobHandler")
	private Gstr1SaveToGstnRetryJobHandler gstr1JobHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN_RETRY_ASYNC_START,
				PerfamanceEventConstants.Gstr1SaveToGstnRetryProcessor,
				PerfamanceEventConstants.execute, null);
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("Gstr1 SaveToGstn Retry Data Execute method is ON with "
							+ "groupcode {} and params {}"),
					groupCode, jsonString);
		}

		if (jsonString != null && groupCode != null) {
			try {
				gstr1JobHandler.saveFailedBatchInvoices(jsonString, groupCode);
				LOGGER.info("Save to Gstn Retry Processed with args {} ", jsonString);

			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN_RETRY_ASYNC_END,
				PerfamanceEventConstants.Gstr1SaveToGstnRetryProcessor,
				PerfamanceEventConstants.execute, null);
	}

}

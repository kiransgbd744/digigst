package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.processors.handler.Gstr1SaveToGstnJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr1ASaveToGstnProcessor")
@Slf4j
public class Gstr1ASaveToGstnProcessor implements TaskProcessor {

	@Autowired
	private Gstr1SaveToGstnJobHandler gstr1JobHandler;


	@Override
	public void execute(Message message, AppExecContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.GSTR1_SAVE_TO_GSTIN_ASYNC_START,
				PerfamanceEventConstants.Gstr1SaveToGstnProcessor,
				PerfamanceEventConstants.execute, null);
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr1 SaveToGstn Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}

		if (jsonString != null && groupCode != null) {
			try {
				ProcessingContext gstr1Context = new ProcessingContext();
				gstr1Context.seAttribute(APIConstants.RETURN_TYPE_STR,
						APIConstants.GSTR1A.toUpperCase());
				gstr1JobHandler.saveCancelledInvoices(jsonString, groupCode,
						gstr1Context);
				LOGGER.info("Gstr1 CAN Save to Gstn Processed with args {} ",
						jsonString);
				gstr1JobHandler.saveActiveInvoices(jsonString, groupCode,
						gstr1Context);
				LOGGER.info("Gstr1 Save to Gstn Processed with args {} ",
						jsonString);

			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.GSTR1_SAVE_TO_GSTIN_ASYNC_END,
				PerfamanceEventConstants.Gstr1SaveToGstnProcessor,
				PerfamanceEventConstants.execute, null);
	}

}

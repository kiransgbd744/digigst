package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.processors.handler.Gstr1DeleteGstnDataJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Component("Gstr1DeleteGstnDataProcessor")
public class Gstr1DeleteGstnDataProcessor implements TaskProcessor {

	@Autowired
	private Gstr1DeleteGstnDataJobHandler gstr1JobHandler;

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
				ProcessingContext gstr1Context = new ProcessingContext();
				gstr1Context.seAttribute(APIConstants.RETURN_TYPE_STR,
						APIConstants.GSTR1.toUpperCase());
				gstr1JobHandler.saveAutoDraftedInvoices(jsonString, groupCode, gstr1Context);
				LOGGER.info("Gstr1 Delete Data Save to Gstn Processed with args {} ", jsonString);

			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}

}

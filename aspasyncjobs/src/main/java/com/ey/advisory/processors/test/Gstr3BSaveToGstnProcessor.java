package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.savetogstn.jobs.gstr3B.Gstr3BSaveInvoiceIdentifier;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;

@Component("Gstr3BSaveToGstnProcessor")
public class Gstr3BSaveToGstnProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BSaveToGstnProcessor.class);

	@Autowired
	@Qualifier("Gstr3BSaveInvoiceIdentifierImpl")
	private Gstr3BSaveInvoiceIdentifier saveData;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Gstr3B SaveToGstn Data Execute method is ON");
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (jsonString != null && groupCode != null) {
			try {
				saveData.findSaveInvoices(jsonString, groupCode,
						APIConstants.GSTR3B);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Save to Gstn Processed with args {} ",
							jsonString);
			} catch (Exception ex) {
				String msg = "Exception while initiating the 3B Save";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}

}

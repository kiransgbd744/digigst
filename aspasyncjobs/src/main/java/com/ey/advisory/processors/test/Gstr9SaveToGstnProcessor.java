package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.savetogstn.gstr9.Gstr9SaveToGstnService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr9SaveToGstnProcessor")
public class Gstr9SaveToGstnProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr9SaveToGstnServiceImpl")
	private Gstr9SaveToGstnService gstr9SaveToGstnService;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Gstr9 SaveToGstn Data Execute method is ON");
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		String gstin = requestObject.get("gstin").getAsString();
		String fy = requestObject.get("fy").getAsString();

		if (jsonString != null && groupCode != null) {
			try {
				gstr9SaveToGstnService.callGstr9SaveToGstn(gstin, fy,groupCode);
				
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Save to Gstn9 Processed with args {} ",
							jsonString);
			} catch (Exception ex) {
				String msg = "Exception while initiating the Gstr9 Save";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}

}

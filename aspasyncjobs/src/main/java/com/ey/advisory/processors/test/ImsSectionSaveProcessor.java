/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.service.ims.ImsSectionSaveHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ImsSectionSaveProcessor")
public class ImsSectionSaveProcessor implements TaskProcessor {

	@Autowired
	private ImsSectionSaveHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("invoked IMS SaveToGstn groupcode {} and params {}",
					groupCode, jsonString);
		}

		if (jsonString != null && groupCode != null) {
			try {

				JsonObject json = JsonParser.parseString(jsonString)
						.getAsJsonObject();
				String gstin = json.get("gstin").getAsString();
				String tableType = json.get("tableType").getAsString();
				String action = json.get("action").getAsString();
				handler.saveActiveInvoices(gstin, groupCode, tableType, action);

				LOGGER.debug("Ims Save to Gstn Processed with args {} ",
						jsonString);

			} catch (Exception ex) {
				String msg = "ImsSectionSaveProcessor Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}

}

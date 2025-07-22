package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.service.ims.ImsSaveStatusHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Component("ImsSaveStatusProcessor")
@Slf4j
public class ImsSaveStatusProcessor implements TaskProcessor {

	@Autowired
	private ImsSaveStatusHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside IMS save status processor with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			String gstin = requestObject.get("gstin").getAsString();
			String tableType = requestObject.get("section").getAsString();
			String refId = requestObject.get(APIConstants.REFID).getAsString();
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("IMS Save STATUS API call for -> GSTIN: '%s', "
								+ "tableType: '%s'", gstin, tableType);
				LOGGER.debug(msg);
			}
			handler.callImsSaveStatusApi(gstin, tableType, refId, groupCode);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"IMS Transaction polling is done -> GSTIN: '%s', "
								+ "tableType: '%s', refId : '%s'",
						gstin, tableType, refId);
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			String msg = "App Exception";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

}

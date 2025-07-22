package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr1.einv.EinvSummaryResponseService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EinvSummaryResponseProcessor")
public class EinvSummaryResponseProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("EinvSummaryResponseServiceImpl")
	private EinvSummaryResponseService einvSummaryRespService;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin EinvSummaryResponseProcessor job");

		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"EinvSummaryResponseProcessor User Upload Job"
								+ " executing for groupcode %s and params %s",
						groupCode, jsonString);
				LOGGER.debug(msg);
			}
			JsonParser parser = new JsonParser();
			JsonObject reqJson = (JsonObject) parser.parse(jsonString);

			String configId = reqJson.get("configId").getAsString();

			LOGGER.debug(
					"EinvSummaryResponseProcessor is in progress ");
			einvSummaryRespService.saveEinvSummaryReport(configId);
		} catch (Exception e) {
			String msg = "Error Occurred in EinvSummaryResponseProcessor.";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		}
	}

}

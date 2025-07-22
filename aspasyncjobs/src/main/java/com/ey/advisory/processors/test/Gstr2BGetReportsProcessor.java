package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr2b.Gstr2BGetCallReportsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2BGetReportsProcessor")
public class Gstr2BGetReportsProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2BGetCallReportsServiceImpl")
	private Gstr2BGetCallReportsService service;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String obj = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2B Get Report Job"
							+ " executing for groupcode {} and params {}",
					groupCode, obj);
		}
		/*JsonArray gstins = new JsonArray();
		JsonArray taxPeriods = new JsonArray();
		Gson googleJson = new Gson();
		List<String> gstnsList = null;
		List<String> months = null;
		List<String> taxPeriodList = null;*/
		try {

			JsonObject request = (new JsonParser()).parse(obj)
					.getAsJsonObject();

			Long id = request.has("id") ? request.get("id").getAsLong()
					: null;

			service.generateReport(id);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}

	}
}

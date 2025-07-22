package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr2b.Gstr2BApiServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2BGetMaunalAPIPushProcessor")
public class Gstr2BGetMaunalAPIPushProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2BApiServiceImpl")
	private Gstr2BApiServiceImpl apiServiceImpl;

	@Override
	public void execute(Message message, AppExecContext context) {
		String jsonString = message.getParamsJson();
		Long invocationId = null;
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			invocationId = requestObject.get("invId").getAsLong();

			apiServiceImpl.parseandpersisResp(invocationId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Completed parsing for all ids, marking status as"
						+ " 'COMPLETED' in Control table");
			}
		} catch (Exception ee) {
			String msg = ee.getMessage().length() > 200
					? ee.getMessage().substring(0, 198) : ee.getMessage();
			LOGGER.error("Error while parsing jsons ", msg);
			throw new AppException(ee);
		}
	}
}

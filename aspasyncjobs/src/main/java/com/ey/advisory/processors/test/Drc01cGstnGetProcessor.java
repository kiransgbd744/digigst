package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.services.drc01c.Drc01cGstnService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("Drc01cGstnGetProcessor")
@Slf4j
public class Drc01cGstnGetProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Drc01cGstnServiceImpl")
	private Drc01cGstnService gstnService;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1 Get Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			String gstin = json.get("gstin").getAsString();
			String refId = json.get("refId").getAsString();
			String taxPeriod = json.get("taxPeriod").getAsString();
			String userName = json.get("userName").getAsString();
			gstnService.getDrcRetComSummary(gstin, taxPeriod, refId, userName);
		} catch (Exception ex) {
			String errMsg = "Error occured while invoking GET Summary call.";
			LOGGER.error(errMsg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}
}

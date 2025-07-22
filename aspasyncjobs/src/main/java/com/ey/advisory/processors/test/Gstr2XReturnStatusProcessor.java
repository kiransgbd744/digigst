package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.refidpolling.gstr2x.Gstr2XRefIdPolling;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.PollingMessage;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */

@Service("Gstr2XReturnStatusProcessor")
@Slf4j
public class Gstr2XReturnStatusProcessor implements TaskProcessor {
	@Autowired
	@Qualifier("Gstr2XRefIdPolling")
	private Gstr2XRefIdPolling gstr2xRefIdPolling;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2X Return Status Data Execute method is ON with message {} ", message);
		}
		String groupcode = message.getGroupCode();
		String jsonReq = message.getParamsJson();
		if (jsonReq != null && groupcode != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr2X ReturnStatus- Request is {} ", jsonReq);
			}
			Gson gson = new Gson();
			PollingMessage reqDto = gson.fromJson(jsonReq, PollingMessage.class);
			gstr2xRefIdPolling.processRefIds(reqDto, groupcode);
		}
	}
}

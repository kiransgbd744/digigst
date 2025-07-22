package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.refidpolling.gstr8.Gstr8RefIdPolling;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.PollingMessage;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Service("Gstr8ReturnStatusProcessor")
@Slf4j
public class Gstr8ReturnStatusProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr8RefIdPolling")
	private Gstr8RefIdPolling gstrR8efIdPolling;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr8 Return Status Data Execute method is ON with message {} ",
					message);
		}
		String groupcode = message.getGroupCode();
		String jsonReq = message.getParamsJson();
		if (jsonReq != null && groupcode != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr8 ReturnStatus- Request is {} ",
						jsonReq);
			}
			Gson gson = new Gson();
			PollingMessage reqDto = gson.fromJson(jsonReq,
					PollingMessage.class);
			gstrR8efIdPolling.processRefIds(reqDto, groupcode);
		}
	}
}

package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.refidpolling.itc04.Itc04RefIdPolling;
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

@Service("Itc04ReturnStatusProcessor")
@Slf4j
public class Itc04ReturnStatusProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("Itc04RefIdPolling")
	private Itc04RefIdPolling itc04RefIdPolling;
	
	@Override
	public void execute(Message message, AppExecContext context) {
	if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(
				"Itc04 Return Status Data Execute method is ON with message {} ",
				message);
	}
	String groupcode = message.getGroupCode();
	String jsonReq = message.getParamsJson();
	if (jsonReq != null && groupcode != null) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Itc04 ReturnStatus- Request is {} ",
					jsonReq);
		}
		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq,
				PollingMessage.class);
		itc04RefIdPolling.processRefIds(reqDto, groupcode);
	}
		
	}

}

package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.refidpolling.gstr1.Gstr1RefIdPolling;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.PollingMessage;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Service("Gstr1ReturnStatusProcessor")
@Slf4j
public class Gstr1ReturnStatusProcessor  implements TaskProcessor {


	@Autowired
	@Qualifier("Gstr1RefIdPolling")
	private Gstr1RefIdPolling gstr1RefIdPolling;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr1 Return Status Data Execute method is ON with message {} ",
					message);
		}
		String groupcode = message.getGroupCode();
		String jsonReq = message.getParamsJson();
		if (jsonReq != null && groupcode != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr1 ReturnStatus- Request is {} ", jsonReq);
			}
			Gson gson = new Gson();
			PollingMessage reqDto = gson.fromJson(jsonReq,
					PollingMessage.class);
			// String groupCode = TenantContext.getTenantId();
			//LOGGER.info("groupCode {} is set", groupcode);
			gstr1RefIdPolling.processRefIds(reqDto, groupcode);
		}

	}

}

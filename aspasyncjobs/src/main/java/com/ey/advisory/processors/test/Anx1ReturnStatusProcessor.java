package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.anx1.Anx1SaveToGstnReqDto;
import com.ey.advisory.app.services.refidpolling.gstr1.Anx1RefIdPolling;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Service("Anx1ReturnStatusProcessor")
@Slf4j
public class Anx1ReturnStatusProcessor  implements TaskProcessor {


	@Autowired
	@Qualifier("Anx1RefIdPolling")
	private Anx1RefIdPolling gstr1RefIdPolling;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx1 Return Status Data Execute method is ON");
		}
		String groupcode = message.getGroupCode();
		String jsonReq = message.getParamsJson();
		if (jsonReq != null && groupcode != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"executing the processRefIds method in Gstr1 SaveToGstn");
			}
			Gson gson = new Gson();
			Anx1SaveToGstnReqDto reqDto = gson.fromJson(jsonReq,
					Anx1SaveToGstnReqDto.class);
			if (reqDto.getGroupcode() == null
					|| reqDto.getGroupcode().isEmpty()) {
				reqDto.setGroupcode(groupcode);
			}
			// String groupCode = TenantContext.getTenantId();
			LOGGER.info("groupCode {} is set", reqDto.getGroupcode());
			gstr1RefIdPolling.processRefIds(reqDto);
		}

	}

}

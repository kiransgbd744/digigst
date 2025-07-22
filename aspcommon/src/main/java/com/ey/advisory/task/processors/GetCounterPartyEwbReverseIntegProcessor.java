/**
 * 
 */
package com.ey.advisory.task.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.ewb.reverseinteg.GetCPReverseIntegService;
import com.ey.advisory.ewb.reverseinteg.ReverseIntegParamsDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid.Khan
 *
 */
@Component("GetCounterPartyEwbReverseIntegProcessor")
@Slf4j
public class GetCounterPartyEwbReverseIntegProcessor implements TaskProcessor  {
	
	@Autowired
	@Qualifier("GetCPReverseIntegServiceImpl")
	private GetCPReverseIntegService cpRevIntegService;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
		Gson gson = new Gson();
		JsonObject obj = new JsonParser().parse(message.getParamsJson())
				.getAsJsonObject();
		ReverseIntegParamsDto req = gson.fromJson(obj,
				ReverseIntegParamsDto.class);
		cpRevIntegService.pushCPEwbsToErp(req);
		} catch(Exception e) {
			LOGGER.error("failed in reverse integration processor", e);
			throw new AppException("failed in reverse integration processor", e);
		}
		
	}

}

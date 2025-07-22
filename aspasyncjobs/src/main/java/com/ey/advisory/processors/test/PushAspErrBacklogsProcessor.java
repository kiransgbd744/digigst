/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.jsonpushback.PushJsonToErp;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Service("PushAspErrBacklogsProcessor")
@Slf4j
public class PushAspErrBacklogsProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("PushJsonToErpImpl")
	private PushJsonToErp pushJsonToErp;

	@Override
	public void execute(Message message, AppExecContext context) {
		LOGGER.error("asp error job push processor started for backog errors");
		String jsonString = message.getParamsJson();
		JsonObject requestParams = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Long minId =  requestParams.get("minId").getAsLong();
		Long maxId =  requestParams.get("maxId").getAsLong();
		String gstin =  requestParams.get("gstin").getAsString();
		
			LOGGER.error("PushAspErrBacklogsProcessor " + message );
		
		pushJsonToErp.pushErrorRecordJson(minId,maxId, gstin);
	}

}

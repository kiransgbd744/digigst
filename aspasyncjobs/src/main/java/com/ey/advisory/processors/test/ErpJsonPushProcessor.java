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
@Service("ErpJsonPushProcessor")
@Slf4j
public class ErpJsonPushProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("PushJsonToErpImpl")
	private PushJsonToErp pushJsonToErp;

	@Override
	public void execute(Message message, AppExecContext context) {
		String jsonString = message.getParamsJson();
		JsonObject requestParams = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Long batchId =  requestParams.get("batchId").getAsLong();
		String gstin =  requestParams.get("gstin").getAsString();
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("JOB_PARAMS " + message );
		}
		//batchId = Long.valueOf("1831");
		//gstin = "33GSPTN0481G1ZA";
		pushJsonToErp.pushErrorRecordJson(batchId, gstin);
	}

}

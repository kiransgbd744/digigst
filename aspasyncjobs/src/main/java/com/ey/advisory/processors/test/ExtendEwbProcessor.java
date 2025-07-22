/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.services.ewb.EwbService;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Service("ExtendEwbProcessor")
public class ExtendEwbProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("EwbServiceImpl")
	private EwbService ewbService;

	@Override
	public void execute(Message message, AppExecContext context) {
		String jsonString = message.getParamsJson();
		Gson gson = new Gson();
		JsonObject obj = new JsonParser().parse(jsonString)
				.getAsJsonObject();
		JsonObject reqObj = obj.get("jsonString").getAsJsonObject();
		ExtendEWBReqDto req = gson.fromJson(reqObj,
				ExtendEWBReqDto.class);
		boolean updateDb = obj.get("updateDb").getAsBoolean();
		boolean erpPush = obj.get("erpPush").getAsBoolean();
		ewbService.extendEWB(req, req.getGstin(),updateDb,erpPush);
		
	}

}

/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.services.ewb.EwbService;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Slf4j
@Service("EwbConsolidatedProcessor")
public class EwbConsolidatedProcessor implements TaskProcessor {
	
	@Autowired
	@Qualifier("EwbServiceImpl")
	EwbService ewbService;
	
	
	@Override
	public void execute(Message message, AppExecContext context) {
		
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		ConsolidateEWBReqDto req = gson.fromJson(json, ConsolidateEWBReqDto.class);
		
		ewbService.consolidateEWB(req, req.getGstin(), true, false);
		
	}

}

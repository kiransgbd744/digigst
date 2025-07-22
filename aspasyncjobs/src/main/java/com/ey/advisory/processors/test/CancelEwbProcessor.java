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
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Service("CancelEwbProcessor")
public class CancelEwbProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("EwbServiceImpl")
	private EwbService ewbService;

	@Override
	public void execute(Message message, AppExecContext context) {
		String jsonString = message.getParamsJson();
		Gson gson = new Gson();
	
		CancelEwbReqDto req = gson.fromJson(jsonString,
				CancelEwbReqDto.class);
		ewbService.cancelEwb(req, true, true);
	}

}

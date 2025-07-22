
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.services.einvoice.CancelIrnService;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.google.gson.Gson;

/**
 * @author Arun.KA
 *
 */
@Service("CancelEinvProcessor")
public class CancelEinvProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("CancelIrnServiceImpl")
	CancelIrnService cancelIrnService;
	
	@Override
	public void execute(Message message, AppExecContext context) {
		
		String jsonString = message.getParamsJson();
		Gson gson = new Gson();
		CancelIrnReqDto req = gson.fromJson(jsonString,
				CancelIrnReqDto.class);
		
		cancelIrnService.CancelEinvEwbRequest(req);
		
	}

}

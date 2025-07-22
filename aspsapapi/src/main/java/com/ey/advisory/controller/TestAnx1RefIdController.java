package com.ey.advisory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.anx1.Anx1SaveToGstnReqDto;
import com.ey.advisory.app.services.refidpolling.gstr1.Anx1RefIdPolling;
import com.google.gson.Gson;


/**
 * @author Siva.Nandam
 *
 */
@RestController
public class TestAnx1RefIdController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TestAnx1RefIdController.class);
	
	@Autowired
	@Qualifier("Anx1RefIdPolling")
	private Anx1RefIdPolling anx1RefIdPolling;
	
	@PostMapping(value = "/ui/testAnx1RefIdStaus")
	public ResponseEntity<String> getrefIdStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}
		Gson gson =new Gson();
		Anx1SaveToGstnReqDto reqDto = 
				gson.fromJson(jsonReq, Anx1SaveToGstnReqDto.class);
		
		//String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", reqDto.getGroupcode());
		return anx1RefIdPolling.processRefIds(reqDto);
	}


}

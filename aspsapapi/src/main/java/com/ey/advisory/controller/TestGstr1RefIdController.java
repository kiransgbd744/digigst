package com.ey.advisory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.refidpolling.gstr1.Gstr1RefIdPolling;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.PollingMessage;
import com.google.gson.Gson;


/**
 * @author Siva.Nandam
 *
 */
@RestController
public class TestGstr1RefIdController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TestGstr1RefIdController.class);
	
	@Autowired
	@Qualifier("Gstr1RefIdPolling")
	private Gstr1RefIdPolling gstr1RefIdPolling;
	
	@PostMapping(value = "/ui/testRefIdStaus")
	public ResponseEntity<String> getrefIdStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}
		Gson gson =new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq, PollingMessage.class);
		
		String groupCode = TenantContext.getTenantId();
		return gstr1RefIdPolling.processRefIds(reqDto, groupCode);
	}


}

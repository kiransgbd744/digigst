package com.ey.advisory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.refidpolling.gstr1.GSTR1RefIdPollingManager;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.PollingMessage;
import com.google.gson.Gson;

/**
 * @author Siva.Nandam
 *
 */
@RestController
public class ANX1SaveToGstnController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ANX1SaveToGstnController.class);

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("DefaultAnx1RefIdPollingManager")
	private GSTR1RefIdPollingManager anx1RefIdPollingManager;

	@PostMapping(value = "/ui/getAnx1RefIdStaus")
	public ResponseEntity<String> getStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq, PollingMessage.class);

		if (reqDto.getReturnType() != null) {
			return anx1RefIdPollingManager.processAnx1RefIds(reqDto, groupCode);
		} else {
			return null;
		}
	}

}

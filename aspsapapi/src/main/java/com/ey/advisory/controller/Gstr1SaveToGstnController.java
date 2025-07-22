package com.ey.advisory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.refidpolling.gstr1.GSTR1RefIdPollingManager;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.PollingMessage;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Hemasundar.J
 *
 */
@RestController
public class Gstr1SaveToGstnController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SaveToGstnController.class);

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("DefaultGSTR1RefIdPollingManager")
	private GSTR1RefIdPollingManager gstr1RefIdPollingManager;

	@PostMapping(value = "/ui/getRefIdStaus")
	public ResponseEntity<String> getrefIdStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}

		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);

		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq, PollingMessage.class);
		if (reqDto.getReturnType() != null) {
			return gstr1RefIdPollingManager.processGstr1RefIds(reqDto,
					groupCode);
		} else {
			return null;
		}
	}

	@PostMapping(value = "/ui/saveToGstnStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getStatus(@RequestBody String jsonString) {
		LOGGER.debug("executing the getStatus method in SaveToGstn");
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		try {
			JsonObject json = saveToGstnEventStatus
					.findLatestStatus(jsonString);
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(json);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while Fetching  Latest status";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/deleteFromGstn")
	public void deleteFromGstn() {
		LOGGER.debug("executing the deleteFromGstn method in SaveToGstn");
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		// Implementation is pending
	}

	@PostMapping(value = "/deleteFromAsp")
	public void deleteFromAsp() {
		LOGGER.debug("executing the deleteFromAsp method in SaveToGstn");
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		// Implementation is pending

	}

}

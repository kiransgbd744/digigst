package com.ey.advisory.controllers.gstr3b;

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

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Divya1.B
 *
 */

@RestController
public class Gstr3BSubmitToGstnController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BSubmitToGstnController.class);

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@PostMapping(value = "/ui/gstr3BSubmitToGstn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr3BSubmitToGstn(
			@RequestBody String jsonString) {
		LOGGER.debug("Recevied GSTR3B Submit request {}", jsonString);
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		String status = null;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			String gstin = requestObject.get("gstin").getAsString();
			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (authStatus.equals("A")) {
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.SUBMITGSTR3B, requestObject.toString(),
						JobConstants.SYSTEM, JobConstants.PRIORITY,
						JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);
				status = "GSTR3B Save Request Submitted Successfully";
			} else {
				status = "Auth Token is Inactive, Please Activate";
			}
			JsonElement respBody = gson.toJsonTree(status);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while Submit GSTR3B";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

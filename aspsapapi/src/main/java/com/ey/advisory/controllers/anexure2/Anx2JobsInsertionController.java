/**
 * 
 */
package com.ey.advisory.controllers.anexure2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Hemasundar.J
 *
 */
@RestController
public class Anx2JobsInsertionController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2JobsInsertionController.class);

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@PostMapping(value = "/ui/anx2GstnGetJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createAnx2GetGstnJob(@RequestBody String jsonParam) {
		LOGGER.debug("createAnx2GetGstnJob insertion started.");
		try {
			String groupCode = TenantContext.getTenantId();
			 AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.ANX2_GSTN_GET, jsonParam, 
					JobConstants.SYSTEM,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree("Success");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

package com.ey.advisory.app.controllers.gstnNotices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.get.notices.handlers.GSTNoticesGetCallHandler;
import com.ey.advisory.app.get.notices.handlers.GstNoticesReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.Jain
 *
 */

@RestController
@Slf4j
public class GstNoticesController {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GSTNoticesGetCallHandler gstNoticesGetCallHandler;

	@PostMapping(value = "/ui/initiateGstNotices", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> initaiteGstNotices(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		String groupCode = TenantContext.getTenantId();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"initiateGstNotices Request received from UI as {} for group code {}",
						request, groupCode);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = JsonParser.parseString(request)
					.getAsJsonObject();
			JsonObject asJsonObject = requestObject.get("req")
					.getAsJsonObject();
			GstNoticesReqDto dtos = gson.fromJson(asJsonObject,
					GstNoticesReqDto.class);

			List<AsyncExecJob> jobList = new ArrayList<>();

			ResponseEntity<String> response = gstNoticesGetCallHandler
					.getGstNotices(dtos.getGstins(), jobList);

			if (!jobList.isEmpty())
				asyncJobsService.createJobs(jobList);

			return response;

		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for GST Notice Get Call ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

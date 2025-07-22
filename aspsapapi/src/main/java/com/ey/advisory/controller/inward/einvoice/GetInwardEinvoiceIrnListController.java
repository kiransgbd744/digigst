package com.ey.advisory.controller.inward.einvoice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.inward.einvoice.InwardEinvoiceGetCallHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
public class GetInwardEinvoiceIrnListController {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private InwardEinvoiceGetCallHandler inwardEinvoiceGetCallHandler;

	@PostMapping(value = "/ui/GetCallInwardIrnList", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr2AGstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		String groupCode = TenantContext.getTenantId();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GetCallInwardIrnList Request received from UI as {} for group code {}",
						request, groupCode);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = JsonParser.parseString(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);

			List<AsyncExecJob> jobList = new ArrayList<>();

			ResponseEntity<String> response = inwardEinvoiceGetCallHandler
					.getCallIRNList(dtos, jobList);

			if (!jobList.isEmpty())
				asyncJobsService.createJobs(jobList);

			return response;

		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for  Get Call Inward Irn List ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

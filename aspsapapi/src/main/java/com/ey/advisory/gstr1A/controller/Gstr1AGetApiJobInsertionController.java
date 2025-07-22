/**
 * 
 */
package com.ey.advisory.gstr1A.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeReadyStatusFYRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9PeriodWiseRepository;
import com.ey.advisory.app.data.services.Gstr1A.Gstr1AGetCallHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@RestController
@Slf4j
public class Gstr1AGetApiJobInsertionController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("GstinGetStatusServiceImpl")
	private GstinGetStatusService gstinGetStatusService;

	@Autowired
	Gstr9PeriodWiseRepository gstr9PeriodWiseRepo;

	@Autowired
	Gstr9ComputeReadyStatusFYRepository gstr9FyRepo;

	@Autowired
	Gstr1AGetCallHandler gstr1AGetCallHandler;

	@PostMapping(value = "/ui/Gstr1AGstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr1AGstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1AGstnGetSection Request received from UI as {} ",
						request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);

			List<AsyncExecJob> jobList = new ArrayList<>();
			
			PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
					PublicApiConstants.GSTR1A_GSTN_GET_SEC_RET);

			ResponseEntity<String> response = gstr1AGetCallHandler
					.createGstr1Gstn(dtos, jobList);

			if (!jobList.isEmpty())
				asyncJobsService.createJobs(jobList);

			return response;
		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for Gstr1 GET ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

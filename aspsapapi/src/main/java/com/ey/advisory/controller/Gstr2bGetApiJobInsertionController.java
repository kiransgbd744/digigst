package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class Gstr2bGetApiJobInsertionController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@PostMapping(value = "/ui/getGstr2BResponse", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr2AGstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray gstins = new JsonArray();
		Gson googleJson = new Gson();
		List<String> gstnsList = null;		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2GetGstr2bController.createGstr2BGstnGetJob() "
							+ "method Request received from UI as {} ",
					request);
		}
		try {

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject().getAsJsonObject("req");

			if ((requestObject.has("gstins"))
					&& (requestObject.getAsJsonArray("gstins").size() > 0)) {
				gstins = requestObject.getAsJsonArray("gstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstnsList = googleJson.fromJson(gstins, listType);
			}
			
			JsonArray respBody = new JsonArray();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Getting all the active GSTNs for GSTR2B GET");
			}
			Map<String, String> authStatusMap = authTokenService
					.getAuthTokenStatusForGstins(gstnsList);
			List<String> activeGstin = new ArrayList<>();
			
			if (gstnsList != null) {
			gstnsList.forEach(gstin -> {
				String authStatus = authStatusMap.get(gstin);
				String msg = "Auth Token is Inactive, Please Activate";
				JsonObject json = new JsonObject();
				if (!"A".equalsIgnoreCase(authStatus)) {
					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);
				} else {
					activeGstin.add(gstin);
					json.addProperty("gstin", gstin);
					json.addProperty("msg",
							"Get GSTR2B Request Initiated Successfully");
					respBody.add(json);
				
				}
			});
			}
			String groupCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", groupCode);

			if (!activeGstin.isEmpty()) {
				asyncJobsService.createJob(groupCode, JobConstants.GSTR2B_GET,
						request, userName, 1L, null, null);
			}
			APIRespDto dto = new APIRespDto("Sucess",
					"Requested GSTR2B GET successfully");

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Before Return GSTR2B GET:{} ");
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (AppException e) {
			String msg = "Auth Token(s) is Inactive,Please Activate gstn(s)";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while Creating job for GET GSTR2B ";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}
}

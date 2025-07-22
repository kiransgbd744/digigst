package com.ey.advisory.controller.customised.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.app.data.services.customisedreport.CustomizedReportUserLogUserDto;
import com.ey.advisory.app.data.services.customisedreport.CustomizedReportUserLogUserService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class CustomizedReportUserLogController {

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;
	
	@Autowired
	@Qualifier("CustomizedReportUserLogUserService")
	CustomizedReportUserLogUserService service;
	
	final private static String reportType = "GSTR1_TRANS_LEVEL";

	@PostMapping(value = "/ui/customizedUserLog", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUserLogs(@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin CustomizedReportUserLogController.getUserLogs,"
						+ "Parsing Input request";
				LOGGER.debug(msg);
			}
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			String entityId = null;

			JsonObject requestObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			if (requestObject.has("entityId")) {
				entityId = requestObject.get("entityId").getAsString();
			}
			
			CustomizedReportUserLogUserDto data = service.getData(reportType, 
							Long.valueOf(entityId), userName);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "CustomizedReportUserLogController"
						+ ".getUserLogs Preparing Response Object";
				LOGGER.debug(msg);
			}
			
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(data);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End CustomizedReportUserLogController.getUserLogs() method"
						+ ", before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

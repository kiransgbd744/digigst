package com.ey.advisory.controller.days.revarsal180;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.service.days.revarsal180.ITCReversal180DaysReqDto;
import com.ey.advisory.service.days.revarsal180.ITCReversal180DaysRequestIdWiseDto;
import com.ey.advisory.service.days.revarsal180.ITCReversal180DaysRequestIdWiseService;
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

@Slf4j
@RestController
public class ITCReversal180DaysRequestIdWiseController {

	@Autowired
	@Qualifier("ITCReversal180DaysRequestIdWiseServiceImpl")
	private ITCReversal180DaysRequestIdWiseService service;
	
	
	@PostMapping(value = "/ui/getITCReversal180ReqIdWise", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReversal180RequestStatus(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin ITCReversal180DaysRequestIdWiseController"
						+ ".getReversal180RequestStatus() method"; 
				LOGGER.debug(msg);
			}
			
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
							
			ITCReversal180DaysReqDto reqDto = gson.fromJson(json,
					ITCReversal180DaysReqDto.class);
			
			List<ITCReversal180DaysRequestIdWiseDto> status = service
					.getRequestStatus(reqDto, userName);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End ITCReversal180DaysRequestIdWiseController"
						+ ".getReversal180RequestStatus, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error ITCReversal180DaysRequestIdWiseController "
					+ "while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

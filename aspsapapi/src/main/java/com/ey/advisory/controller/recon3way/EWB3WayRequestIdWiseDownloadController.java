package com.ey.advisory.controller.recon3way;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;
import com.ey.advisory.gstr2.initiaterecon.EWB3WayRequesIdWiseDownloadTabService;
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
public class EWB3WayRequestIdWiseDownloadController {

	@Autowired
	@Qualifier("EWB3WayRequesIdWiseDownloadTabServiceImpl")
	private EWB3WayRequesIdWiseDownloadTabService service;
	
	@RequestMapping(value = "/ui/ewb3WayDownloadIdWise", method = 
			RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ewb3WayDownloadIdWise(
			@RequestBody String jsonString) {
		
		LOGGER.debug("Inside EWB3WayRequestIdWiseDownloadController"
				+ ".ewb3WayDownloadIdWise() method and file type is {} ");
		

		try {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		String configId = json.get("configId").getAsString();
		
				
		List<Gstr2RequesIdWiseDownloadTabDto> resp = service
						.getDownloadData(Long.valueOf(configId));
		JsonObject resps = new JsonObject();
		JsonElement respBody = gson.toJsonTree(resp);
		resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resps.add("resp", respBody);
		return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

	} catch (JsonParseException ex) {
		String msg = "Error while parsing the input Json";
		LOGGER.error(msg, ex);

		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		return new ResponseEntity<>(resp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (Exception ex) {
		String msg = "Unexpected error while retriving Data Status ";
		LOGGER.error(msg, ex);
		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		return new ResponseEntity<>(resp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

}

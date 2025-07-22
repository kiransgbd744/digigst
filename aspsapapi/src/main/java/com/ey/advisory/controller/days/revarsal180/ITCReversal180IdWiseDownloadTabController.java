package com.ey.advisory.controller.days.revarsal180;

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
import com.ey.advisory.service.days.revarsal180.ITCReversal180DownloadTabDto;
import com.ey.advisory.service.days.revarsal180.ITCReversal180IdWiseDownloadTabService;
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
public class ITCReversal180IdWiseDownloadTabController {

	@Autowired
	@Qualifier("ITCReversal180IdWiseDownloadTabServiceImpl")
	private ITCReversal180IdWiseDownloadTabService service;
	
	@RequestMapping(value = "/ui/reversal180DownloadIdWise", method = 
			RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getdownloadPopUp(
			@RequestBody String jsonString) {
		
		LOGGER.debug("Inside ITCReversal180IdWiseDownloadTabController"
				+ ".getdownloadPopUp() method and file type is {} ");

		try {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		String computeId = json.get("computeId").getAsString();
				
		List<ITCReversal180DownloadTabDto> resp = service
				.getDownloadData(Long.valueOf(computeId));
		
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

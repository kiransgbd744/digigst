/**
 * 
 */
package com.ey.advisory.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2InitiateMatchingAPManualService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */



@Slf4j
@RestController
public class TestApReconController {
	
	@Autowired
	@Qualifier("Gstr2InitiateMatchingAPManualServiceImpl")
	private Gstr2InitiateMatchingAPManualService service;
	
	@PostMapping(value = "/ui/testApRecon", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bAutoCalcAndSave(@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString).getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			Long configId = reqJson.get("configId").getAsLong();

			String gstin = reqJson.get("gstin").getAsString();

			Long entityId = reqJson.get("entityId").getAsLong();
			
			service.executeAPManualRecon(configId, Arrays.asList(gstin), entityId);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree("succees"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while get3BAutoCalAndSave";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}


}

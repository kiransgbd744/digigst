/**
 * 
 */
package com.ey.advisory.gstr2.ap.recon.controller;

import java.util.ArrayList;
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

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.common.GsonUtil;
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

@Slf4j
@RestController
public class CheckForAPReconOptedController {

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;
	
	@RequestMapping(value = "/ui/checkForAP", method = 
			RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> checkForAPAnswer(
			@RequestBody String jsonString) {
		
		LOGGER.debug("Inside CheckForAPReconOptedController"
				+ ".checkForAPAnswer() method and file type is {} ");
		
		List<Long> entityIds = new ArrayList<>();
		
		boolean flag = false;

		try {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		Long entityId = json.get("entityId").getAsLong();
				
		entityIds.add(entityId);
		List<Long> optedEntities = entityConfigPemtRepo
				.getAllEntitiesOpted2B(entityIds, "I27");
		
		if(optedEntities == null || optedEntities.isEmpty()){
			
			flag = true;
		}
			//hinding reqId dropdown --> AP false, NOn AP True
		JsonObject resps = new JsonObject();
		JsonElement respBody = gson.toJsonTree(flag);
		resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resps.add("resp", respBody);
		return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

	} catch (JsonParseException ex) {
		String msg = "Error while parsing the input Json : "
				+ "CheckForAPReconOptedController";
		LOGGER.error(msg, ex);

		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		return new ResponseEntity<>(resp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (Exception ex) {
		String msg = "Unexpected error while retriving Data Status :"
				+ " CheckForAPReconOptedController ";
		LOGGER.error(msg, ex);
		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		return new ResponseEntity<>(resp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

}
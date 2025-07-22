/**
 * 
 */
package com.ey.advisory.controllers.gstr3b.entity.setoff;

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

import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffComputeSaveClosingBalDto;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffComputeSaveInnerDto;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3bSetOffComputeSaveToDBService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
public class Gstr3bSetOffComputeSaveToDBController {

	@Autowired
	@Qualifier("Gstr3bSetOffComputeSaveToDBServiceImpl")
	private Gstr3bSetOffComputeSaveToDBService service;

	@PostMapping(value = "/ui/save3BsetOffComputeToDb", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr3bSetOffComputeSaveToDB(
			@RequestBody String jsonString) {

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonArray reqArray = requestObject.get("req").getAsJsonArray();

			Gstr3BSetOffComputeSaveInnerDto userInputList = new Gson().fromJson(
					reqArray.get(0), Gstr3BSetOffComputeSaveInnerDto.class);

			Gstr3BSetOffComputeSaveClosingBalDto userInputList1 = new Gson()
					.fromJson(reqArray.get(1),
							Gstr3BSetOffComputeSaveClosingBalDto.class);

			Gstr3BSetOffComputeSaveClosingBalDto userInputList2 = new Gson()
					.fromJson(reqArray.get(2),
							Gstr3BSetOffComputeSaveClosingBalDto.class);

			List<Gstr3BSetOffComputeSaveClosingBalDto> newdtoList = new ArrayList<>();
			newdtoList.add(userInputList1);
			newdtoList.add(userInputList2);
			
			 service.saveComputedtoDb(userInputList, newdtoList);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	
}
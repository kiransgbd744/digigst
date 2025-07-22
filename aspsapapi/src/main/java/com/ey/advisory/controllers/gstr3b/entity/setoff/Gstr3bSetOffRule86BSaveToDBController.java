/**
 * 
 */
package com.ey.advisory.controllers.gstr3b.entity.setoff;

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
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffSnapDetailsService;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetRule86BSaveDto;
import com.google.gson.Gson;
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
public class Gstr3bSetOffRule86BSaveToDBController {

	@Autowired
	@Qualifier("Gstr3BSetOffSnapDetailsServiceImpl")
	private Gstr3BSetOffSnapDetailsService service;

	@PostMapping(value = "/ui/gstr3BOffSet86BSave", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr3bSetOffComputeSaveToDB(
			@RequestBody String jsonString) {

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			
			
			Gstr3BSetRule86BSaveDto userInputList = new Gson()
					.fromJson(requestObject, Gstr3BSetRule86BSaveDto.class);

			service.saveRule86BToDb(userInputList);

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
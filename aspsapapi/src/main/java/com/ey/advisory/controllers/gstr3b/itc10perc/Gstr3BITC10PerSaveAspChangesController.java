package com.ey.advisory.controllers.gstr3b.itc10perc;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;
import com.ey.advisory.app.gstr3b.itc10perc.service.Gstr3BITC10PerSaveASpChangesService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr3BITC10PerSaveAspChangesController {

	@Autowired
	@Qualifier("Gstr3BITC10PerSaveAspChangesServiceImpl")
	private Gstr3BITC10PerSaveASpChangesService service;

	@PostMapping(value = "/ui/gstr3BITC10PerSaveAspChanges", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstinDashboardUserInput(
			@RequestBody String jsonString) {

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			JsonElement userInputElement = requestObject.get("userInputList");
			Type listType = new TypeToken<List<Gstr3BGstinAspUserInputDto>>() {
			}.getType();

			List<Gstr3BGstinAspUserInputDto> userInputList = new Gson()
					.fromJson(userInputElement, listType);

			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");
			
			String status = requestObject.has("status")
					? requestObject.get("status").getAsString() : "";
			if (Strings.isNullOrEmpty(status))
				throw new AppException("status cannot be empty");
			
			service.saveItcChangsToAsp(gstin, taxPeriod, userInputList, status);

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
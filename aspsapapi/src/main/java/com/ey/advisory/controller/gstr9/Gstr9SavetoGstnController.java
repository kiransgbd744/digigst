package com.ey.advisory.controller.gstr9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.savetogstn.gstr9.Gstr9SaveToGstnService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */

@Slf4j
@RestController
public class Gstr9SavetoGstnController {

	@Autowired
	private Gstr9SaveToGstnService gstr9SaveToGstnService;

	@PostMapping(value = "/ui/gstr9SaveToGstn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveToGstr9(@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE saveToGstr9");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String gstin = requestObject.get("gstin").getAsString();

			String fy = requestObject.get("fy").getAsString();

			if (Strings.isNullOrEmpty(gstin) || Strings.isNullOrEmpty(fy)) {
				String msg = "Gstin and Financial year is mandatory for save to gstn.";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			String respData = gstr9SaveToGstnService.saveGstr9DataToGstn(gstin,
					fy);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respData);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while Saving data to gstn  ";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}

	}
}

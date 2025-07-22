package com.ey.advisory.controllers.gstr3b.entity.setoff;

import java.lang.reflect.Type;
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

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3bSetOffEntityComputeSetOffService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
public class Gstr3BEntityComputeSetOffController {

	@Autowired
	@Qualifier("Gstr3bSetOffEntityComputeSetOffServiceImpl")
	Gstr3bSetOffEntityComputeSetOffService service;

	private static final String GSTIN = "gstins";

	@PostMapping(value = "/ui/gstr3BSetOffCompute", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGSTINsandStatus(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr3BEntityComputeSetOffController"
						+ ".getGSTINsandStatus ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject errorResp = new JsonObject();
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			JsonArray gstins = new JsonArray();

			List<String> gstinsList = null;
			String taxPeriod = reqJson.has("taxPeriod")
					? reqJson.get("taxPeriod").getAsString() : null;

			if (reqJson.has(GSTIN)
					&& reqJson.getAsJsonArray(GSTIN).size() > 0) {
				gstins = reqJson.getAsJsonArray(GSTIN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstinsList = gson.fromJson(gstins, listType);
			}

			if (gstinsList == null || gstinsList.isEmpty()) {
				String msg = "Gstins cannot be empty";
				errorResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				LOGGER.error(msg);
				return new ResponseEntity<>(errorResp.toString(),
						HttpStatus.OK);
			}

			String statusData = service
					.getComputeStatus(gstinsList, taxPeriod);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(statusData);
			gstinResp.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr3BEntityComputeSetOffController"
						+ ".getGSTINsandStatus ,before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

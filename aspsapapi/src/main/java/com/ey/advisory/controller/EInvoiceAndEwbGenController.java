/**
 * 
 */
package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.einvoice.EInvAndEwbGenRequest;
import com.ey.advisory.app.docs.dto.einvoice.EInvAndEwbGenResponse;
import com.ey.advisory.app.services.docs.einvoice.EInvoiceGenService;
import com.ey.advisory.app.services.docs.einvoice.EwbGenService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class EInvoiceAndEwbGenController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvoiceAndEwbGenController.class);

	@Autowired
	@Qualifier("EInvoiceGenService")
	private EInvoiceGenService einvoiceGenService;

	@Autowired
	@Qualifier("EwbGenService")
	private EwbGenService ewbGenService;

	@RequestMapping(value = "/ui/eInvoiceGenAction", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eInvoiceGenAction(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Type listType = new TypeToken<List<EInvAndEwbGenRequest>>() {
			}.getType();
			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<EInvAndEwbGenRequest> eInvStatusDto = gson.fromJson(json,
					listType);

			EInvAndEwbGenRequest criteria = new EInvAndEwbGenRequest();
			List<Long> ids = new ArrayList<>();
			List<String> gstns = new ArrayList<>();
			for (EInvAndEwbGenRequest dto : eInvStatusDto) {
				Long id = dto.getDocId();
				String gstin = dto.getGstin();
				ids.add(id);
				gstns.add(gstin);
				criteria.setDocIds(ids);
				criteria.setGstins(gstns);
			}
			List<EInvAndEwbGenResponse> recResponse = einvoiceGenService
					.eInvoiceGen(criteria);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data ";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/ewbGenAction", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ewbGenAction(@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Type listType = new TypeToken<List<EInvAndEwbGenRequest>>() {
			}.getType();
			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<EInvAndEwbGenRequest> eInvStatusDto = gson.fromJson(json,
					listType);

			EInvAndEwbGenRequest criteria = new EInvAndEwbGenRequest();
			List<Long> ids = new ArrayList<>();
			List<String> gstns = new ArrayList<>();
			for (EInvAndEwbGenRequest dto : eInvStatusDto) {
				Long id = dto.getDocId();
				String gstin = dto.getGstin();
				ids.add(id);
				gstns.add(gstin);
				criteria.setDocIds(ids);
				criteria.setGstins(gstns);
			}
			List<EInvAndEwbGenResponse> recResponse = ewbGenService
					.ewbGenAction(criteria);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data ";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
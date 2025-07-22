/**
 * 
 */
package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.RevertingInvReqDto;
import com.ey.advisory.app.docs.dto.RevertingInvResponseDto;
import com.ey.advisory.app.services.docs.RevertingInvoiceService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class RevertingInvoiceController {

	@Autowired
	@Qualifier("RevertingInvoiceService")
	private RevertingInvoiceService revertingInvoiceService;

	@RequestMapping(value = "/ui/revertingInvoice", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> revertingInvoice(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			RevertingInvReqDto criteria = gson.fromJson(json,
					RevertingInvReqDto.class);

			RevertingInvResponseDto revertingResponse = revertingInvoiceService
					.revertingInv(criteria);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(revertingResponse);
			if(revertingResponse.getNewId() != null){
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			}else{
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			}
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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

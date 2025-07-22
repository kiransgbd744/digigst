package com.ey.advisory.controller.days.revarsal180;

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
import com.ey.advisory.service.days.revarsal180.ITCReversal180DaysComputeReqDto;
import com.ey.advisory.service.days.revarsal180.ITCReversal180DaysComputeService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class ITCReversal180DaysComputeController {

	@Autowired
	@Qualifier("ITCReversal180DaysComputeServiceImpl")
	private ITCReversal180DaysComputeService service;
	
	
	@PostMapping(value = "/ui/getITCReversal180Compute", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> computeForItcReversal(
			@RequestBody String reqJson) {

		JsonObject requestObject = (new JsonParser()).parse(reqJson)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String.format(
				"Inside ITCReversal180DaysComputeController " + "reqJson %s ",
				reqJson);
		LOGGER.debug(msg);
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			ITCReversal180DaysComputeReqDto reqDto = gson.fromJson(json,
					ITCReversal180DaysComputeReqDto.class);

			String respData = service.itcReversalCompute(reqDto);
					

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respData);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			msg = "Unexpected error while invoking "
					+ "ITCReversal180DaysComputeController.computeForItcReversal() "
					+ "method ";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus
					.INTERNAL_SERVER_ERROR);

		}
	}
}
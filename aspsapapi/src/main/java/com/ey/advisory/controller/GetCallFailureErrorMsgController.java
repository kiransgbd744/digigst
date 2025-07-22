package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.common.GetCallErrorMessageDto;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@RestController
@Slf4j
public class GetCallFailureErrorMsgController {

	@Autowired
	private GetAnx1BatchRepository getAnx1BatchRepository;

	@PostMapping(value = { "/ui/getCallFailureErrMsg" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGetCallErrMsg(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		try {
			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json for getCallFailureErrMsg -> " + reqJson);
			}
			GetCallErrorMessageDto dto = gson.fromJson(reqJson,
					GetCallErrorMessageDto.class);
			
			String errMsg = getAnx1BatchRepository.getFailedErrMsg(dto.getGstin(),
					dto.getSection(), dto.getTaxPeriod(), dto.getReturnType());
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("errMsg -> " + errMsg);
			}
			
			dto.setErrMsg(errMsg);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(dto));
		} catch (Exception e) {
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			LOGGER.error("Exception Occured:{} ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}

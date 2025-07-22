package com.ey.advisory.controllers.anexure1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx1.recipientsummary.RecipientResponseDetailService;
import com.ey.advisory.app.anx1.recipientsummary.RecipientResponseDetailsDto;
import com.ey.advisory.app.anx1.recipientsummary.RecipientSummaryRequestDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
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
public class Anx1RecipientResponseDetailsController {

	@Autowired
	@Qualifier("RecipientResponseDetailServiceImpl")
	private RecipientResponseDetailService responseDetailService;
	
	@Autowired
	@Qualifier("Anx1RecipientSummaryValidator")
	Anx1RecipientSummaryValidator validator;
	
	@PostMapping(value = "/ui/getRecipientResponseDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRecipientResponseDetails(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin getRecipientResponseDetails"
						+ ".getRecipientResponseDetails ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			RecipientSummaryRequestDto request  = gson.fromJson(json,
					RecipientSummaryRequestDto.class);

			
			validator.recipientValidator(request);

		String condition = Anx1RecipientSummaryValidator.
				queryCondition(request);

			List<RecipientResponseDetailsDto> response = responseDetailService
					.getRecipientResponseDetail(condition, request);

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(response);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End getRecipientResponseDetails"
					+ ".getRecipientResponseDetails, before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {

			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}
}
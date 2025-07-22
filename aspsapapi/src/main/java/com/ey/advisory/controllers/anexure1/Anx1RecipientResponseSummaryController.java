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

import com.ey.advisory.app.anx1.recipientsummary.RecipientResponseSummaryDto;
import com.ey.advisory.app.anx1.recipientsummary.RecipientResponseSummaryService;
import com.ey.advisory.app.anx1.recipientsummary.RecipientSummaryRequestDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@RestController
@Slf4j
public class Anx1RecipientResponseSummaryController {

	@Autowired
	@Qualifier("RecipientResponseSummaryServiceImpl")
	RecipientResponseSummaryService recipientRespSummary;
	
	@Autowired
	Anx1RecipientSummaryValidator validator;

	@PostMapping(value = "/ui/getRecipientResponseSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllEntities(
			@RequestBody String recipientRespSummaryRequest) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin getRecipientResponseSummary "
					+ ". getRecipientResponseSummary,Parsing Input request";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser())
				.parse(recipientRespSummaryRequest).getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			RecipientSummaryRequestDto request = gson.fromJson(json,
					RecipientSummaryRequestDto.class);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(request.toString());
			}
			
			//validating mandatory field
			validator.recipientValidator(request);
			
			//validating optional field
			String condition = Anx1RecipientSummaryValidator
					.queryCondition(request);

			List<RecipientResponseSummaryDto> response = recipientRespSummary
					.getRecipientResponseSummary(condition, request);

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(response);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "End getRecipientResponseSummary "
						+ " before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {

			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}
}

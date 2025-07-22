/**
 * 
 */
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

import com.ey.advisory.app.anx1.recipientsummary.RecipientSRSummaryDto;
import com.ey.advisory.app.anx1.recipientsummary.RecipientSRSummaryService;
import com.ey.advisory.app.anx1.recipientsummary.RecipientSummaryRequestDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 * This Class is Controller For ReceipientSRSummary the Service first call is
 * made here After Converting request to appropriate Dto and validating 
 * mandatory fields only then we processed further or else it is Populated
 * as Error.
 */

@Slf4j
@RestController
public class Anx1RecipientSRSummaryController {
	
	@Autowired
	@Qualifier("RecipientSRSummaryServiceImpl")
	RecipientSRSummaryService recipientSRSummaryService; 
	
	@Autowired
	@Qualifier("Anx1RecipientSummaryValidator")
	Anx1RecipientSummaryValidator receipientSumValidatin;
	
	
	@PostMapping( value = "/ui/getSRSummaryDetails", consumes = 
		{MediaType.APPLICATION_JSON_VALUE},produces= {
				MediaType.APPLICATION_JSON_VALUE} )
	public ResponseEntity<String> getSRSummaryDetails(@RequestBody
			String  recipientSummaryRequest)
	{
		
		JsonObject requestObject = (new JsonParser()).parse(
				recipientSummaryRequest).getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		
		
		try {
			RecipientSummaryRequestDto request  = gson.fromJson(json,
					RecipientSummaryRequestDto.class);
			if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("Json Parser Parsed and"
							+ "Converted to RecipientSummaryRequestDto :"+
							request.toString());
			}
			receipientSumValidatin.recipientValidator(request);
			String validationQuery = Anx1RecipientSummaryValidator
					.queryCondition(request);
			List<RecipientSRSummaryDto> srSummary =recipientSRSummaryService.
				getAnx1SRSummary(request,validationQuery);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(srSummary);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		catch( Exception ex)
		{
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}
}
	

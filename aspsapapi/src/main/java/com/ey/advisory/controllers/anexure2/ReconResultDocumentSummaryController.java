package com.ey.advisory.controllers.anexure2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx.reconresult.ReconResultDocSummaryReqDto;
import com.ey.advisory.app.anx.reconresult.ReconResultDocSummaryRespDto;
import com.ey.advisory.app.anx.reconresult.ReconResultDocSummaryService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 *
 */

@Slf4j
@RestController
public class ReconResultDocumentSummaryController {
	
	@Autowired
	@Qualifier("ReconResultDocSummaryServiceImpl")
	ReconResultDocSummaryService reconResultService;
	
	@PostMapping(value = "/ui/getDocumentSummReconResult", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDocumentSummReconResult
	 (@RequestBody String jsonString) {
		
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin DocumentSummReconResult "
						+ " FilterForGstinController"
						+ "getReconResultReportForGstin ,Parsing Input request";
				LOGGER.debug(msg);
			}
			
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			ReconResultDocSummaryReqDto reqDto = gson.fromJson(json,
					ReconResultDocSummaryReqDto.class);
			if (reqDto.getEntityId() == null || reqDto.getTaxPeriod() == null ||
					reqDto.getTaxPeriod().length() != 6) {
				String msg = "EntityId or TaxPeriod Cannot be Empty";
				throw new AppException(msg);
			}
			
			List<ReconResultDocSummaryRespDto> respObj = reconResultService.
					getReconResultDocSummDetails(reqDto);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respObj);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ae) {
			return InputValidationUtil.createJsonErrResponse(ae);
		}
		
	}		
}

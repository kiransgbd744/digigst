/**
 * 
 */
package com.ey.advisory.controllers.anexure2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx.reconresult.ReconResultUpdateReqDto;
import com.ey.advisory.app.anx.reconresult.ReconResultUpdateService;
import com.ey.advisory.app.util.GsonUtil;
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
public class ReconResultUpdateController {
	
	@Autowired
	@Qualifier("ReconResultUpdateServiceImpl")
	public ReconResultUpdateService reconResultUpdate;

	@PostMapping(value = "/ui/updateReconResultAction", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateReconResultUserActions(
			@RequestBody String jsonString) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin ReconResultReportUpdate with User Action";
				LOGGER.debug(msg);
			}
			
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			
			    ReconResultUpdateReqDto request  = gson.fromJson(json,
			    		ReconResultUpdateReqDto.class);
				if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("Json Parser Parsed and"
								+ "Converted to ReconResultUpdateReqDto :"+
								request.toString());
				}
		
				int updateNo =reconResultUpdate.updateReconUserActions(request);
				String msg = "Total No of Records Updated "+updateNo;
				JsonObject resp = new JsonObject();
				JsonObject detResp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(msg);
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

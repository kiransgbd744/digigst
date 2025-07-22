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

import com.ey.advisory.app.anx2.initiaterecon.ReconResponseDataStatusDto;
import com.ey.advisory.app.anx2.initiaterecon.ReconResponseDataStatusReqDto;
import com.ey.advisory.app.anx2.initiaterecon.ReconResponseDataStatusService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class ReconResponseDataStatusController {
	
	@Autowired
	@Qualifier("ReconResponseDataStatusServiceImpl")
	private ReconResponseDataStatusService statusService;
	
	@PostMapping(value = "/ui/getReconResponseDataStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconResponseDataStatus(
			@RequestBody String jsonString) {
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject errorResp = new JsonObject();
		
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside ReconResponseDataStatusController"
						+ ".getReconResponseDataStatus");
			}
			JsonObject requestObject = (new JsonParser())
					.parse(jsonString).getAsJsonObject();

			JsonElement json = requestObject.get("req").getAsJsonObject();
			
			ReconResponseDataStatusReqDto request = new Gson()
					.fromJson(json, ReconResponseDataStatusReqDto.class);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Invoking getReconResponseDataStatus() "
						+ "method, requestJson : "+request);
			}
			List<ReconResponseDataStatusDto> respList = 
					statusService.getReconResponseDataStatus(request);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Invoked getReconResponseDataStatus() "
						+ "method, respList : "+respList);
			}
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
}



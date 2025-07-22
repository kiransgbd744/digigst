package com.ey.advisory.controller.sac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.sac.SacDashboardURLService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class SacGstr1OutwardReportController {

	@Autowired
	@Qualifier("SacDashboardURLService")
	private SacDashboardURLService service;
	
	@PostMapping(value = "/ui/callSacGstr1OutwardReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> callSacGstr1OutwardReport(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE SacGstr1OutwardReportController."
						+ "callSacGstr1OutwardReport() method");
			}
			
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			
			String reportName = reqJson.has("reportName") 
					? reqJson.get("reportName").getAsString() : null;
			String entityId = reqJson.has("entityId") 
							? reqJson.get("entityId").getAsString() : null;		

			String sacUrl = service.getSacUrl(reportName, 
					Integer.valueOf(entityId));

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(sacUrl);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (QueryTimeoutException ex) {
			String msg = "Error while Calling  Stroe proc ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (PersistenceException ex) {
			String msg = ex.getMessage();
			LOGGER.error("Error while Calling  proc USP_SAC_GSTR1_OUTWARD_RPT"
					,ex.getMessage());
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error in SacGstr1OutwardReportController."
						+ "callSacGstr1OutwardReport() method";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
}
/**
 * 
 */
package com.ey.advisory.service.asprecon.auto.recon.erp.report;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;

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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author QD194RK
 *
 */

@RestController
@Slf4j
public class TestERPReportController {

	@Autowired
	@Qualifier("Gstr2AutoReconErpReportFetchDetailsImpl")
	private Gstr2AutoReconErpReportFetchDetails service;

	@PostMapping(value = "/ui/testErpReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> callSacGstr1OutwardReport(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE TestController");
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Long configId = requestObject.get("configId").getAsLong();
			Long entityId = requestObject.get("entityId").getAsLong();

			service.generateReport(configId, entityId);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree("success");
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (QueryTimeoutException ex) {
			String msg = "Error while Calling  TestController ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (PersistenceException ex) {
			String msg = ex.getMessage();
			LOGGER.error("Error Test",
					ex.getMessage());
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error Test";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
}
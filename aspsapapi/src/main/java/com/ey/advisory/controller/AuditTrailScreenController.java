/**
 * 
 */
package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.AuditTrailScreenReqDto;
import com.ey.advisory.app.docs.dto.AuditTrailScreenSummaryRespDto;
import com.ey.advisory.app.services.daos.audit.trail.AuditTrailScreenService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class AuditTrailScreenController {

	@Autowired
	@Qualifier("AuditTrailScreenServiceImpl")
	private AuditTrailScreenService auditTrailScreenService;

	@PostMapping(value = "/ui/getAuditTrailScreenIOData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getConsolidatedEwbDetails(
			@RequestBody String jsonString) {

		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

			AuditTrailScreenSummaryRespDto auditTrailResp = null;

			AuditTrailScreenReqDto request = gson.fromJson(reqJson.toString(),
					AuditTrailScreenReqDto.class);
			if ("O".equalsIgnoreCase(request.getType())) {

				auditTrailResp = auditTrailScreenService
						.getAuditTrailOutwardResp(request);
			} else if ("I".equalsIgnoreCase(request.getType())) {

				auditTrailResp = auditTrailScreenService
						.getAuditTrailInwardResp(request);
			}
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(auditTrailResp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing auditTrailResp "
					+ " Data " + ex;
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

package com.ey.advisory.controller.gstr2;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateMatchingReconService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr2InitiateMatchingReconController {

	@Autowired
	@Qualifier("Gstr2InitiateMatchingReconServiceImpl")
	Gstr2InitiateMatchingReconService initiateReconcileService;

	@PostMapping(value = "/ui/gstr2InitiateMatching",
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<String> InitiateReconcile(
			@RequestBody String jsonString) {
		try {

			String toTaxPeriod2A = null;
			String fromTaxPeriod2A = null;
			String toTaxPeriodPR = null;
			String fromTaxPeriodPR = null;
			String toDocDate = null;
			String fromDocDate = null;
			String reconType = null;
			Boolean mandatoryReports = true;

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Begin Gstr2InitiateMatchingRecon to Initiate "
								+ "Recon : %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();
			String entId = requestObject.get("entityId").getAsString();
			Long entityId = Long.valueOf(entId);
			JsonArray gstins = requestObject.getAsJsonArray("gstins");

			if (requestObject.has("toTaxPeriod")) {
				toTaxPeriodPR = requestObject.get("toTaxPeriod").getAsString();
			}
			if (requestObject.has("fromTaxPeriod")) {
				fromTaxPeriodPR = requestObject.get("fromTaxPeriod")
						.getAsString();
			}
			if (requestObject.has("toTaxPeriod2A")) {
				toTaxPeriod2A = requestObject.get("toTaxPeriod2A")
						.getAsString();
			}
			if (requestObject.has("fromTaxPeriod2A")) {
				fromTaxPeriod2A = requestObject.get("fromTaxPeriod2A")
						.getAsString();
			}
			if (requestObject.has("toDocDate")) {
				toDocDate = requestObject.get("toDocDate").getAsString();
			}
			if (requestObject.has("fromDocDate")) {
				fromDocDate = requestObject.get("fromDocDate").getAsString();
			}
			if (requestObject.has("reconType")) {
				reconType = requestObject.get("reconType").getAsString();
			}
			
			if (requestObject.has("mandatory")) {
				mandatoryReports = requestObject.get("mandatory").getAsBoolean();
			}

			if (toTaxPeriod2A != null && fromTaxPeriod2A != null
					&& toTaxPeriodPR != null && fromTaxPeriodPR != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"InitiateReconMatching "
									+ "Parameters toReturnPeriod2A %s To "
								+ "fromReturnPeriod2A %s toReturnPeriodPR %s"
									+ " To fromReturnPeriodPR %s, reconType %s "
									+ ": ",
							toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
							fromTaxPeriodPR, reconType);
					LOGGER.debug(msg);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("InitiateReconMatching "
							+ "Parameters toDocDate %s To fromDocDate %s "
							+ ": ", toDocDate, fromDocDate);
					LOGGER.debug(msg);
				}
			}
			Gson googleJson = new Gson();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> gstinlist = googleJson.fromJson(gstins, listType);

			if (CollectionUtils.isEmpty(gstinlist))
				throw new AppException("User Does not have any gstin");

			JsonArray addlReports = requestObject.getAsJsonArray("addlReports");
			ArrayList<String> addlReportsList = googleJson.fromJson(addlReports,
					listType);

			String status = initiateReconcileService.initiatReconcile(gstinlist,
					entityId, toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
					fromTaxPeriodPR, toDocDate, fromDocDate, addlReportsList, 
					reconType, mandatoryReports);

			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinDetResp.add("status", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"End Gstr2InitiateMatchingRecon to Initiate Recon"
								+ " before returning response : %s",
						gstinDetResp);
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

}

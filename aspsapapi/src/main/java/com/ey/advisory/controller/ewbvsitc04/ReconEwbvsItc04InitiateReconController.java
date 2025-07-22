package com.ey.advisory.controller.ewbvsitc04;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04InitiateReconService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@RestController
@Slf4j
public class ReconEwbvsItc04InitiateReconController {

	@Autowired
	@Qualifier("EwbVsItc04InitiateReconServiceImpl")
	EwbVsItc04InitiateReconService initiateReconcileService;

	@PostMapping(value = "/ui/EwbVsItc04InitiateRecon",
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<String> InitiateRecon(
			@RequestBody String jsonString) {

		
		String criteria = null;
		String fy = null;
		String fromTaxPeriod = null;
		String toTaxPeriod = null;

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Begin ReconEwbvsItc04InitiateReconController to Initiate "
							+ "Recon : %s", jsonString);
			LOGGER.debug(msg);
		}
		
		JsonObject json =  JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject requestObject = json.get("req").getAsJsonObject();
		
		Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();

		try {
			Long entityId = requestObject.get("entityId").getAsLong();
			JsonArray gstins = requestObject.getAsJsonArray("gstins");
			criteria = requestObject.get("criteria").getAsString();
			fy = requestObject.get("fy").getAsString();
			fromTaxPeriod = requestObject.get("fromTaxPeriod").getAsString();
			toTaxPeriod = requestObject.get("toTaxPeriod").getAsString();
			JsonArray addlReport = requestObject.getAsJsonArray("addReport");
			
			if (fromTaxPeriod != null && fy != null && toTaxPeriod != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"InitiateRecon" + "Parameters fromTaxPeriod %s, toTaxPeriod %s,"
									+ "fy %s , reconType %s "
									+ ": ",
									fromTaxPeriod, toTaxPeriod, fy, criteria);
					LOGGER.debug(msg);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("InitiateRecon "
							+ "Parameters fromTaxPeriod %s fy %s "
							+ ": ", fromTaxPeriod, fy);
					LOGGER.debug(msg);
				}
			}
			Gson googleJson = new Gson();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> gstinlist = googleJson.fromJson(gstins, listType);
			List<String> addReport = googleJson.fromJson(addlReport, listType);
			
			if (CollectionUtils.isEmpty(gstinlist))
				throw new AppException("User Does not have any gstin");

			String status = initiateReconcileService.initiatReconcile(gstinlist,
					entityId, fromTaxPeriod, toTaxPeriod, fy, criteria, addReport);

			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinDetResp.add("status", respBody);
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"End ReconEwbvsItc04InitiateReconController to Initiate Recon"
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

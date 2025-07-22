/**
 * 
 */
package com.ey.advisory.controllers.anexure2;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx2.initiaterecon.GstinsForEntityService;
import com.ey.advisory.app.anx2.initiaterecon.InitiateReconFetchGstinsInfoDto;
import com.ey.advisory.app.anx2.initiaterecon.InitiateReconcileService;
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
 * @author Arun.KA
 *
 */

@Slf4j
@RestController
public class Anx2InitiateReconController {

	@Autowired
	@Qualifier("GstinsForEntityServiceImpl")
	GstinsForEntityService gstinsForEntityService;

	@Autowired
	@Qualifier("InitiateReconcileServiceImpl")
	InitiateReconcileService initiateReconcileService;

	@PostMapping(value = "/ui/getGstinsForAnx2EntityId", 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<String> getGstinsDetails(
			@RequestBody String jsonString) {
		try{
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin Anx2InitiateRecon to get Gstins :"
					+ " %s",jsonString);
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		Long entityId = requestObject.has("entityId") && NumberUtils
				.isCreatable(requestObject.get("entityId").toString())
						? requestObject.get("entityId").getAsLong() : null;
		String taxPeriod = requestObject.get("taxPeriod").getAsString();

		List<InitiateReconFetchGstinsInfoDto> gstinsdetails =
				gstinsForEntityService.getGstinsInfo(entityId, taxPeriod);

		JsonObject gstinDetResp = new JsonObject();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(gstinsdetails);
		gstinDetResp.add("gstinDet", respBody);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", gstinDetResp);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("End Anx2InitiateRecon with Gstins"
				+ " before returning response : %s",gstinDetResp.toString());
			LOGGER.debug(msg);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}

	@PostMapping(value = "/ui/anx2InitiateMatching", 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<String> InitiateReconcile(
			@RequestBody String jsonString) {
		try{
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin Anx2InitiateRecon to "
					+ "Initiate Recon : %s",jsonString);
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();
		String entId = requestObject.get("entityId").getAsString();
		Long entityId = Long.valueOf(entId);
		JsonArray gstins = requestObject.getAsJsonArray("gstins");
		JsonArray infoReports = requestObject.getAsJsonArray("infoReports");
		String taxPeriod = requestObject.get("taxPeriod").getAsString();
		Gson googleJson = new Gson();

		Type listType = new TypeToken<ArrayList<String>>() {
		}.getType();
		ArrayList<String> gstinlist = googleJson.fromJson(gstins, listType);
		ArrayList<String> infoReportlist = googleJson.fromJson(infoReports,
				listType);

		String status = initiateReconcileService.initiatReconcile(gstinlist,
				infoReportlist, taxPeriod, entityId);

		JsonObject gstinDetResp = new JsonObject();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(status);
		gstinDetResp.add("status", respBody);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", gstinDetResp);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("End Anx2InitiateRecon to Initiate Recon"
					+ " before returning response : %s",gstinDetResp);
			LOGGER.debug(msg);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	}



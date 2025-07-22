package com.ey.advisory.controller.gstr1.sales.register;

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

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.service.gstr1.sales.register.SalesRegisterInitiateReconService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class SalesRegisterInitiateReconController {

	@Autowired
	@Qualifier("SalesRegisterInitiateReconServiceImpl")
	SalesRegisterInitiateReconService initiateReconcileService;

	@PostMapping(value = "/ui/salesRegisterInitiateRecon",
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<String> InitiateRecon(
			@RequestBody String jsonString) {

		String fromReturnPeriod = null;
		String toReturnPeriod = null;
		String criteria = null;

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Begin SalesRegisterInitiateReconController to Initiate "
							+ "Recon : %s", jsonString);
			LOGGER.debug(msg);
		}
		JsonObject json = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject requestObject = json.get("req").getAsJsonObject();
		
		Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();

		try {
			Long entityId = requestObject.get("entityId").getAsLong();
			JsonArray gstins = requestObject.getAsJsonArray("gstins");
			fromReturnPeriod = requestObject.get("fromReturnPeriod")
					.getAsString();
			toReturnPeriod = requestObject.get("toReturnPeriod").getAsString();
			criteria = requestObject.get("criteria").getAsString();
//			String gstr1Type = requestObject.get("gstr1Type").getAsString();
//			String eInvType = requestObject.get("eInvType").getAsString();
//			String gewbType = requestObject.get("gewbType").getAsString();
//			JsonArray addlReport = requestObject.getAsJsonArray("addReport");
			
			
			
			if (fromReturnPeriod != null && toReturnPeriod != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"InitiateRecon" + "Parameters toReturnPeriod %s To "
									+ "fromReturnPeriod %s , reconType %s "
									+ ": ",
							toReturnPeriod, fromReturnPeriod, criteria);
					LOGGER.debug(msg);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("InitiateRecon "
							+ "Parameters fromReturnPeriod %s To toReturnPeriod %s "
							+ ": ", fromReturnPeriod, toReturnPeriod);
					LOGGER.debug(msg);
				}
			}
			Gson googleJson = new Gson();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> gstinlist = googleJson.fromJson(gstins, listType);
//			List<String> addReport = googleJson.fromJson(addlReport, listType);
			
			if (CollectionUtils.isEmpty(gstinlist))
				throw new AppException("User Does not have any gstin");

			String status = initiateReconcileService.initiatReconcile(gstinlist,
					entityId, fromReturnPeriod, toReturnPeriod, criteria);

			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinDetResp.add("status", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"End SalesRegisterInitiateReconController to Initiate Recon"
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

package com.ey.advisory.controller.gstr9;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.gstr9.Gstr9TaxPaidTabService;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9TaxPaidDashboardDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9TaxPaidMapDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@RestController
@Slf4j
public class Gstr9TaxPaidTabController {

	@Autowired
	@Qualifier("Gstr9TaxPaidTabServiceImpl")
	private Gstr9TaxPaidTabService gstr9TaxPaidTabService;

	@PostMapping(value = "/ui/getGstr9TaxPaidData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9TaxPaidData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String gstin = requestObject.get("gstin").getAsString();
			String fyOld = requestObject.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			List<Gstr9TaxPaidDashboardDto> gstr9TaxPaidDashboardData = 
					gstr9TaxPaidTabService
					.getGstr9TaxPaidDetails(gstin, formattedFy);

			String jsonEINV = gson.toJson(gstr9TaxPaidDashboardData);
			JsonElement gstr9TaxPaidElement = new JsonParser().parse(jsonEINV);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("gstr9TaxPaidDashboardData", gstr9TaxPaidElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jsonObject);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Gstr9 TaxPaid Data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/saveGstr9TaxPaidData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr9TaxPaidData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for saveGstr9TaxPaidData in "
						+ "Gstr9TaxPaidTabController: %s",
						jsonString.toString());
				LOGGER.debug(msg);
			}
			List<Gstr9TaxPaidMapDto> userInputList = new ArrayList<>();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String gstin = requestObject.get("gstin").getAsString();
			String fyOld = requestObject.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			JsonArray userInputs = requestObject.getAsJsonArray("userInputs");
			for (Object o : userInputs) {
				JsonObject jsonLineItem = (JsonObject) o;
				Gstr9TaxPaidMapDto reqDto = (Gstr9TaxPaidMapDto) gson
						.fromJson(jsonLineItem, Gstr9TaxPaidMapDto.class);
				userInputList.add(reqDto);
			}

			gstr9TaxPaidTabService.saveGstr9TaxPaidUserInput(userInputList,
					gstin, formattedFy);

			JsonObject resps = new JsonObject();
			String msg = "Data Saved Successfully";
			resps.add("hdr", new Gson().toJsonTree(new APIRespDto("S", msg)));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while Saving Gstr9 TaxPaid Data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

}

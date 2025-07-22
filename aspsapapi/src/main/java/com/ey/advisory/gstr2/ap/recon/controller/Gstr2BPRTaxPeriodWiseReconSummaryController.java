package com.ey.advisory.gstr2.ap.recon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.getr2.ap.recon.summary.Gstr2APAndNonAPReconSummaryReqDto;
import com.ey.advisory.app.getr2.ap.recon.summary.Gstr2BPRReconSummaryService;
import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryMasterDto;
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
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class Gstr2BPRTaxPeriodWiseReconSummaryController {

	@Autowired
	@Qualifier("Gstr2BPRReconSummaryServiceImpl")
	Gstr2BPRReconSummaryService service;

	@PostMapping(value = "/ui/gstr2BPRTaxReconSummary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> reconSummary(@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		Long configId;
		List<String> gstinList = null;

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Gstr2BPRTaxPeriodWiseReconSummaryController"
								+ ".reconSummary() method :: jsonString : %s",
						jsonString);
				LOGGER.debug(msg);
			}

			Gstr2APAndNonAPReconSummaryReqDto req = gson.fromJson(json,
					Gstr2APAndNonAPReconSummaryReqDto.class);

			configId = req.getConfigId();
			String toTaxPeriod = req.getToTaxPeriod();
			String fromTaxPeriod = req.getFromTaxPeriod();
			String toTaxPeriod_2B = req.getToTaxPeriod_2A();
			String fromTaxPeriod_2B = req.getFromTaxPeriod_2A();
			String criteria = req.getCriteria();

			if (req.getGstins() != null && !req.getGstins().isEmpty()) {
				gstinList = req.getGstins();
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"reconSummary() Parameter " + "configId %s:: ",
						configId);
				LOGGER.debug(msg);
			}

			String reconType = req.getReconType();
			Gstr2ReconSummaryMasterDto respList = service.getReconSummary(
					configId, gstinList, toTaxPeriod, fromTaxPeriod, reconType,
					toTaxPeriod_2B, fromTaxPeriod_2B, criteria);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr2APAndNonAPReconSummaryController"
						+ ".reconSummary, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}

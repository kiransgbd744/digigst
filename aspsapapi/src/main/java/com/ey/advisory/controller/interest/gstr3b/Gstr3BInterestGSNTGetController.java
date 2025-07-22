package com.ey.advisory.controller.interest.gstr3b;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr3b.Gstr3BAutoCalcReportDownloadReq;
import com.ey.advisory.app.gstr3b.Gstr3BGetLiabilityAutoCalcReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.service.interest.gstr3b.Gstr3BInterestGSNTGetService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class Gstr3BInterestGSNTGetController {

	@Autowired
	@Qualifier("Gstr3BInterestGSNTGetServiceImpl")
	Gstr3BInterestGSNTGetService service;

	@PostMapping(value = "/ui/gstr3BInterestAutoCalc", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BInterestAutoCalc(
			@RequestBody String jsonString) {

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("inside Gstr3BInterestGSNTGetController"
					+ ".getGstr3BInterestAutoCalc()");

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			Gstr3BGetLiabilityAutoCalcReqDto req = gson.fromJson(
					reqJson.toString(), Gstr3BGetLiabilityAutoCalcReqDto.class);

			String gstin = req.getGstin();

			String taxPeriod = req.getTaxPeriod();

			if (Strings.isNullOrEmpty(taxPeriod)
					|| Strings.isNullOrEmpty(gstin)) {
				String msg = "Return Period And Gstin cannot be empty";
				LOGGER.error(msg);
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
			}

			String msg = service.getGstnInterestAutoCalc(gstin,
					taxPeriod);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while getGstr3BInterestAutoCalc";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/gstr3BInterestAutoCalcBulk", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bInterestAutoCalcBulk(
			@RequestBody String jsonString) {

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("inside Gstr3BInterestGSNTGetController"
					+ ".getGstr3bInterestAutoCalcBulk ()");

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();
		
		try {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject jsonReq = requestObject.get("req").getAsJsonObject();
		

			Gstr3BAutoCalcReportDownloadReq criteria = gson.fromJson(jsonReq,
					Gstr3BAutoCalcReportDownloadReq.class);

			List<String> gstins = criteria.getGstin();

			String taxPeriod = criteria.getTaxPeriod();

			JsonArray respBody = new JsonArray();

			for (String gstin : gstins) {
				JsonObject json = new JsonObject();
				String apiResponse = service
						.getGstnInterestAutoCalc(gstin, taxPeriod);
				json.addProperty("gstin", gstin.toString());
				json.addProperty("msg", apiResponse);
				respBody.add(json);
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while getGstr3bInterestAutoCalcBulk";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
		}
	}
}

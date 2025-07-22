package com.ey.advisory.controllers.anexure2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx2.reconsummary.Anx2ReconSummaryStatusDto;
import com.ey.advisory.app.anx2.reconsummary.Anx2ReconSummaryStatusService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Anx2ReconSummStatusController {

	@Autowired
	@Qualifier("anx2ReconSummaryStatusServiceImpl")
	Anx2ReconSummaryStatusService anx2ReconSumService;

	@PostMapping(value = "/ui/getDataForAnnx2ReconSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDataForAnnx2ReconSummary(
			@RequestBody String reqJson) {

		int returnPeriod;
		JsonObject requestObject = (new JsonParser()).parse(reqJson)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String taxPeriod = json.get("taxPeriod").getAsString();
			Long entityId = json.get("entityId").getAsLong();

			if (taxPeriod == null || taxPeriod.length() != 6
					|| entityId == null) {
				String msg = "TaxPeriod or Entity Id Cannot "
						+ " be empty or Null";
				throw new AppException(msg);
			} else {
				returnPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Recon Summary Screen Search on foll"
						+ "Parameters Return Period %d ",returnPeriod);
				LOGGER.debug(msg);
			}

			List<Anx2ReconSummaryStatusDto> respObj = anx2ReconSumService
					.getReconDetailSummaryStatus(returnPeriod, entityId);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respObj);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {

			return InputValidationUtil.createJsonErrResponse(e);
		}

	}
}
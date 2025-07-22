package com.ey.advisory.gstr1A.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr1a.einv.Gstr1AEinvInitiateReconService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@RestController
public class Gstr1APrVsSubmittedInitiateReconController {

	@Autowired
	@Qualifier("Gstr1AEinvInitiateReconServiceImpl")
	private Gstr1AEinvInitiateReconService gstinEinvService;

	@PostMapping(value = "/ui/gstr1APRvsSubmittedInitiateMatching", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> Gstr1PRvsSubmittedInitiateReconcile(
			@RequestBody String jsonString) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Begin Gstr1PRvsEinvInitiateReconcile to"
								+ " Initiate Recon : %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();
			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();
			String entId = requestObject.get("req").getAsJsonObject()
					.get("entityId").getAsString();

			Long entityId = Long.valueOf(entId);
			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);
			Map<String, List<String>> dataSecAttrs = annexure1SummaryRequest
					.getDataSecAttrs();

			String gstin = null;

			List<String> gstinlist = null;
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						gstin = key;
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinlist = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}

				}
			}

			String fromTaxPeriod = requestObject.get("req").getAsJsonObject()
					.get("taxPeriodFrom").getAsString();
			String toTaxPeriod = requestObject.get("req").getAsJsonObject()
					.get("taxPeriodTo").getAsString();

			String status = gstinEinvService.initiatPRvsSubmRecon(gstinlist,
					fromTaxPeriod, toTaxPeriod, entityId);

			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinDetResp.add("status", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"End Gstr1Pr vs Submission InitiateRecon to Initiate Recon"
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

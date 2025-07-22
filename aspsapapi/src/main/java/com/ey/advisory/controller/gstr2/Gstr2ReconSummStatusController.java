package com.ey.advisory.controller.gstr2;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2ReconSummaryStatusService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class Gstr2ReconSummStatusController {

	@Autowired
	@Qualifier("Gstr2ReconSummaryStatusServiceImpl")
	private Gstr2ReconSummaryStatusService service;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigRepos;


	@PostMapping(value = "/ui/getDataForGatr2ReconSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDataForAnnx2ReconSummary(
			@RequestBody String reqJson) {

		JsonObject requestObject = (new JsonParser()).parse(reqJson)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		String toTaxPeriod2A = null;
		String fromTaxPeriod2A = null;
		String toTaxPeriodPR = null;
		String fromTaxPeriodPR = null;
		String toDocDate = null;
		String fromDocDate = null;
		String reconType = null;//2APR or 2BPR

		try {
			if (json.has("toTaxPeriod2A")) {
				toTaxPeriod2A = json.get("toTaxPeriod2A").getAsString();
			}
			if (json.has("fromTaxPeriod2A")) {
				fromTaxPeriod2A = json.get("fromTaxPeriod2A").getAsString();
			}
			if (json.has("toTaxPeriod")) {
				toTaxPeriodPR = json.get("toTaxPeriod").getAsString();
			}
			if (json.has("fromTaxPeriod")) {
				fromTaxPeriodPR = json.get("fromTaxPeriod").getAsString();
			}
			if (json.has("toDocDate")) {
				toDocDate = json.get("toDocDate").getAsString();
			}
			if (json.has("fromDocDate")) {
				fromDocDate = json.get("fromDocDate").getAsString();
			}
			if (json.has("reconType")) {
				reconType = json.get("reconType").getAsString();
			}
			Long entityId = json.get("entityId").getAsLong();

			if (toTaxPeriod2A != null && fromTaxPeriod2A != null
					&& toTaxPeriodPR != null && fromTaxPeriodPR != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Recon Summary Screen Search on 2A"
									+ "Parameters toTaxPeriod2A %s To"
									+ " fromTaxPeriod2A %s "
									+ "Parameters toTaxPeriodPR %s "
									+ "To fromTaxPeriodPR %s ,reconType %s"
									+ ": ",
							toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
							fromTaxPeriodPR, reconType);
					LOGGER.debug(msg);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Recon Summary Screen Search on"
							+ "Parameters toDocDate %s To fromDocDate %s "
							+ ": ", toDocDate, fromDocDate);
					LOGGER.debug(msg);
				}
			}
			//159894-US
		Optional<String> findIsItcRejectedOpted = entityConfigRepos.findIsItcRejectedOpted(
				    entityId,
				    "Whether GSTR 2B rejected tables records should participate in recon 2BvsPR?",
				    "R"
				);
		if (LOGGER.isDebugEnabled()) {
		    String msg = String.format("findIsItcRejectedOpted returned Optional: %s", findIsItcRejectedOpted);
		    LOGGER.debug(msg);
		}

		String optedAns = findIsItcRejectedOpted.orElse(null);

		if (LOGGER.isInfoEnabled()) {
		    String msg = String.format("Opted Answer: %s", optedAns);
		    LOGGER.info(msg);
		}

				if (optedAns != null && !optedAns.trim().isEmpty()) {
				    optedAns = "A".equalsIgnoreCase(optedAns) 
				        ? "GSTR 2B Rejected records will participate - Yes" 
				        : "GSTR 2B Rejected records will participate - No";
				} else {
				    optedAns = "GSTR 2B Rejected records will participate - No";
				}
			

			List<Gstr2ReconSummaryStatusDto> respObj = service
					.getReconDetailSummaryStatus(entityId, toTaxPeriod2A,
							fromTaxPeriod2A, toTaxPeriodPR, fromTaxPeriodPR,
							fromDocDate, toDocDate, reconType);
			
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respObj);

			// Add optedAns only if reconType = "2BPR"
			if ("2BPR".equalsIgnoreCase(reconType)) {
			    detResp.addProperty("optedAns", optedAns);
			}

			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			/*JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respObj);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);*/
		} catch (Exception e) {

			return InputValidationUtil.createJsonErrResponse(e);
		}

	}
}
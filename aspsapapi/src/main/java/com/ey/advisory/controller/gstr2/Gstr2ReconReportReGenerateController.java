package com.ey.advisory.controller.gstr2;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@RestController
@Slf4j
public class Gstr2ReconReportReGenerateController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	private final static String REPORT_GENERATION_INPROGRESS = "REPORT_GENERATION_INPROGRESS";

	@PostMapping(value = "/ui/gstr2ReconReportReGenerate", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> gstr2ReconReportReGenerate(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			String msg = null;

			@SuppressWarnings("deprecation")
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject request = requestObject.getAsJsonObject("req");

			Long configId = request.has("configId")
					? request.get("configId").getAsLong()
					: null;

			LOGGER.debug("Inside Gstr2ReconReportReGenerateController ConfigId"
					+ " {}: ", configId);

			Gstr2ReconConfigEntity entity = reconConfigRepo
					.findByConfigId(configId);

			String reportStatus = entity.getStatus();
			String reconType = entity.getType();

			if (REPORT_GENERATION_INPROGRESS.equalsIgnoreCase(reportStatus)) {

				 msg = String.format("Report Generation is already in Progress, "
						+ "Please wait : %d ", configId);
				LOGGER.debug(msg);
				
			} else {

				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("configId", configId);
				jsonParams.addProperty("reconType", reconType);

				asyncJobsService.createJob(
						TenantContext.getTenantId(),
						JobConstants.GSTR2_RECON_Report_ReGenerate,
						jsonParams.toString(), userName, 50L, null, null);

				reconConfigRepo.updateReconConfigStatusAndReportName(
						REPORT_GENERATION_INPROGRESS, null, LocalDateTime.now(),
						configId);

				 msg = String.format("Report Generation Started for "
				 		+ "failed reports : %d", configId);
			}

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(msg);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto("Failed", ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while Re generating recon report";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}
}

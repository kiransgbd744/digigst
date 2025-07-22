package com.ey.advisory.controllers.vendorcommunication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.asprecon.ReturnDataStorageStatusEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnDataStorageStatusRepository;
import com.ey.advisory.common.AppException;
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
 * @author Shashikant.Shukla
 *
 */
@RestController
@Slf4j
public class InitiateVendorGetCallController {

	private static final String FINANCIAL_YEAR = "financialYear";

	private static final String FAILED = "Failed";

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("ReturnDataStorageStatusRepository")
	private ReturnDataStorageStatusRepository returnDataStorageStatusRepo;

	@PostMapping(value = "/ui/initiateVendorFilingByDays", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorsReturnFileStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject request = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String financialYear = request.get(FINANCIAL_YEAR).getAsString();
			String entityId = request.get("entityId").getAsString();
			String noOfDays = request.get("noOfDays").getAsString();
			String userName = SecurityContext.getUser().getUserPrincipalName();

			ReturnDataStorageStatusEntity returnDataStorageStatusEntity = returnDataStorageStatusRepo
					.findByFinancialYear(financialYear);

			if (returnDataStorageStatusEntity == null) {
				returnDataStorageStatusEntity = new ReturnDataStorageStatusEntity(
						financialYear, "SUBMITTED", userName, userName);
				returnDataStorageStatusRepo.save(returnDataStorageStatusEntity);
			} else {
				if (returnDataStorageStatusEntity.getStatus()
						.equalsIgnoreCase("SUBMITTED")
						|| returnDataStorageStatusEntity.getStatus()
								.equalsIgnoreCase("InProgress")) {
					String errorMessage = String.format(
							"Generation of Non Compliant Vendor report for"
									+ " FY - %s is in progress.",
							financialYear);
					LOGGER.error(errorMessage);
					throw new AppException(errorMessage);
				}
			}
			Long requestId = returnDataStorageStatusEntity.getId();
			
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("entityId", entityId);
			jsonParams.addProperty("noOfDays", noOfDays);
			jsonParams.addProperty(FINANCIAL_YEAR, financialYear);
			jsonParams.addProperty("complianceType", "Vendor");
			jsonParams.addProperty("requestId", requestId);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.InitiateGetVendorDaysFiling,
					jsonParams.toString(), userName, 5L, 0L, 0L);

			JsonObject resps = new JsonObject();
			resps.addProperty("status",
					"Return Filing Status Successfully Submitted");
			JsonElement respBody = gson.toJsonTree(resps);
			resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			resp.add("resp", respBody);
			LOGGER.error("exception while intiating vendor filing status: ", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}

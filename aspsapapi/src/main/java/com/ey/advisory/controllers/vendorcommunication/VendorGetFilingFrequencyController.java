package com.ey.advisory.controllers.vendorcommunication;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
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

import com.ey.advisory.app.data.entities.client.asprecon.FrequencyDataStorageStatusEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.FrequencyDataStorageStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
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
public class VendorGetFilingFrequencyController {

	private static final String FINANCIAL_YEAR = "financialYear";

	private static final String FAILED = "Failed";

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	
	@Autowired
	@Qualifier("FrequencyDataStorageStatusRepository")
	private FrequencyDataStorageStatusRepository frequencyDataStorageStatusRepo;

	@PostMapping(value = "/ui/getFilingFrequency", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorsReturnFileStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject request = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String financialYear = request.get(FINANCIAL_YEAR).getAsString();
			String entityId = request.get("entityId").getAsString();
			String complianceType = request.get("complianceType").getAsString();
			String noOfDays = "0";
			if (request.has("noOfDays")) {
				noOfDays = request.get("noOfDays").getAsString();
			}
			
			String userName = SecurityContext.getUser().getUserPrincipalName();

			FrequencyDataStorageStatusEntity freqDataStorageStatusEntity = frequencyDataStorageStatusRepo
					.findByFinancialYearAndEntityId(financialYear,Long.valueOf(entityId));

			if (freqDataStorageStatusEntity == null) {
				freqDataStorageStatusEntity = new FrequencyDataStorageStatusEntity(
						financialYear, "SUBMITTED", userName, userName,Long.valueOf(entityId));
				frequencyDataStorageStatusRepo
						.save(freqDataStorageStatusEntity);
			} else {
				if (freqDataStorageStatusEntity.getStatus()
						.equalsIgnoreCase("SUBMITTED")
						|| freqDataStorageStatusEntity.getStatus()
								.equalsIgnoreCase("InProgress")) {
					String errorMessage = String.format(
							"Earlier initiated Return Filing Frequency Get Call "
							+ "is still under progress. Try after sometime.",
							financialYear);
					LOGGER.error(errorMessage);
					throw new AppException(errorMessage);
				}
			}
			Long requestId = freqDataStorageStatusEntity.getId();

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("entityId", entityId);
			jsonParams.addProperty(FINANCIAL_YEAR, financialYear);
			jsonParams.addProperty("complianceType", complianceType);
			jsonParams.addProperty("requestId", requestId);
			jsonParams.addProperty("noOfDays", noOfDays);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.Get_RETURN_FILING_FREQUENCY,
					jsonParams.toString(), userName, 5L, 0L, 0L);

			JsonObject resps = new JsonObject();
			resps.addProperty("message",
					"Return Filing Frequency Get Call initiated successfully");
			resps.addProperty("status",
					freqDataStorageStatusEntity.getStatus());
			resps.addProperty("freqTime",EYDateUtil.toISTDateTimeFromUTC(
					freqDataStorageStatusEntity.getModifiedOn())
					.format(FORMATTER));
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
	
	@PostMapping(value = "/ui/getVendorFilingFrequency", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSelectedVendorsReturnFileStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject request = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String financialYear = request.get(FINANCIAL_YEAR).getAsString();
			String entityId = request.get("entityId").getAsString();
			String complianceType = request.get("complianceType").getAsString();
			List<String> vendorGstinList = new ArrayList<>();
			if (request.has("vendorGstins")) {
				JsonArray vendorGstinArray = request
						.getAsJsonArray("vendorGstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();

				vendorGstinList = gson.fromJson(vendorGstinArray, listType);
			}
			
			String userName = SecurityContext.getUser().getUserPrincipalName();

			FrequencyDataStorageStatusEntity freqDataStorageStatusEntity = frequencyDataStorageStatusRepo
					.findByFinancialYearAndEntityId(financialYear,Long.valueOf(entityId));

			if (freqDataStorageStatusEntity == null) {
				freqDataStorageStatusEntity = new FrequencyDataStorageStatusEntity(
						financialYear, "SUBMITTED", userName, userName,Long.valueOf(entityId));
				frequencyDataStorageStatusRepo
						.save(freqDataStorageStatusEntity);
			} else {
				if (freqDataStorageStatusEntity.getStatus()
						.equalsIgnoreCase("SUBMITTED")
						|| freqDataStorageStatusEntity.getStatus()
								.equalsIgnoreCase("InProgress")) {
					String errorMessage = String.format(
							"Earlier initiated Return Filing Frequency Get Call "
							+ "is still under progress. Try after sometime.",
							financialYear);
					LOGGER.error(errorMessage);
					throw new AppException(errorMessage);
				}
			}
			Long requestId = freqDataStorageStatusEntity.getId();

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("entityId", entityId);
			jsonParams.addProperty(FINANCIAL_YEAR, financialYear);
			jsonParams.addProperty("complianceType", complianceType);
			jsonParams.addProperty("requestId", requestId);
			if (!vendorGstinList.isEmpty())
				jsonParams.addProperty("vendorGstins",
						vendorGstinList.toString());
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.Get_VENDOR_RETURN_FILING_FREQUENCY,
					jsonParams.toString(), userName, 5L, 0L, 0L);

			JsonObject resps = new JsonObject();
			resps.addProperty("message",
					"Return Filing Frequency Get Call initiated successfully");
			resps.addProperty("status",
					freqDataStorageStatusEntity.getStatus());
			resps.addProperty("freqTime",EYDateUtil.toISTDateTimeFromUTC(
					freqDataStorageStatusEntity.getModifiedOn())
					.format(FORMATTER));
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

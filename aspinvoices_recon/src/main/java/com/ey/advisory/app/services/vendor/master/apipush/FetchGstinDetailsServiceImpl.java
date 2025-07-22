/**
 * 
 */
package com.ey.advisory.app.services.vendor.master.apipush;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.GetGstinMasterDetailApiPushEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GstinValidatorPayloadEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GetGstinVendorMasterDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorApiPushRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorPayloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDao;
import com.ey.advisory.app.services.gstinvalidator.apipush.GstinValidatorReqDto;
import com.ey.advisory.app.services.gstinvalidator.apipush.GstinValidatorReqDtoList;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("FetchGstinDetailsServiceImpl")
@Slf4j
public class FetchGstinDetailsServiceImpl {

	@Autowired
	@Qualifier("taxPayerDetailsDaoImpl")
	TaxPayerDetailsDao taxPayerDao;

	@Autowired
	@Qualifier("GstinValidatorApiPushRepository")
	private GstinValidatorApiPushRepository apiPushRepo;

	@Autowired
	@Qualifier("GstinValidatorPayloadRepository")
	private GstinValidatorPayloadRepository payloadRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GetGstinVendorMasterDetailRepository")
	private GetGstinVendorMasterDetailRepository getGstinVendorMasterDetailRepository;

	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	public void fetchGstinTaxPayerDetails(String payloadId, String groupCode,
			String sourceId) {
		
		try{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter1 = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDateTime = currentDateTime.format(formatter1);
		LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime,
				formatter1);

		GstinValidatorPayloadEntity payloadEntity = payloadRepo
				.getGstinValidatorPayload(payloadId); 

		String jsonString = GenUtil
				.convertClobtoString(payloadEntity.getReqPlayload());

		Gson gson = new Gson();

		JsonObject reqObj = JsonParser.parseString(jsonString).getAsJsonObject()
				.get("req").getAsJsonObject();

		GstinValidatorReqDtoList reqDto = gson.fromJson(reqObj,
				GstinValidatorReqDtoList.class);

		/*
		 * List<Object[]> vendorGstinNameList =
		 * vendorMasterUploadEntityRepository
		 * .getVendorGstinAndVendorName(gstinList);
		 * 
		 * Map<String, String> vendorGstinNameMap = new HashMap<>();
		 * 
		 * for (Object[] result : vendorGstinNameList) { String vendorGstin =
		 * (String) result[0]; String vendorName = (String) result[1];
		 * 
		 * vendorGstinNameMap.put(vendorGstin, vendorName); }
		 * 
		 * if (LOGGER.isDebugEnabled()) { LOGGER.debug(
		 * "Inside the method initiateGetCallForSelectedGstin. Parsed DateTime: {}"
		 * , parsedDateTime); }
		 */
		List<GetGstinMasterDetailApiPushEntity> gstinVendorMasterDetailsList = new ArrayList<>();

		int totalCount = 0;
		int errorCount = 0;

		for (GstinValidatorReqDto gstin : reqDto.getGstinList()) {
			GetGstinMasterDetailApiPushEntity gstinResp = new GetGstinMasterDetailApiPushEntity();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"inside VendorInitiateGetCallServiceImpl before calling api response");

			APIResponse apiResponse = taxPayerDao
					.findTaxPayerDetails(gstin.getGstin(), groupCode);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Following is Tax Payer Detailed "
								+ " Response Dto from Gstin  %s :",
						apiResponse.toString());
				LOGGER.debug(msg);
			}
			if (!apiResponse.isSuccess()) {

				try {
					List<APIError> apiRespErrors = apiResponse.getErrors();
					APIError respError = apiRespErrors.get(0);

					gstinResp.setGstin(gstin.getGstin());
					gstinResp.setCustomerCode(gstin.getCustomerCode());

					// String vendorName = vendorGstinNameMap.get(gstin);
					gstinResp.setPayloadId(payloadId);
					// gstinResp.setVendorNameAsUploaded(vendorName);
					gstinResp.setLastGetCallStatus("Failed");
					gstinResp.setErrorCode(respError.getErrorCode());
					gstinResp.setErrorDiscription(respError.getErrorDesc());
					gstinVendorMasterDetailsList.add(gstinResp);
					++errorCount;
					++totalCount;
				} catch (Exception e) {

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"exception caught in the !apiResponse.isSuccess() block  "
										+ " Response %s from Gstin  %s :",
								gstin);
						LOGGER.debug(msg);
					}
				}

			} else {
				try {
					String apiResp = apiResponse.getResponse();

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Following is Tax Payer Detailed "
										+ " Response %s from Gstin  %s :",
								apiResp, gstin);
						LOGGER.debug(msg);
					}

					JsonObject requestObject = JsonParser.parseString(apiResp)
							.getAsJsonObject();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"inside VendorInitiateGetCallServiceImpl after  calling api response apiResponse is %s",
								apiResp);
					}

					gstinResp.setGstin(gstin.getGstin());
					gstinResp.setCustomerCode(gstin.getCustomerCode());

					// String vendorName = vendorGstinNameMap.get(gstin);
					// gstinResp.setVendorNameAsUploaded(vendorName);
					gstinResp.setLastUpdated(parsedDateTime);
					gstinResp.setBusinessConstitution(
							checkForNull(requestObject.get("ctb")));
					gstinResp.setCentreJurisdiction(
							checkForNull(requestObject.get("ctj")));
					gstinResp.setTaxpayerType(
							checkForNull(requestObject.get("dty")));
					gstinResp.setLegalName(
							checkForNull(requestObject.get("lgnm")));
					gstinResp.setTradeName(
							checkForNull(requestObject.get("tradeNam")));
					gstinResp.setBusinessNatureActivity(
							checkForNullNBA((requestObject.get("nba"))));

					String estatus = requestObject.has("einvoiceStatus")
							? ("Yes".equalsIgnoreCase(checkForNull(
									requestObject.get("einvoiceStatus")))
											? "Yes" : "No")
							: "No";
					gstinResp.setEinvApplicable(estatus);
					if (requestObject.has("pradr")
							&& !requestObject.get("pradr").isJsonNull()) {
						JsonObject address = requestObject.get("pradr")
								.getAsJsonObject();
						if (address.has("addr")) {
							JsonObject addDetails = address.get("addr")
									.getAsJsonObject();
							gstinResp.setBuildingName(
									checkForNull(addDetails.get("bnm")));
							gstinResp.setState(
									checkForNull(addDetails.get("stcd")));
							gstinResp.setLocation(
									checkForNull(addDetails.get("loc")));
							gstinResp.setDoorNum(
									checkForNull(addDetails.get("bno")));
							gstinResp.setFloorNum(
									checkForNull(addDetails.get("flno")));
							gstinResp.setPin(
									checkForNull(addDetails.get("pncd")));
							gstinResp.setStreet(
									checkForNull(addDetails.get("st")));

						}
					}

					String rgdtValue = checkForNull(requestObject.get("rgdt"));
					LocalDate regDate = rgdtValue != null
							&& !rgdtValue.isEmpty()
							&& !"-".equalsIgnoreCase(rgdtValue)
									? LocalDate.parse(rgdtValue, formatter)
									: null;

					gstinResp.setRegistrationDate(regDate);
					gstinResp.setStateJurisdiction(
							checkForNull(requestObject.get("stj")));
					String cxdtValue = checkForNull(requestObject.get("cxdt"));
					LocalDate cancelDate = cxdtValue != null
							&& !cxdtValue.isEmpty()
							&& !"-".equalsIgnoreCase(cxdtValue)
									? LocalDate.parse(cxdtValue, formatter)
									: null;
					gstinResp.setCancellationDate(cancelDate);
					gstinResp.setGstinStatus(
							checkForNull(requestObject.get("sts")));

					gstinResp.setLastGetCallStatus("Success");
					gstinResp.setIsActive(true);
					gstinResp.setPayloadId(payloadId);
					gstinVendorMasterDetailsList.add(gstinResp);
					++totalCount;

				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"exception caught in the apiResponse.isSuccess() block  "
										+ " Response %s from Gstin  %s :",
								gstin);
						LOGGER.debug(msg);
					}
				}
			}
		}
		List<List<GetGstinMasterDetailApiPushEntity>> chunks = Lists
				.partition(gstinVendorMasterDetailsList, 2000);
		// saving all the values
		for (List<GetGstinMasterDetailApiPushEntity> chunk : chunks) {
			apiPushRepo.saveAll(chunk);
		}
		if (totalCount == errorCount) {
			payloadRepo.updateStatus(payloadId, "E", LocalDateTime.now(),
					errorCount, totalCount);
		} else if (errorCount == 0) {
			payloadRepo.updateStatus(payloadId, "P", LocalDateTime.now(),
					errorCount, totalCount);
		} else {
			payloadRepo.updateStatus(payloadId, "PE", LocalDateTime.now(),
					errorCount, totalCount);

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Posting reverse integration job for payloadId : {} ",
					payloadId);

		}

		String jobCategory = APIConstants.GSTIN_VALIDATOR_PAYLOAD_METADATA_REV_INTG;
		JsonObject jobParams = new JsonObject();
		jobParams.addProperty("payloadId", payloadId);
		jobParams.addProperty("sourceId", sourceId);
		jobParams.addProperty("scenarioName",
				APIConstants.GSTIN_VALIDATOR_PAYLOAD_METADATA_REV_INTG);

		asyncJobsService.createJob(groupCode, jobCategory, jobParams.toString(),
				"SYSTEM", JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);
		}catch(Exception ex)
		{
			LOGGER.error(" Error occured while fetching the venndor details {} ",ex);
			throw new AppException(ex);
		}
	}

	private String checkForNull(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? null
				: jsonElement.getAsString();
	}

	private String checkForNullNBA(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsJsonArray().toString().replace("[", "")
						.replace("]", "");
	}
}

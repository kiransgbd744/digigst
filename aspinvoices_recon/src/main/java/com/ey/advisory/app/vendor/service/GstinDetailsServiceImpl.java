/**
 * 
 */
package com.ey.advisory.app.vendor.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.GetGstinMasterDetailApiPushEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorApiPushRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDao;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Service("GstinDetailsServiceImpl")
@Slf4j
public class GstinDetailsServiceImpl {

	@Autowired
	@Qualifier("taxPayerDetailsDaoImpl")
	TaxPayerDetailsDao taxPayerDao;

	@Autowired
	@Qualifier("GstinValidatorApiPushRepository")
	private GstinValidatorApiPushRepository apiPushRepo;

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepo;

	public void fetchGstinTaxPayerDetails(String payloadId, String groupCode) {

		try {
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd/MM/yyyy");
			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter1 = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String formattedDateTime = currentDateTime.format(formatter1);
			LocalDateTime parsedDateTime = LocalDateTime
					.parse(formattedDateTime, formatter1);

			VendorValidatorPayloadEntity payloadEntity = payloadRepo
					.getGstinValidatorPayload(payloadId);

			String jsonString = GenUtil
					.convertClobtoString(payloadEntity.getReqPlayload());

			Gson gson = new Gson();

			JsonObject requestObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonArray json = requestObj.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", json);
			}

			Type listType = new TypeToken<List<VendorReqDto>>() {
			}.getType();

			List<VendorReqDto> reqDto = gson.fromJson(json, listType);

			List<GetGstinMasterDetailApiPushEntity> gstinVendorMasterDetailsList = new ArrayList<>();

			int totalCount = 0;
			int errorCount = 0;

			Set<String> uniqueGstins = reqDto.stream().map(o -> o.getGstin())
					.collect(Collectors.toSet());

			for (String gstin : uniqueGstins) {

				GetGstinMasterDetailApiPushEntity gstinResp = new GetGstinMasterDetailApiPushEntity();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"inside VendorInitiateGetCallServiceImpl before calling api response");

				APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
						groupCode);

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

						gstinResp.setGstin(gstin);
						gstinResp.setCustomerCode(null);
						gstinResp.setVendorValidationApi(true);
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

						JsonObject requestObject = JsonParser
								.parseString(apiResp).getAsJsonObject();

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"inside VendorInitiateGetCallServiceImpl after  calling api response apiResponse is %s",
									apiResp);
						}

						gstinResp.setGstin(gstin);
						// gstinResp.setCustomerCode(gstin.getCustomerCode());

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
												? "Yes"
												: "No")
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

						String rgdtValue = checkForNull(
								requestObject.get("rgdt"));
						LocalDate regDate = rgdtValue != null
								&& !rgdtValue.isEmpty()
								&& !"-".equalsIgnoreCase(rgdtValue)
										? LocalDate.parse(rgdtValue, formatter)
										: null;

						gstinResp.setRegistrationDate(regDate);
						gstinResp.setStateJurisdiction(
								checkForNull(requestObject.get("stj")));
						String cxdtValue = checkForNull(
								requestObject.get("cxdt"));
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
						gstinResp.setVendorValidationApi(true);
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
				LOGGER.debug(
						"saved data into GetGstinMasterDetailApiPushEntity "
						+ "for payloadId : {} ",
						payloadId);

			}

		} catch (Exception ex) {
			LOGGER.error(
					" Error occured while fetching the venndor details {} ",
					ex);
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

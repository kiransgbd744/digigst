package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.asprecon.GetGstinVendorMasterDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDao;
import com.ey.advisory.app.itcmatching.vendorupload.GetGstinVendorMasterDetailEntity;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("VendorInitiateGetCallServiceImpl")
@Slf4j
public class VendorInitiateGetCallServiceImpl
		implements VendorInitiateGetCallService {

	@Autowired
	@Qualifier("taxPayerDetailsDaoImpl")
	TaxPayerDetailsDao taxPayerDao;

	@Autowired
	@Qualifier("GetGstinVendorMasterDetailRepository")
	private GetGstinVendorMasterDetailRepository getGstinVendorMasterDetailRepository;

	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Override
	public void initiateGetCallForSelectedGstin(
			List<String> vendorGstins,Long entityId) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter1 = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDateTime = currentDateTime
				.format(formatter1);
		LocalDateTime parsedDateTime = LocalDateTime
				.parse(formattedDateTime, formatter1);
		List<Object[]> vendorGstinNameList = vendorMasterUploadEntityRepository
				.getVendorGstinAndVendorName(vendorGstins);

		Map<String, String> vendorGstinNameMap = new HashMap<>();

		for (Object[] result : vendorGstinNameList) {
			String vendorGstin = (String) result[0];
			String vendorName = (String) result[1];

			vendorGstinNameMap.put(vendorGstin, vendorName);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside the method initiateGetCallForSelectedGstin. Parsed DateTime: {}",
					parsedDateTime);
		}

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		String groupCode = TenantContext.getTenantId();
		List<GetGstinVendorMasterDetailEntity> gstinVendorMasterDetailsList = new ArrayList<>();
		List<String> vendorList = new ArrayList<>();
		for (String gstin : vendorGstins) {
			GetGstinVendorMasterDetailEntity gstinResp = new GetGstinVendorMasterDetailEntity();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"inside VendorInitiateGetCallServiceImpl before calling api response");

			APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
					groupCode);
			
			// for testing purpose i have commented code
			/*
			 * String hardcodedApiResponse
			 * ="{\"stjCd\":\"TN001\",\"lgnm\":\"EnY TN TaxPayer 1 Ltd\",\"stj\":\"ADYAR\",\"dty\":\"Regular\",\"cxdt\":\"\",\"gstin\":\"33GSPTN0481G1ZA\",\"lstupdt\":\"28/03/2017\",\"rgdt\":\"01/04/2016\",\"ctb\":\"Proprietorship\",\"sts\":\"Active\",\"tradeNam\":\"EnY TN TaxPayer 1 Ltd\",\"ctjCd\":\"0101\",\"ctj\":\"ALLAHABAD-I\",\"einvoiceStatus\":\"No\"}"
			 * ; //String
			 * error="{\"status_cd\":\"0\",\"error\":{\"error_cd\":\"GEN5005\",\"message\":\"Account plan limit exceeded\"}}"
			 * ; APIResponse apiResponse = new APIResponse();
			 * apiResponse.setResponse(hardcodedApiResponse);
			 */

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
					vendorList.add(gstin);
					gstinResp.setVendorGstin(gstin);
					String vendorName = vendorGstinNameMap.get(gstin);
					gstinResp.setVendorNameAsUploaded(vendorName);
					gstinResp.setEntityId(entityId);
					gstinResp.setLastGetCallStatus("Failed");
					gstinResp.setUpdatedBy(userName);
					gstinResp.setErrorCode(respError.getErrorCode());
					gstinResp.setErrorDiscription(respError.getErrorDesc());
					gstinResp.setIsDelete(false);
					//gstinResp.setLastUpdated(parsedDateTime);
					gstinVendorMasterDetailsList.add(gstinResp);
				} catch (Exception e) {

					if (LOGGER.isDebugEnabled()) {
						String msg = String
								.format("exception caught in the !apiResponse.isSuccess() block  "
										+ " Response %s from Gstin  %s :",
										gstin);
						LOGGER.debug(msg);
					}
				}

			} else {
				try {
					String apiResp = apiResponse.getResponse();

					if (LOGGER.isDebugEnabled()) {
						String msg = String
								.format("Following is Tax Payer Detailed "
										+ " Response %s from Gstin  %s :",
										apiResp,
										gstin);
						LOGGER.debug(msg);
					}

					JsonObject requestObject = JsonParser.parseString(apiResp)
							.getAsJsonObject();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"inside VendorInitiateGetCallServiceImpl after  calling api response apiResponse is %s",
								apiResp);
					}
					vendorList.add(gstin);
					gstinResp.setVendorGstin(gstin);
					gstinResp.setEntityId(entityId);
					String vendorName = vendorGstinNameMap.get(gstin);
					gstinResp.setVendorNameAsUploaded(vendorName);
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
							? ("Yes".equalsIgnoreCase(
									checkForNull(
											requestObject
													.get("einvoiceStatus")))
															? "Applicable"
															: "Not Applicable")
							: "Not Applicable";
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
					gstinResp.setIsDelete(false);
					gstinResp.setUpdatedBy(userName);
					gstinVendorMasterDetailsList.add(gstinResp);

				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String
								.format("exception caught in the apiResponse.isSuccess() block  "
										+ " Response %s from Gstin  %s :",
										gstin);
						LOGGER.debug(msg);
					}
				}
			}
		}

		// soft deleting
		getGstinVendorMasterDetailRepository
				.softDeleteVgstinBeforePersist(vendorList,entityId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"inside after softDeleteVgstinBeforePersist. Vendor List details:{}",
					vendorList);

		}

		// saving all the values
		getGstinVendorMasterDetailRepository
				.saveAll(gstinVendorMasterDetailsList);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside after  getGstinVendorMasterDetailRepository inserting into tables. Details:");

			for (GetGstinVendorMasterDetailEntity entity : gstinVendorMasterDetailsList) {
				LOGGER.debug("Vendor GSTIN: {}", entity.getVendorGstin());
			}
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

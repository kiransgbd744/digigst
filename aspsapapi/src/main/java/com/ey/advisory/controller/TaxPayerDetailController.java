package com.ey.advisory.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;
import com.ey.advisory.app.data.repositories.client.GstinValidatorConfigRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDao;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.controller.GstinOtpAuthTokenController.GstnData;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstnapi.services.GenerateAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * This Api will return TaxPayer Details from Gstin Side and also validate if
 * this is Valid GSTIN or not
 */

@Slf4j
@RestController
public class TaxPayerDetailController {

	private static final String INVALID_GSTIN = "GSTIN is not valid";
	private int errorCount = 0;

	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	TaxPayerDetailsService taxPayerService;

	@Autowired
	@Qualifier("GenerateAuthTokenServiceImpl")
	GenerateAuthTokenService authTokenService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gstnAuthService;

	@Autowired
	@Qualifier("taxPayerDetailsDaoImpl")
	TaxPayerDetailsDao taxPayerDao;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private EinvMasterGstinRepository einvMasterGstinRepo;

	@Autowired
	@Qualifier("GstinValidatorConfigRepository")
	GstinValidatorConfigRepository gstinValidRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@PostMapping(value = "/ui/getTaxPayerDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxPayerDet(@RequestBody String reqJson) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupCode = TenantContext.getTenantId();

			JsonObject requestObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("About to execute the TaxPayerDetails api, "
								+ "Request is '%s'", json.toString());
				LOGGER.debug(msg);
			}
			GstnData gstnData = gson.fromJson(json, GstnData.class);
			String gstin = gstnData.getGstin().toUpperCase();
			boolean isEinvoiceApplicable = gstnData.isEinvApplicability();

			if (gstin.length() != 15 || !gstin.matches("[A-Za-z0-9]+")
					|| gstin.matches("[A-Za-z]+") || gstin.matches("[0-9]+")) {
				throw new AppException(INVALID_GSTIN);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Calling Gstin Tax Payer Api for Following gstin : %s",
						gstin);
				LOGGER.debug(msg);
			}

			PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
					PublicApiConstants.GSTIN_UI_SEARCH);

			if (isEinvoiceApplicable) {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.GSTIN_EINV_UI_SEARCH);

			}

			TaxPayerDetailsDto apiResp = taxPayerService
					.getTaxPayerDetails(gstin, groupCode);
            /*
			Map<String, Config> configMap = configManager.getConfigs(
					"EINVAPP",
					"einv.applicable.date");

			String date = configMap.get("einv.applicable.date") == null ? "NA"
					: String.valueOf(
							configMap.get("einv.applicable.date").getValue());

			if (isEinvoiceApplicable) {
				List<String> distinctPan = einvMasterGstinRepo
						.getAllDistinctPan();
				/*
				 * EinvGstinMasterEntity entity = einvMasterGstinRepo
				 * .findByGstin(gstin);
				 */
				/* String pan = gstin.substring(2, 12);
				if (distinctPan.contains(pan))
					apiResp.setEinvApplicable(
							"Applicable as per NIC Master(Last Updated : "
									+ date + ")");
				else
					apiResp.setEinvApplicable(
							"Not Applicable as per NIC Master(Last Updated : "
									+ date + ")");
			}
*/
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(apiResp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}

	@PostMapping(value = "/api/getTaxPayerDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxPayerDetails(
			@RequestBody String reqJson) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupCode = TenantContext.getTenantId();

			JsonObject requestObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("About to execute the TaxPayerDetails api, "
								+ "Request is '%s'", json.toString());
				LOGGER.debug(msg);
			}

			GstnData gstnData = gson.fromJson(json, GstnData.class);
			String gstin = gstnData.getGstin();
			
			if (gstin.length() != 15 || !gstin.matches("[A-Za-z0-9]+")
					|| gstin.matches("[A-Za-z]+") || gstin.matches("[0-9]+")) {
				throw new AppException(INVALID_GSTIN);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Calling Gstin Tax Payer Api for Following gstin : %s",
						gstin);
				LOGGER.debug(msg);
			}

			PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
					PublicApiConstants.GSTIN_API_SEARCH);
			
		
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
				List<APIError> apiRespErrors = apiResponse.getErrors();
				APIError respError = apiRespErrors.get(0);
				throw new AppException(respError.getErrorDesc());
			}

			String apiResp = apiResponse.getResponse();
			JsonObject response = (new JsonParser().parse(apiResp))
					.getAsJsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(response);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}

	@PostMapping(value = "/api/getBulkTaxPayerDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMultiTaxPayerDetails(
			@RequestBody String reqJson) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject();
			JsonArray reqArray = requestObject.get("req").getAsJsonArray();
			errorCount = 0;
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("About to execute the TaxPayerDetails api, "
								+ "Request is '%s'", reqArray.toString());
				LOGGER.debug(msg);
			}
			List<JsonObject> responseList = responseListArray(reqArray, true);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(responseList);
			if (reqArray.size() == errorCount)
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			else
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}

	@PostMapping(value = "/api/getEInvAppTaxPayerDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMultiEInvTaxPayerDetails(
			@RequestBody String reqJson) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		GstinValidatorEntity entity = new GstinValidatorEntity();
		JsonObject resp = new JsonObject();
		try {

			Long requestID = generateCustomId(entityManager);
			entity.setDateOfUpload(LocalDateTime.now());
			entity.setRequestId(requestID);
			entity.setCreatedBy("API");
			entity.setEinvApplicable(true);
			JsonObject requestObject = null;
			JsonArray reqArray = null;
			try {
				requestObject = (new JsonParser()).parse(reqJson)
						.getAsJsonObject();
				reqArray = requestObject.get("req").getAsJsonArray();
			} catch (JsonParseException ex) {
				throw new AppException("Invalid request payload");
			}

			if (reqArray.size() == 0) {
				throw new AppException("GSTIN is mandatory");
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("About to execute the TaxPayerDetails api, "
								+ "Request is '%s'", reqArray.toString());
				LOGGER.debug(msg);
			}

			String groupCode = TenantContext.getTenantId();
			List<JsonObject> responseList = new ArrayList<>();

			/*Map<String, Config> configMap = configManager.getConfigs(
					"EINVAPP",
					"einv.applicable.date");

			String date = configMap.get("einv.applicable.date") == null ? "NA"
					: String.valueOf(
							configMap.get("einv.applicable.date").getValue());
			List<String> distinctPan = einvMasterGstinRepo.getAllDistinctPan();*/

			for (JsonElement inv : reqArray) {
				JsonObject jsonObject = inv.getAsJsonObject();
				if (!jsonObject.has("gstin")) {
					throw new AppException("GSTIN is mandatory");
				}

				String gstn = jsonObject.get("gstin").getAsString();

				if (gstn.isEmpty() || gstn == null) {
					throw new AppException("GSTIN cannot be empty ");
				}
				String gstin = gstn.toUpperCase();
				JsonObject response = new JsonObject();
				String appMsg = null;
				if (gstin.length() != 15 || !gstin.matches("[A-Za-z0-9]+")
						|| gstin.matches("[A-Za-z]+")
						|| gstin.matches("[0-9]+")) {
					response.addProperty("gstin", gstin);
					response.addProperty("msg", INVALID_GSTIN);

				} else {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.GSTIN_EINV_API_BULK_SEARCH);
					APIResponse apiResponse = taxPayerDao
							.findTaxPayerDetails(gstin, groupCode);
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Following is Tax Payer Detailed "
										+ " Response Dto from Gstin  %s :",
								apiResponse.toString());
						LOGGER.debug(msg);
					}
					if (!apiResponse.isSuccess()) {
						List<APIError> apiRespErrors = apiResponse.getErrors();
						APIError respError = apiRespErrors.get(0);

						response.addProperty("gstin", gstin);
						response.addProperty("msg", respError.getErrorDesc());
						appMsg = "Not Applicable";
					} else {
						String apiResp = apiResponse.getResponse();
						response = (new JsonParser().parse(apiResp))
								.getAsJsonObject();
						checkForNbaAndNtr(response);
						appMsg = response.has("einvoiceStatus") ?
									( "Yes".equalsIgnoreCase(checkForNull(response.get("einvoiceStatus"))) 
									? "Applicable"
									:  "Not Applicable") :"Not Applicable" ;
					}
				

					/*String pan = gstin.substring(2, 12);
					if (distinctPan.contains(pan))
						appMsg = "Applicable as per NIC Master(Last Updated : "
								+ date + ")";
					else
						appMsg = "Not Applicable as per NIC Master(Last Updated : "
								+ date + ")";*/
                    response.remove("einvoiceStatus");
					response.addProperty("einvApplicable", appMsg);

				}

				responseList.add(response);
			}
			entity.setNoOfGstins((long) reqArray.size());
			entity.setStatus("COMPLETED");
			gstinValidRepo.save(entity);

			JsonElement respBody = gson.toJsonTree(responseList);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching ReturnFilling Status ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			entity.setStatus("FAILED");
			entity.setNoOfGstins(0L);
			gstinValidRepo.save(entity);
			resp.add("errMsg", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	private List<JsonObject> responseListArray(JsonArray reqArray,
			boolean isEinvoiceApplicable) {

		List<JsonObject> responseList = new ArrayList<>();

		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		for (JsonElement inv : reqArray) {
			String reqObj = inv.toString();
			JsonObject response = new JsonObject();
			GstnData gstnData = gson.fromJson(reqObj, GstnData.class);
			String gstin = gstnData.getGstin().toUpperCase();
			if (gstin.length() != 15 || !gstin.matches("[A-Za-z0-9]+")
					|| gstin.matches("[A-Za-z]+") || gstin.matches("[0-9]+")) {
				errorCount++;
				response.addProperty("gstin", gstin);
				response.addProperty("errMsg", INVALID_GSTIN);

			} else if ("I"
					.equals(gstnAuthService.getAuthTokenStatusForGstin(
							APIConstants.DEFAULT_PUBLIC_API_GSTIN))
					&& !authTokenService.generateAuthToken(
							APIConstants.DEFAULT_PUBLIC_API_GSTIN, null)) {
				errorCount++;

				response.addProperty("gstin", gstin);
				response.addProperty("errMsg",
						"Not able to generate Auth Token");
			} else {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.GSTIN_API_BULK_SEARCH);
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
					List<APIError> apiRespErrors = apiResponse.getErrors();
					APIError respError = apiRespErrors.get(0);
					errorCount++;
					response.addProperty("gstin", gstin);
					response.addProperty("errMsg", respError.getErrorDesc());

				} else {
					String apiResp = apiResponse.getResponse();
					response = (new JsonParser().parse(apiResp))
							.getAsJsonObject();
					checkForNbaAndNtr(response);
				}
				String appMsg = "Not Applicable";
				if (isEinvoiceApplicable) {

					appMsg = response.has("einvoiceStatus") ?
									( "Yes".equalsIgnoreCase(checkForNull(response.get("einvoiceStatus"))) 
									? "Applicable"
									:  "Not Applicable") :"Not Applicable" ;

					response.addProperty("einvApplicable", appMsg);

				}
			}

			responseList.add(response);
		}
		return responseList;

	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT GSTINVALID_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		return ((Long) query.getSingleResult());

	}

	private static void checkForNbaAndNtr(JsonObject response) {
		if (response.has("pradr") && !response.get("pradr").isJsonNull()) {
			JsonObject address = response.get("pradr").getAsJsonObject();
			String ntr = checkForNullNtr(address.get("ntr"));
			address.addProperty("ntr", ntr);
		}

		String nba = checkForNullNBAAndNTR(response.get("nba"));
		response.addProperty("nba", nba);

		if (response.has("adadr") && !response.get("adadr").isJsonNull()) {
			JsonArray address = response.get("adadr").getAsJsonArray();
			  if (address.size() > 0) {
			JsonObject adadrResp = address.get(0).getAsJsonObject();
			String ntr = checkForNullNtr(adadrResp.get("ntr"));
			adadrResp.addProperty("ntr", ntr);
			  }

		}
	}
	

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

	private static String checkForNullNBAAndNTR(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsJsonArray().toString().replace("[", "")
						.replace("]", "").replace("\"", "");
	}
	
	private String checkForNull(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsString();
	}
	private static String checkForNullNtr(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsString();
	}
	public static void main(String[] args) {
		 
		 String apiResp = "{\"stjCd\":\"AR031\",\"dty\":\"Regular\",\"lgnm\":\"JOHN KARA\",\"stj\":\"Naharlagun Zone-I\",\"adadr\":[],\"cxdt\":\"\",\"gstin\":\"12BNNPK9717P1ZA\",\"nba\":[\"Works Contract\",\"Others\"],\"lstupdt\":\"30/09/2019\",\"ctb\":\"Proprietorship\",\"rgdt\":\"26/09/2019\",\"pradr\":{\"addr\":{\"bnm\":\"TIGDO\",\"st\":\"TIGDO ROAD\",\"loc\":\"DOIMUKH\",\"bno\":\"01\",\"dst\":\"Papum Pare\",\"lt\":\"\",\"locality\":\"\",\"pncd\":\"791112\",\"landMark\":\"\",\"stcd\":\"Arunachal Pradesh\",\"geocodelvl\":\"NA\",\"flno\":\"GROUND FLOOR\",\"lg\":\"\"},\"ntr\":\"Works Contract, Others\"},\"ctjCd\":\"UA0102\",\"sts\":\"Active\",\"tradeNam\":\"M/S. T3 ENTERPRISES\",\"ctj\":\"ITANAGAR RANGE\",\"einvoiceStatus\":\"No\"}";

		 JsonObject	response = (new JsonParser().parse(apiResp))
					.getAsJsonObject();
			checkForNbaAndNtr(response);
			
			
			//String appMsg = null;
			String appMsg = response.has("einvoiceStatus") ?
					( "Yes".equalsIgnoreCase(checkForNulln(response.get("einvoiceStatus"))) 
					? "Applicable"
					:  "Not Applicable") :"Not Applicable" ;
		
		System.out.println(appMsg);
		//System.out.println("Hi");
		
	 }
	private static String checkForNulln(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsString();
	}
}

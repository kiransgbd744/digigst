package com.ey.advisory.controller;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
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

import com.ey.advisory.app.data.entities.client.asprecon.GetGstinMasterDetailApiPushEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GstinValidatorPayloadEntity;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.GstinValidatorConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorApiPushRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorPayloadRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDao;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.app.services.gstinvalidator.apipush.GstinValidatorPayloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.functions.EinvJsonSchemaValidatorHelper;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstnapi.services.GenerateAuthTokenService;
import com.ey.advisory.services.days180.api.push.PaymentReference180DaysDocSaveService;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * This Api will return TaxPayer Details from Gstin Side and also validate if
 * this is Valid GSTIN or not
 */

@Slf4j
@RestController
public class GstinValidatorRevIntgApiController {

	@Autowired
	@Qualifier("PaymentReference180DaysDocSaveService")
	private PaymentReference180DaysDocSaveService docService;

	@Autowired
	@Qualifier("GstinValidatorPayloadServiceImpl")
	private GstinValidatorPayloadService payloadService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository erpCompanyCodeMappingRepository;

	@Autowired
	@Qualifier("GstinValidatorPayloadRepository")
	private GstinValidatorPayloadRepository payloadRepository;

	@Autowired
	@Qualifier("GstinValidatorApiPushRepository")
	private GstinValidatorApiPushRepository apiPushRepo;

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

	@PostMapping(value = "/api/gstinValidatorApiPush", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstinValidatorApiRequest(
			ServletRequest request, @RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String companyCode = req.getHeader("companyCode");
		String pushType = req.getHeader("pushType");
		String checkSum = req.getHeader("checkSum");

		return saveGstinValidatorRequest(payloadId, null, companyCode,
				pushType, jsonString, "GstinValidatorSchema.json", checkSum);
	}

	private ResponseEntity<String> saveGstinValidatorRequest(String payloadId,
			String category, String companyCode, String pushType,
			String jsonString, String schemaName, String checkSum) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		ResponseEntity<String> responseEntity = null;
		List<String> revIntjobParamsList = new ArrayList<>();
		JsonObject revIntjobjsonParams = new JsonObject();
		
		String sourceId = null;
		
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);
			
			
			List<Object> sourceIds = erpCompanyCodeMappingRepository
					.findSourceIdByCompanyCode(companyCode);
			if (sourceIds != null && !sourceIds.isEmpty()) {
				sourceId = String.valueOf(sourceIds.get(0));
			}
			
			
			if (!Strings.isNullOrEmpty(checkSum)
					&& !APIConstants.NOCHECKSUM.equalsIgnoreCase(checkSum)
					&& !checkSum.equalsIgnoreCase(genCheckSum)) {
				String msg = "CheckSum is not matching";
				String errorCode = "ER7777";
				LOGGER.error(errorCode, msg);
				throw new AppException(msg, errorCode);
			}
			
			Optional<GstinValidatorPayloadEntity> isRecordPres = payloadRepository
					.findByCloudCheckSumAndStatus(genCheckSum, APIConstants.IP);
			
			
			int gstinCount = 0;

			
			if (isRecordPres.isPresent()) {
				payloadService.create(payloadId, companyCode,
						String.valueOf(gstinCount), pushType,
						genCheckSum, category, APIConstants.DUP, checkSum, jsonString);

				String errMsg = "Record is already in InProgress.";
				LOGGER.error(errMsg);
				
				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "E");
				hdrMsg.addProperty("payloadId", payloadId);
				
				JsonObject respMsg = new JsonObject();
				respMsg.addProperty("msg", "Record is already in InProgress.");

				resp.add("hdr", new Gson().toJsonTree(hdrMsg));
				resp.add("resp", new Gson().toJsonTree(respMsg));

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				return responseEntity;
			}

			payloadService.create(payloadId, companyCode,
					String.valueOf(gstinCount), pushType, genCheckSum,
					category, APIConstants.IP, checkSum, jsonString);

			revIntjobjsonParams.addProperty("payloadId", payloadId);
			revIntjobjsonParams.addProperty("sourceId", sourceId);
			revIntjobjsonParams.addProperty("scenarioName",
					APIConstants.GSTIN_VALIDATOR_PAYLOAD_METADATA_REV_INTG);
			
			revIntjobParamsList.add(revIntjobjsonParams.toString());

			List<ErrorDetailsDto> errorList = jsonSchemaValidatorHelper
					.validateInptJson(jsonString, schemaName);

			if (errorList.isEmpty()) {
				/*
				JsonObject reqObj = JsonParser.parseString(jsonString)
				.getAsJsonObject().get("req").getAsJsonObject();

				
				GstinValidatorReqDtoList reqDto = gson.fromJson(reqObj,
						GstinValidatorReqDtoList.class);

				List<GetGstinMasterDetailApiPushEntity> entityList = reqDto
						.getGstinList().stream()
						.map(o -> convertToEntity(payloadId,
								o.getCustomerCode(), o.getGstin()))
						.collect(Collectors.toList());
				apiPushRepo.saveAll(entityList);
*/
				JsonObject jsonParams = new JsonObject();

				jsonParams.addProperty("payloadId", payloadId);			
				jsonParams.addProperty("sourceId", sourceId);
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.GSTIN_VALIDATOR_API_PUSH,
						jsonParams.toString(), "SYSTEM", JobConstants.PRIORITY,
						JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);

				JsonObject respMsg = new JsonObject();
			
				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "S");
				hdrMsg.addProperty("payloadId", payloadId);
				hdrMsg.addProperty("msg", "Received Successfully");

				resp.add("hdr", gson.toJsonTree(hdrMsg));

				resp.add("resp", gson.toJsonTree(respMsg));

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);

				return responseEntity;
			} else {

				JsonElement respBody = gson.toJsonTree(errorList);

				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "E");
				hdrMsg.addProperty("payloadId", payloadId);
				hdrMsg.addProperty("msg",
						"Error occured while validating json schema");

				resp.add("hdr", new Gson().toJsonTree(hdrMsg));
				resp.add("resp", respBody);

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				String errorCode = "ER7778";
				String msg = respBody.toString();
				if (msg.length() > 500) {
					msg = msg.substring(0, 500);
				}
				payloadService.updateError(payloadId, APIConstants.E, errorCode,
						msg);

				createMetaDataJobs(revIntjobParamsList);

				return responseEntity;
			}
		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode() : "ER8888";
			String errorMsg = "ERP (run time error) -" + ex.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;
			try {
				payloadService.updateErrorExc(payloadId, APIConstants.E,
						errorCode.length() > 500 ? errorCode.substring(0, 500)
								: errorCode,
						errorMsg.length() > 1000 ? errorMsg.substring(0, 1000)
								: errorMsg,
						9);
			} catch (Exception e) {
				LOGGER.error("Exception while connecting server", e);
			}
			LOGGER.error("saveOutwardDoc method exception ", ex);

			JsonObject hdrMsg = new JsonObject();
			hdrMsg.addProperty("status", "E");
			hdrMsg.addProperty("payloadId", payloadId);

			JsonObject respMsg = new JsonObject();
			respMsg.addProperty("msg", errorMsg);

			resp.add("hdr", new Gson().toJsonTree(hdrMsg));
			resp.add("resp", new Gson().toJsonTree(respMsg));

			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);

			createMetaDataJobs(revIntjobParamsList);

			return responseEntity;
		} finally {
			CommonContext.clearContext();
		}
	}

	private GetGstinMasterDetailApiPushEntity convertToEntity(String payloadId,
			String customerCode, String gstin) {
		GetGstinMasterDetailApiPushEntity entity = new GetGstinMasterDetailApiPushEntity();
		entity.setPayloadId(payloadId);
		entity.setCustomerCode(customerCode);
		entity.setGstin(gstin);
		entity.setCreatedOn(LocalDateTime.now());
		return entity;
	}

	private void createMetaDataJobs(List<String> metaDataRevIntjobjsonParams) {
		if (metaDataRevIntjobjsonParams == null
				|| metaDataRevIntjobjsonParams.isEmpty())
			return;

		String jobCategory = APIConstants.GSTIN_VALIDATOR_PAYLOAD_METADATA_REV_INTG;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		for (String jobParams : metaDataRevIntjobjsonParams) {
			asyncJobsService.createJob(TenantContext.getTenantId(), jobCategory,
					jobParams, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

	private static List<Object[]> convertJsonArrayToList(JsonArray jsonArray) {
		List<Object[]> resultList = new ArrayList<>();

		for (JsonElement jsonElement : jsonArray) {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				Object[] file = jsonObject.entrySet().stream()
						.map(entry -> entry.getValue()).toArray();
				resultList.add(file);
			}
		}

		return resultList;
	}
	/*
	public static void main(String[] args) {
		String genCheckSum = "i2e+FBTfF/+HYs4BpPtVX/039b8=";
		
		byte[] decode = Base64.getDecoder().decode(genCheckSum);
		
		String originalString = new String(decode, StandardCharsets.UTF_8);
		
		System.out.println(originalString);

	}
*/
	
	public static void main(String[] args) {
		  MessageDigest sha256;
          try {
              sha256 = MessageDigest.getInstance("SHA-256");

              File file = new File("C://Users//AZ956YX//OneDrive - EY//Desktop//Einvoice.xml");
              MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(Files.readAllBytes(file.toPath()));
				byte[] digest = md.digest();
				String fileHash = DatatypeConverter.printHexBinary(digest)
						.toUpperCase();
              System.out.println(fileHash);
          }catch(Exception ex)
          {
        	  }
          }
	
	public static void printByteArray(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.printf("%02X", array[i]);
            if ((i % 4) == 3) System.out.print(" ");
        }
        System.out.println();
    }
}

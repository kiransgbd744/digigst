package com.ey.advisory.app.common;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.PaymentReferencePayloadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.itc.reversal.api.dto.ITCReversal180DayApiResponseDto;
import com.ey.advisory.services.days180.api.push.PaymentReference180DaysDocSaveService;
import com.ey.advisory.services.days180.api.push.PaymentReferencePayloadEntity;
import com.ey.advisory.services.days180.api.push.PaymentReferencePayloadService;
import com.ey.advisory.services.days180.api.push.PaymentReferenceReqDto;
import com.ey.advisory.services.days180.api.push.PaymentRefernce180DaysRespDto;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@Component("ITCReversal180DayApiReqRespLogHelper")
@Slf4j
public class ITCReversal180DayApiReqRespLogHelper {

	
	@Autowired
	@Qualifier("PaymentReference180DaysDocSaveService")
	private PaymentReference180DaysDocSaveService docService;

	@Autowired
	@Qualifier("PaymentReferencePayloadServiceImpl")
	private PaymentReferencePayloadService payloadService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository erpCompanyCodeMappingRepository;

	@Autowired
	@Qualifier("PaymentReferencePayloadRepository")
	private PaymentReferencePayloadRepository payloadRepository;
	
	public ITCReversal180DayApiResponseDto processRequest(String payloadId,
			String category, String checksum, String docCount, String pushType,
			String companyCode, String headerSourceId, String jsonString) {
		
		ITCReversal180DayApiResponseDto response = new ITCReversal180DayApiResponseDto();
				
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> revIntjobParamsList = new ArrayList<>();
		JsonObject revIntjobjsonParams = new JsonObject();

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);

			if (!Strings.isNullOrEmpty(checksum)
					&& !APIConstants.NOCHECKSUM.equalsIgnoreCase(checksum)
					&& !checksum.equalsIgnoreCase(genCheckSum)) {
				String msg = "CheckSum is not matching";
				String errorCode = "ER7777";
				LOGGER.error(errorCode, msg);
				throw new AppException(msg, errorCode);
			}

			List<Object> sourceIds = erpCompanyCodeMappingRepository
					.findSourceIdByCompanyCode(companyCode);
			String sourceId = null;
			if (!sourceIds.isEmpty()) {
				sourceId = headerSourceId != null ? headerSourceId
						: String.valueOf(sourceIds.get(0));
			}
			Optional<PaymentReferencePayloadEntity> isRecordPres = payloadRepository
					.findByCloudCheckSumAndStatus(genCheckSum, APIConstants.IP);
			if (isRecordPres.isPresent()) {
				payloadService.create(payloadId, companyCode, docCount,
						checksum, pushType, genCheckSum, sourceId,
						APIConstants.DUP,jsonString);
				
				String errMsg = "Record is already in InProgress.";
				LOGGER.error(errMsg);
				response = new ITCReversal180DayApiResponseDto();
				response.setPayloadId(payloadId);
				response.setStatus("E");
				response.setMsg(errMsg);
				return response;
			}
			revIntjobjsonParams.addProperty("payloadId", payloadId);
			revIntjobjsonParams.addProperty("sourceId", sourceId);
			revIntjobjsonParams.addProperty("scenarioName",
					APIConstants.REVERSAL180DAYS_PAYLOAD_METADATA_REV_INTG);
			revIntjobParamsList.add(revIntjobjsonParams.toString());

			payloadService.create(payloadId, companyCode, docCount, checksum,
					pushType, genCheckSum, sourceId, APIConstants.IP,jsonString);
			
		
				
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
	
			JsonArray json = requestObject.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", json);
			}
	
			Type listType = new TypeToken<List<PaymentReferenceReqDto>>() {
			}.getType();
	
			List<PaymentReferenceReqDto> documents = gson.fromJson(json,
					listType);
	
			List<Object[]> fileList = convertDTOList(documents);
	
			PaymentRefernce180DaysRespDto finalResp = docService
					.saveDocs(fileList, payloadId);
	
			createMetaDataJobs(revIntjobParamsList);
	
			Integer errorCount = finalResp.getErrorCount();
			Integer totalCount = finalResp.getTotalCount();
			
			
			if (errorCount > 0) {
				payloadService.update(payloadId, APIConstants.PE,
						errorCount, totalCount);
			} else {
				payloadService.update(payloadId, APIConstants.P, errorCount,
						totalCount);
			}
			response = new ITCReversal180DayApiResponseDto();
			response.setPayloadId(payloadId);
			response.setStatus("S");
			response.setMsg("Received Successfully");
			return response;
			
		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode()
					: "ER8888";
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
			
			response = new ITCReversal180DayApiResponseDto();
			response.setPayloadId(payloadId);
			response.setStatus("E");
			response.setMsg(errorMsg);

			createMetaDataJobs(revIntjobParamsList);

			return response;
		} finally {
			CommonContext.clearContext();
		}
	}

	private void createMetaDataJobs(List<String> metaDataRevIntjobjsonParams) {
		if (metaDataRevIntjobjsonParams == null
				|| metaDataRevIntjobjsonParams.isEmpty())
			return;

		String jobCategory = APIConstants.REVERSAL180DAYS_PAYLOAD_METADATA_REV_INTG;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		for (String jobParams : metaDataRevIntjobjsonParams) {
			asyncJobsService.createJob(TenantContext.getTenantId(), jobCategory,
					jobParams, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

	private static List<Object[]> convertDTOList(
			List<PaymentReferenceReqDto> dtoList) {

		List<Object[]> resultList = new ArrayList<>();

		for (PaymentReferenceReqDto dto : dtoList) {
			Object[] objectArray = new Object[] { dto.getActionType(),
					dto.getCustomerGSTIN(), dto.getSupplierGSTIN(),
					dto.getSupplierName(), dto.getSupplierCode(),
					dto.getDocumentType(), dto.getDocumentNumber(),
					dto.getDocumentDate(), dto.getInvoiceValue(),
					dto.getStatutoryDeductionsApplicable(),
					dto.getStatutoryDeductionAmount(),
					dto.getAnyOtherDeductionAmount(),
					dto.getRemarksforDeductions(), dto.getDueDateofPayment(),
					dto.getPaymentReferenceNumber(),
					dto.getPaymentReferenceDate(), dto.getPaymentDescription(),
					dto.getPaymentStatus(), dto.getPaidAmounttoSupplier(),
					dto.getCurrencyCode(), dto.getExchangeRate(),
					dto.getUnpaidAmounttoSupplier(), dto.getPostingDate(),
					dto.getPlantCode(), dto.getProfitCentre(),
					dto.getDivision(), dto.getUserDefinedField1(),
					dto.getUserDefinedField2(), dto.getUserDefinedField3() };

			resultList.add(objectArray);
		}

		return resultList;
	}

}

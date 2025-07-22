package com.ey.advisory.app.services.jobs.erp;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.asprecon.GstinValidatorPayloadEntity;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorPayloadRepository;
import com.ey.advisory.app.services.gstinvalidator.apipush.GstinValidatorPayloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.soap.integration.api.dto.GstinValidatorSoapApiResponseDto;
import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@Component("GstinValidationApiReqRespLogHelper")
@Slf4j
public class GstinValidationApiReqRespLogHelper {

	@Autowired
	@Qualifier("GstinValidatorPayloadServiceImpl")
	private GstinValidatorPayloadService payloadService;

	@Autowired
	@Qualifier("GstinValidatorPayloadRepository")
	private GstinValidatorPayloadRepository payloadRepository;

	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository erpCompanyCodeMappingRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	public GstinValidatorSoapApiResponseDto processRequest(String payloadId,
			String category, String companyCode, String pushType,
			String jsonString, String checkSum) {

		GstinValidatorSoapApiResponseDto response = new GstinValidatorSoapApiResponseDto();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";
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
						String.valueOf(gstinCount), pushType, genCheckSum,
						category, APIConstants.DUP, checkSum, jsonString);

				String errMsg = "Record is already in InProgress.";
				LOGGER.error(errMsg);
				response = new GstinValidatorSoapApiResponseDto();
				response.setPayloadId(payloadId);
				response.setStatus("E");
				response.setMsg(errMsg);
				return response;
			}

			payloadService.create(payloadId, companyCode,
					String.valueOf(gstinCount), pushType, genCheckSum, category,
					APIConstants.IP, checkSum, jsonString);

			JsonObject revIntjobjsonParams = new JsonObject();

			/*
			 * revIntjobjsonParams.addProperty("payloadId", payloadId);
			 * revIntjobjsonParams.addProperty("sourceId", sourceId);
			 * revIntjobjsonParams.addProperty("scenarioName",
			 * APIConstants.GSTIN_VALIDATOR_PAYLOAD_METADATA_REV_INTG);
			 */

			JsonObject jsonParams = new JsonObject();

			jsonParams.addProperty("payloadId", payloadId);
			jsonParams.addProperty("sourceId", sourceId);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTIN_VALIDATOR_API_PUSH,
					jsonParams.toString(), "SYSTEM", JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			response = new GstinValidatorSoapApiResponseDto();
			response.setPayloadId(payloadId);
			response.setStatus("S");
			response.setMsg("Received Successfully");
			return response;
		} catch (Exception ex) {
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
			LOGGER.error("saveVendorMasterDoc method exception ", ex);

			response = new GstinValidatorSoapApiResponseDto();
			response.setPayloadId(payloadId);
			response.setStatus("E");
			response.setMsg(errorMsg);
			return response;
		} finally {
			CommonContext.clearContext();
		}
	}

}

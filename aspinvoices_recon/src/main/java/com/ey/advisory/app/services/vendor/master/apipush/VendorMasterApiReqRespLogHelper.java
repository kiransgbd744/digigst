package com.ey.advisory.app.services.vendor.master.apipush;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiPayloadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.vendor.master.api.dto.VendorMasterApiResponseDto;
import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@Component("VendorMasterApiReqRespLogHelper")
@Slf4j
public class VendorMasterApiReqRespLogHelper {

	@Autowired
	@Qualifier("VendorMasterApiPayloadServiceImpl")
	private VendorMasterApiPayloadService payloadService;

	@Autowired
	@Qualifier("VendorMasterApiPayloadRepository")
	private VendorMasterApiPayloadRepository payloadRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	public VendorMasterApiResponseDto processRequest(String payloadId,
			String category, String pushType, String jsonString,
			String checkSum,String sourceId) {

		VendorMasterApiResponseDto response = new VendorMasterApiResponseDto();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";
		
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);

			if (!Strings.isNullOrEmpty(checkSum)
					&& !APIConstants.NOCHECKSUM.equalsIgnoreCase(checkSum)
					&& !checkSum.equalsIgnoreCase(genCheckSum)) {
				String msg = "CheckSum is not matching";
				String errorCode = "ER7777";
				LOGGER.error(errorCode, msg);
				throw new AppException(msg, errorCode);
			}

			Optional<VendorMasterApiPayloadEntity> isRecordPres = payloadRepository
					.findByCloudCheckSumAndStatus(genCheckSum, APIConstants.IP);

			int gstinCount = 0;

			if (isRecordPres.isPresent()) {
				payloadService.create(payloadId, String.valueOf(gstinCount),
						pushType, genCheckSum, category, APIConstants.DUP,
						checkSum, jsonString);

				String errMsg = "Record is already in InProgress.";
				LOGGER.error(errMsg);
				response = new VendorMasterApiResponseDto();
				response.setPayloadId(payloadId);
				response.setStatus("E");
				response.setMsg(errMsg);
				return response;
			}

			payloadService.create(payloadId, String.valueOf(gstinCount),
					pushType, genCheckSum, category, APIConstants.IP, checkSum,
					jsonString);

			JsonObject jsonParams = new JsonObject();

			jsonParams.addProperty("payloadId", payloadId);
			jsonParams.addProperty("sourceId", sourceId);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.VENDOR_MASTER_API_PUSH, jsonParams.toString(),
					userName, JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			response = new VendorMasterApiResponseDto();
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

			response = new VendorMasterApiResponseDto();
			response.setPayloadId(payloadId);
			response.setStatus("E");
			response.setMsg(errorMsg);
			return response;
		} finally {
			CommonContext.clearContext();
		}
	}

}

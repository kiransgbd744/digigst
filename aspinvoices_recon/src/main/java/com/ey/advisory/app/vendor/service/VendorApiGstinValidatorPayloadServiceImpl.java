package com.ey.advisory.app.vendor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Service("VendorApiGstinValidatorPayloadServiceImpl")
@Slf4j
public class VendorApiGstinValidatorPayloadServiceImpl
		implements VendorApiGstinValidatorPayloadService {

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepository;

	@Override
	public void create(String payloadId, String companyCode, String gstinCount,
			String pushType, String genCheckSum, String category,
			String apiStatus, String checkSum, String jsonString) {
		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the recording of "
					+ "the payload in the master ";
			LOGGER.warn(msg);
			return;
		}

		LocalDateTime convertNow = LocalDateTime.now();
		User user = SecurityContext.getUser();

		VendorValidatorPayloadEntity payload = new VendorValidatorPayloadEntity();
		payload.setPayloadId(payloadId);
		payload.setCompanyCode(companyCode);
		if (gstinCount != null && !gstinCount.isEmpty()) {
			payload.setGstinCount(Integer.valueOf(gstinCount));
		}
		payload.setCloudCheckSum(genCheckSum);

		if (pushType != null && !pushType.isEmpty()) {
			payload.setPushType(Integer.valueOf(pushType));
		}
		payload.setCategory(category);
		payload.setCheckSum(checkSum);
		payload.setStatus(apiStatus);
		payload.setCreatedBy(user.getUserPrincipalName());
		payload.setCreatedOn(convertNow);
		payload.setReqPlayload(GenUtil.convertStringToClob(jsonString));

		payloadRepository.save(payload);
	}

	@Override
	public void updateError(String payloadId, String status, String errorCode,
			String jsonErrorResp) {
		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the update of "
					+ "the payload in the master (failure scenario)";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime modifiedOn = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		payloadRepository.updateCpiStatus(payloadId, status, modifiedOn,
				errorCode, jsonErrorResp, 0, 0);
	}

	@Override
	public void saveCPIError(String payloadId, String companyCode,
			String docCount, String checkSum, String pushType, String errorCode,
			String jsonErrorResp) {

		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the recording of "
					+ "the payload in the master (from CPI)";
			LOGGER.warn(msg);
			return;
		}
		String payloadID = null;
		List<Object> payloadIDS = payloadRepository.findBypayloadId(payloadId);
		if (payloadIDS != null && !payloadIDS.isEmpty()) {
			payloadID = (String) payloadIDS.get(0);
		}
		if (payloadID == null || payloadID.isEmpty()) {
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			User user = SecurityContext.getUser();

			VendorValidatorPayloadEntity outPayload = new VendorValidatorPayloadEntity();
			outPayload.setPayloadId(payloadId);
			outPayload.setCompanyCode(companyCode);
			if (docCount != null && !docCount.isEmpty()) {
				outPayload.setGstinCount(Integer.valueOf(docCount));
			}
			// outPayload.setCheckSum(checkSum);
			if (pushType != null && !pushType.isEmpty()) {
				outPayload.setPushType(Integer.valueOf(pushType));
			}
			outPayload.setErrorCode(errorCode);
			outPayload.setJsonErrorResponse(jsonErrorResp);
			outPayload.setStatus(APIConstants.E_CPI);
			outPayload.setCreatedBy(user.getUserPrincipalName());
			outPayload.setCreatedOn(convertNow);
			payloadRepository.save(outPayload);
		} else {
			LocalDateTime modifiedOn = LocalDateTime.now();
			payloadRepository.updateCpiStatus(payloadId, APIConstants.E_CPI,
					modifiedOn, errorCode, jsonErrorResp, 0, 0);
		}
	}

	@Override
	public void update(String payloadId, String status, Integer errorCount,
			Integer totalCount) {

		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the updating of "
					+ "the payload in the master (success scenario)";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime modifiedOn = LocalDateTime.now();
		payloadRepository.updateStatus(payloadId, status, modifiedOn,
				errorCount, totalCount);
	}

	@Override
	public void updateErrorExc(String payloadId, String status,
			String errorCode, String jsonErrorResp, Integer pushType) {
		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the update of "
					+ "the payload in the master (failure scenario)";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime modifiedOn = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		payloadRepository.updateCpiStatus(payloadId, status, modifiedOn,
				errorCode, jsonErrorResp, 0, 0, pushType);
	}
}

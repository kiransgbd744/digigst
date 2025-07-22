/**
 * 
 */
package com.ey.advisory.app.data.services.common;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.InwardPayloadEntity;
import com.ey.advisory.app.data.repositories.client.InwardPayloadRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("InwardPayloadServiceImpl")
@Slf4j
public class InwardPayloadServiceImpl implements InwardPayloadService {

	@Autowired
	@Qualifier("InwardPayloadRepository")
	private InwardPayloadRepository inwardPayloadRepository;

	@Override
	public void create(String payloadId, String companyCode, String docCount,
			String checkSum, String pushType, String genChekSum,
			String sourceId, String status) {

		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the recording of "
					+ "the payload in the master ";
			LOGGER.warn(msg);
			return;
		}

		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		User user = SecurityContext.getUser();

		InwardPayloadEntity inPayload = new InwardPayloadEntity();
		inPayload.setPayloadId(payloadId);
		inPayload.setCompanyCode(companyCode);
		inPayload.setDerivedSourceId(sourceId);
		if (docCount != null && !docCount.isEmpty()) {
			inPayload.setDocCount(Integer.valueOf(docCount));
		}
		inPayload.setCheckSum(checkSum);
		inPayload.setCloudCheckSum(genChekSum);
		if (pushType != null && !pushType.isEmpty()) {
			inPayload.setPushType(Integer.valueOf(pushType));
		}
		inPayload.setStatus(status);
		inPayload.setCreatedBy(user.getUserPrincipalName());
		inPayload.setCreatedOn(convertNow);
		inwardPayloadRepository.save(inPayload);
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
		LocalDateTime modifiedOn = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		inwardPayloadRepository.updateStatus(payloadId, status, modifiedOn,
				errorCount, totalCount);
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
		inwardPayloadRepository.updateCpiStatus(payloadId, status, modifiedOn,
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
		List<Object> payloadIDS = inwardPayloadRepository
				.findBypayloadId(payloadId);
		if (payloadIDS != null && !payloadIDS.isEmpty()) {
			payloadID = (String) payloadIDS.get(0);
		}
		if (payloadID == null || payloadID.isEmpty()) {
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			User user = SecurityContext.getUser();

			InwardPayloadEntity inPayload = new InwardPayloadEntity();
			inPayload.setPayloadId(payloadId);
			inPayload.setCompanyCode(companyCode);
			if (docCount != null && !docCount.isEmpty()) {
				inPayload.setDocCount(Integer.valueOf(docCount));
			}
			inPayload.setCheckSum(checkSum);
			if (pushType != null && !pushType.isEmpty()) {
				inPayload.setPushType(Integer.valueOf(pushType));
			}
			inPayload.setErrorCode(errorCode);
			inPayload.setJsonErrorResponse(jsonErrorResp);
			inPayload.setStatus(APIConstants.E_CPI);
			inPayload.setCreatedBy(user.getUserPrincipalName());
			inPayload.setCreatedOn(convertNow);
			inwardPayloadRepository.save(inPayload);
		} else {
			LocalDateTime modifiedOn = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			inwardPayloadRepository.updateCpiStatus(payloadId,
					APIConstants.E_CPI, modifiedOn, errorCode, jsonErrorResp, 0,
					0);
		}
	}
}

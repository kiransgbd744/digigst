/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Itc04PayloadEntity;
import com.ey.advisory.app.data.repositories.client.Itc04PayloadRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Itc04PayloadServiceImpl")
@Slf4j
public class Itc04PayloadServiceImpl implements Itc04PayloadService {

	@Autowired
	@Qualifier("Itc04PayloadRepository")
	private Itc04PayloadRepository itc04PayloadRepository;

	@Override
	public void create(String payloadId, String companyCode, String docCount,
			String checkSum, String pushType, String sourceId, String genCheckSum) {
		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the recording of "
					+ "the payload in the master ";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		User user = SecurityContext.getUser();

		Itc04PayloadEntity itc04Payload = new Itc04PayloadEntity(); 
		itc04Payload.setPayloadId(payloadId);
		itc04Payload.setCompanyCode(companyCode); 
		if (docCount != null) {
			itc04Payload.setDocCount(Integer.valueOf(docCount));
		}
		itc04Payload.setCheckSum(checkSum);
		itc04Payload.setCloudCheckSum(genCheckSum);
		if (pushType != null) {
			itc04Payload.setPushType(Integer.valueOf(pushType));
		}
		itc04Payload.setSourceId(sourceId);
		itc04Payload.setStatus(APIConstants.IP);
		itc04Payload.setCreatedBy(user.getUserPrincipalName());
		itc04Payload.setCreatedOn(convertNow);
		itc04PayloadRepository.save(itc04Payload);
	}

	@Override
	public void updateError(String payloadId, String status, String errorCode,
			String sourceId, String jsonErrorResp) {
		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the update of "
					+ "the payload in the master (failure scenario)";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime modifiedOn = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		itc04PayloadRepository.updateCpiStatus(payloadId, sourceId, status,
				modifiedOn, errorCode, jsonErrorResp, 0, 0);
	}

	@Override
	public void saveCPIError(String payloadId, String companyCode,
			String docCount, String checkSum, String pushType, String errorCode,
			String sourceId, String jsonErrorResp) {

		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the recording of "
					+ "the payload in the master (from CPI)";
			LOGGER.warn(msg);
			return;
		}
		String payloadID = null;
		List<Object> payloadIDS = itc04PayloadRepository
				.findBypayloadIdAndSourceId(payloadId, sourceId);
		if (payloadIDS != null && !payloadIDS.isEmpty()) {
			payloadID = (String) payloadIDS.get(0);
		}
		if (payloadID == null || payloadID.isEmpty()) {
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			User user = SecurityContext.getUser();

			Itc04PayloadEntity itc04Payload = new Itc04PayloadEntity(); 
			itc04Payload.setPayloadId(payloadId);
			itc04Payload.setSourceId(sourceId);
			itc04Payload.setCompanyCode(companyCode);
			if (docCount != null) {
				itc04Payload.setDocCount(Integer.valueOf(docCount));
			}
			itc04Payload.setCheckSum(checkSum);
			if (pushType != null) {
				itc04Payload.setPushType(Integer.valueOf(pushType));
			}
			itc04Payload.setErrorCode(errorCode);
			itc04Payload.setJsonErrorResponse(jsonErrorResp);
			itc04Payload.setStatus(APIConstants.E_CPI);
			itc04Payload.setCreatedBy(user.getUserPrincipalName());
			itc04Payload.setCreatedOn(convertNow);
			itc04PayloadRepository.save(itc04Payload);
		} else {
			LocalDateTime modifiedOn = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			itc04PayloadRepository.updateCpiStatus(payloadId, sourceId,
					APIConstants.E_CPI, modifiedOn, errorCode, jsonErrorResp, 0,
					0);
		}
	}

	@Override
	public void update(String payloadId, String status, Integer errorCount,
			String sourceId, Integer totalCount) {

		if (Strings.isNullOrEmpty(payloadId)) {
			String msg = "No payloadId found, skipping the updating of "
					+ "the payload in the master (success scenario)";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime modifiedOn = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		itc04PayloadRepository.updateStatus(payloadId, sourceId, status,
				modifiedOn, errorCount, totalCount);

	}

}

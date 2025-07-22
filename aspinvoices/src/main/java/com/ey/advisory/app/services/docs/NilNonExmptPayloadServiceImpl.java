/**
 * 
 */
package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.NilNonExmptPayloadEntity;
import com.ey.advisory.app.data.repositories.client.NilNonExmptPayloadRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("NilNonExmptPayloadServiceImpl")
@Slf4j
public class NilNonExmptPayloadServiceImpl
		implements NilNonExmptPayloadService {

	@Autowired
	@Qualifier("NilNonExmptPayloadRepository")
	private NilNonExmptPayloadRepository nilNonExmptPayloadRepository;

	@Override
	public void persistPayload(String cntrlPayloadId, String cntrlChecksum,
			String cntrlCount, String companyCode, String sgstin,
			String reurnPeriod) {
		if (Strings.isNullOrEmpty(cntrlPayloadId)) {
			String msg = "No cntrlPayloadId found, skipping the recording of "
					+ "the payload in the master ";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		User user = SecurityContext.getUser();

		NilNonExmptPayloadEntity nilNonPayload = new NilNonExmptPayloadEntity();

		nilNonPayload.setControlSgstin(sgstin);
		nilNonPayload.setControlRetPeriod(reurnPeriod);
		nilNonPayload.setControlPayloadID(cntrlPayloadId);
		nilNonPayload.setCompanyCode(companyCode);
		if (cntrlCount != null) {
			nilNonPayload.setControlCount(Integer.valueOf(cntrlCount));
		}
		nilNonPayload.setControlCheckSum(cntrlChecksum);
		nilNonPayload.setStatus(APIConstants.IP);
		nilNonPayload.setCreatedBy(user.getUserPrincipalName());
		nilNonPayload.setCreatedOn(convertNow);
		nilNonExmptPayloadRepository.save(nilNonPayload);
	}

	@Override
	public void updateError(String cntrlPayloadId, String status,
			String errorCode, String jsonErrorResp) {
		if (Strings.isNullOrEmpty(cntrlPayloadId)) {
			String msg = "No cntrlPayloadId found, skipping the update of "
					+ "the payload in the master (failure scenario)";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime modifiedOn = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		nilNonExmptPayloadRepository.updateErrorStatus(cntrlPayloadId, status,
				modifiedOn, errorCode, jsonErrorResp);
	}

	@Override
	public void update(String cntrlPayloadId, String status) {

		if (Strings.isNullOrEmpty(cntrlPayloadId)) {
			String msg = "No cntrlPayloadId found, skipping the updating of "
					+ "the payload in the master (success scenario)";
			LOGGER.warn(msg);
			return;
		}
		LocalDateTime modifiedOn = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		nilNonExmptPayloadRepository.updateStatus(cntrlPayloadId, status,
				modifiedOn);

	}
}
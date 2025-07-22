package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Gstr7PayloadEntity;
import com.ey.advisory.app.data.repositories.client.Gstr7PayloadRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Gstr7PayloadServiceImpl")
@Slf4j
public class Gstr7PayloadServiceImpl implements Gstr7PayloadService {

	@Autowired
	@Qualifier("Gstr7PayloadRepository")
	private Gstr7PayloadRepository gstr7PayloadRepository;

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

		Gstr7PayloadEntity nilNonPayload = new Gstr7PayloadEntity();

		nilNonPayload.setControlSgstin(sgstin);
		nilNonPayload.setControlRetPeriod(reurnPeriod);
		nilNonPayload.setControlPayloadID(cntrlPayloadId);
		nilNonPayload.setCompanyCode(companyCode);
		nilNonPayload.setControlCount(Integer.valueOf(cntrlCount));
		nilNonPayload.setControlCheckSum(cntrlChecksum);
		nilNonPayload.setStatus(APIConstants.IP);
		nilNonPayload.setCreatedBy(user.getUserPrincipalName());
		nilNonPayload.setCreatedOn(convertNow);
		gstr7PayloadRepository.save(nilNonPayload);
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
		gstr7PayloadRepository.updateErrorStatus(cntrlPayloadId, status,
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
		gstr7PayloadRepository.updateStatus(cntrlPayloadId, status,
				modifiedOn);

	}
}
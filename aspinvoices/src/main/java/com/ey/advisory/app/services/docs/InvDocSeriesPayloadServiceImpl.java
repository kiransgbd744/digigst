/**
 * 
 */
package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.InvDocSeriesPayloadEntity;
import com.ey.advisory.app.data.repositories.client.InvDocSeriesPayloadRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("InvDocSeriesPayloadServiceImpl")
@Slf4j
public class InvDocSeriesPayloadServiceImpl
		implements InvDocSeriesPayloadService {

	@Autowired
	@Qualifier("InvDocSeriesPayloadRepository")
	private InvDocSeriesPayloadRepository invDocSeriesPayloadRepository;

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

		InvDocSeriesPayloadEntity invDocPayload = new InvDocSeriesPayloadEntity();

		invDocPayload.setControlSgstin(sgstin);
		invDocPayload.setControlRetPeriod(reurnPeriod);
		invDocPayload.setControlPayloadID(cntrlPayloadId);
		invDocPayload.setCompanyCode(companyCode);
		if (cntrlCount != null) {
			invDocPayload.setControlCount(Integer.valueOf(cntrlCount));
		}
		invDocPayload.setControlCheckSum(cntrlChecksum);
		invDocPayload.setStatus(APIConstants.IP);
		invDocPayload.setCreatedBy(user.getUserPrincipalName());
		invDocPayload.setCreatedOn(convertNow);
		invDocSeriesPayloadRepository.save(invDocPayload);
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
		invDocSeriesPayloadRepository.updateErrorStatus(cntrlPayloadId, status,
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
		invDocSeriesPayloadRepository.updateStatus(cntrlPayloadId, status,
				modifiedOn);

	}
}
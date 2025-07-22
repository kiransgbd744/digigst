package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Gstr3BPayloadEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3BPayloadRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Gstr3BPayloadServiceImpl")
@Slf4j
public class Gstr3BPayloadServiceImpl implements Gstr3BPayloadService {

	@Autowired
	@Qualifier("Gstr3BPayloadRepository")
	private Gstr3BPayloadRepository gstr3BPayloadRepository;

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
		Gstr3BPayloadEntity gstr3b = new Gstr3BPayloadEntity(); 

		gstr3b.setControlSgstin(sgstin);
		gstr3b.setControlRetPeriod(reurnPeriod);
		gstr3b.setControlPayloadID(cntrlPayloadId);
		gstr3b.setCompanyCode(companyCode);
		gstr3b.setControlCount(Integer.valueOf(cntrlCount));
		gstr3b.setControlCheckSum(cntrlChecksum);
		gstr3b.setStatus(APIConstants.IP);
		gstr3b.setCreatedBy(user.getUserPrincipalName());
		gstr3b.setCreatedOn(convertNow);
		gstr3BPayloadRepository.save(gstr3b);
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
		gstr3BPayloadRepository.updateErrorStatus(cntrlPayloadId, status,
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
		gstr3BPayloadRepository.updateStatus(cntrlPayloadId, status,
				modifiedOn);

	}
}
/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1GetOverallStatusHandler")
public class Gstr1GetOverallStatusHandler {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	private GstnUserRequestRepository gstnUserRepo;

	// @Transactional(value = "clientTransactionManager")
	public void execute(String groupCode) {

		try {
			TenantContext.setTenantId(groupCode);

			List<Object[]> objs = gstnUserRepo.findNewUserRequestIds(
					APIConstants.GET, APIConstants.GSTR1.toUpperCase());

			for (Object[] obj : objs) {

				Long userRequestId = obj[0] != null
						? Long.parseLong(String.valueOf(obj[0])) : null;
				String gstin = obj[1] != null ? String.valueOf(obj[1]) : null;
				String retPeriod = obj[2] != null ? String.valueOf(obj[2])
						: null;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"GSTR1 GET sections Overall status execution "
									+ "started for userRequestId %s, gstin %s, "
									+ "retperiod %s",
							userRequestId, gstin, retPeriod));
				}

				if (userRequestId != null) {

					if (isAllSectionsGetCallsSuccess(userRequestId, gstin,
							retPeriod)) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format(
									"GSTR1 GET sections Overall status  "
											+ "for userRequestId %s, gstin %s, "
											+ "retperiod %s is Success",
									userRequestId, gstin, retPeriod));
						}
						gstnUserRepo.updateRequestStatus(userRequestId, 1);
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format(
									"GSTR1 GET sections Overall status "
											+ "for userRequestId %s, gstin %s, "
											+ "retperiod %s is not Success",
									userRequestId, gstin, retPeriod));
						}
						gstnUserRepo.updateRequestStatus(userRequestId, 0);
					}

				}

			}
		} catch (Exception e) {
			LOGGER.error("Unexpected Error", e);
			throw new AppException(e.getMessage(), e);
		}
	}

	private Boolean isAllSectionsGetCallsSuccess(Long userRequestId,
			String gstin, String retPeriod) {

		List<String> statusList = batchRepo
				.findStatusByUserRequestIdAndIsDeleteFalse(gstin, retPeriod,
						userRequestId);

		if (statusList == null || statusList.isEmpty()) {
			return false;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"%d GSTR1 GET sections are availble for userRequestId %d ",
					statusList.size(), userRequestId));
		}
		// All sections(19) GET call batch entry must present
		if (statusList.size() < 19) {
			return false;
		}
		List<String> gstnStatus = new ArrayList<>();
		gstnStatus.add(APIConstants.SUCCESS);
		gstnStatus.add(APIConstants.SUCCESS_WITH_NO_DATA);

		statusList.removeAll(gstnStatus);

		if (statusList.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"All GSTR1 GET sections for userRequestId %d is successful",
						userRequestId));
			}
			return true;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"%d GSTR1 GET sections for userRequestId %d is not successful",
					statusList.size(), userRequestId));
		}
		return false;

	}
}

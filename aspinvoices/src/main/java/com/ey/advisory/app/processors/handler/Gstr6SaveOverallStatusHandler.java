/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr6SaveOverallStatusHandler")
public class Gstr6SaveOverallStatusHandler {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;

	@Autowired
	private GstnUserRequestRepository gstnUserRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	private Boolean isAllSectionsSaveCallsSuccess(Long userRequestId,
			String gstin, String retPeriod) {

		List<String> statusList = batchRepo
				.findStatusByUserRequestIdAndIsDeleteFalse(gstin, retPeriod,
						userRequestId);

		if (statusList == null || statusList.isEmpty()) {
			return false;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"%d GSTR6 SAVE sections are availble for userRequestId %d ",
					statusList.size(), userRequestId));
		}
		// Any refID is not polled(not got the status
		if (statusList.contains(null)) {
			return null;
		}
		List<String> gstnStatus = new ArrayList<>();
		gstnStatus.add(APIConstants.P);

		statusList.removeAll(gstnStatus);

		if (statusList.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"All GSTR6 SAVE sections for userRequestId %d is successful",
						userRequestId));
			}
			return true;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"%d GSTR6 SAVE sections for userRequestId %d is not successful",
					statusList.size(), userRequestId));
		}
		return false;

	}

	// @Transactional(value = "clientTransactionManager")
	public void execute(String groupCode, String userName) {

		try {
			TenantContext.setTenantId(groupCode);
			List<Object[]> objs = gstnUserRepo.findNewUserRequestIds(
					APIConstants.SAVE, APIConstants.GSTR6.toUpperCase());


			for (Object[] obj : objs) {

				Long userRequestId = obj[0] != null
						? Long.parseLong(String.valueOf(obj[0])) : null;
				String gstin = obj[1] != null ? String.valueOf(obj[1]) : null;
				String retPeriod = obj[2] != null ? String.valueOf(obj[2])
						: null;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"GSTR6 SAVE sections Overall status execution "
									+ "started for userRequestId %s, gstin %s, "
									+ "retperiod %s",
							userRequestId, gstin, retPeriod));
				}

				if (userRequestId != null) {
					Boolean isSuccess = isAllSectionsSaveCallsSuccess(
							userRequestId, gstin, retPeriod);

					if (isSuccess == null) {
						LOGGER.error(
								"Reference ids status is not polled completely.");
						continue;
					}
					if (isSuccess) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format(
									"GSTR6 SAVE sections Overall status  "
											+ "for userRequestId %s, gstin %s, "
											+ "retperiod %s is Success",
									userRequestId, gstin, retPeriod));
						}
						gstnUserRepo.updateRequestStatus(userRequestId, 1);
						List<Long> r6Details = batchRepo
								.findCalculateR6ByUserRequestId(userRequestId,
										APIConstants.CALCULATE_R6);
						if (r6Details == null || r6Details.isEmpty()) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Gstr6CalculateR6 is eligible to execute");
							}
							insertForCalculateR6(gstin, retPeriod, groupCode,
									userRequestId, userName);
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format(
									"GSTR6 SAVE sections Overall status "
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

	private void insertForCalculateR6(String gstin, String retPeriod,
			String groupCode, Long userRequestId, String userName) {

		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("gstin", gstin);
		jsonParams.addProperty("ret_period", retPeriod);
		jsonParams.addProperty("userRequestId", userRequestId);

		asyncJobsService.createJob(groupCode, "Gstr6CalculateR6",
				jsonParams.toString(), userName, JobConstants.PRIORITY,
				JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Async job created with job_categ as Gstr6CalculateR6");
		}

	}

}

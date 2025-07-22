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
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Service("Itc04Table5SaveToGstnJobHandler")
public class Itc04Table5SaveToGstnJobHandler {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;

	@Autowired
	private GstnUserRequestRepository gstnUserRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	public void execute(String groupCode, String userName) {
		try {
			TenantContext.setTenantId(groupCode);
			List<Object[]> objs = gstnUserRepo.findNewUserRequestIds(APIConstants.SAVE,
					APIConstants.ITC04.toUpperCase());

			for (Object[] obj : objs) {

				Long userRequestId = obj[0] != null ? Long.parseLong(String.valueOf(obj[0])) : null;
				String gstin = obj[1] != null ? String.valueOf(obj[1]) : null;
				String retPeriod = obj[2] != null ? String.valueOf(obj[2]) : null;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							String.format(
									"ITC04 Table5 SAVE sections status execution "
											+ "started for userRequestId %s, gstin %s, " + "retperiod %s",
									userRequestId, gstin, retPeriod));
				}

				if (userRequestId != null) {
					Boolean isSuccess = isAllSectionsSaveCallsSuccess(userRequestId, gstin, retPeriod);

					if (isSuccess == null) {
						LOGGER.error("Reference ids status is not polled completely.");
						continue;
					}
					if (isSuccess) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format("ITC04 Table5 SAVE sections status  "
									+ "for userRequestId %s, gstin %s, " + "retperiod %s is Success", userRequestId,
									gstin, retPeriod));
						}
						gstnUserRepo.updateRequestStatus(userRequestId, 1);
						SaveTable5Sections(gstin, retPeriod, userRequestId, APIConstants.TABLE5A, groupCode, userName);
						SaveTable5Sections(gstin, retPeriod, userRequestId, APIConstants.TABLE5B, groupCode, userName);
						SaveTable5Sections(gstin, retPeriod, userRequestId, APIConstants.TABLE5C, groupCode, userName);
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format("ITC04 SAVE Table5 Sections Overall status "
									+ "for userRequestId %s, gstin %s, " + "retperiod %s is not Success", userRequestId,
									gstin, retPeriod));
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

	private void SaveTable5Sections(String gstin, String retPeriod, Long userRequestId, String Section,
			String groupCode, String userName) {
		List<Long> table5Details = batchRepo.findItc04Table5sectionsByUserRequestId(userRequestId, APIConstants.SAVE,
				Section, APIConstants.ITC04.toUpperCase());
		if (table5Details == null || table5Details.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ITC04 Table5 sections is eligible to execute");
			}
			insertForTable5Sections(gstin, retPeriod, groupCode, userRequestId, userName, Section);
		}

	}

	private Boolean isAllSectionsSaveCallsSuccess(Long userRequestId, String gstin, String retPeriod) {

		List<String> statusList = batchRepo.findStatusByUserRequestIdAndIsDeleteFalse(gstin, retPeriod, userRequestId);

		if (statusList == null || statusList.isEmpty()) {
			return false;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("%d ITC04 SAVE sections are availble for userRequestId %d ", statusList.size(),
					userRequestId));
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
				LOGGER.debug(
						String.format("All ITC04 SAVE sections for userRequestId %d is successful", userRequestId));
			}
			return true;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("%d ITC04 SAVE sections for userRequestId %d is not successful",
					statusList.size(), userRequestId));
		}
		return false;

	}

	private void insertForTable5Sections(String gstin, String retPeriod, String groupCode, Long userRequestId,
			String userName, String section) {

		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("gstin", gstin);
		jsonParams.addProperty("returnPeriod", retPeriod);
		jsonParams.addProperty("userRequestId", userRequestId);
		jsonParams.addProperty("section", section);

		/*asyncJobsService.createJob(groupCode, "Itc04Table5SaveToGstn", jsonParams.toString(), userName,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID, JobConstants.SCHEDULE_AFTER_IN_MINS);*/
		
		asyncJobsService.createJob(groupCode,
				JobConstants.ITC04_SAVETOGSTN, jsonParams.toString(),
				userName, JobConstants.PRIORITY,
				JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Async job created with job_categ as Itc04Table5SaveToGstn");
		}

	}

}

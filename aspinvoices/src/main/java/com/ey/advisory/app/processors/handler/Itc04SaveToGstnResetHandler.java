package com.ey.advisory.app.processors.handler;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.Itc04DocRepository;
import com.ey.advisory.app.docs.dto.SaveToGstnSectionWiseReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Service("Itc04SaveToGstnResetHandler")
public class Itc04SaveToGstnResetHandler {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Itc04DocRepository")
	private Itc04DocRepository itc04DocRepository;

	@Transactional(value = "clientTransactionManager")
	public void itc04ResetAndCreateJob(Pair<String, String> pair, Gstr1SaveToGstnReqDto dto, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("itc04ResetAndCreateJob Request started {} ", dto);
		}

		String userName = SecurityContext.getUser().getUserPrincipalName();
		List<String> sections = dto.getTableSections();
		if (dto.getIsResetSave()) {
			itc04DocRepository.resetSaveItc04(pair.getValue0(), pair.getValue1(), sections);
		}
		Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(pair.getValue0(), pair.getValue1(),
				APIConstants.SAVE, APIConstants.ITC04.toUpperCase(), groupCode, userName, false, false, false);
		Gson gson = GsonUtil.newSAPGsonInstance();
		SaveToGstnSectionWiseReqDto sectionDto = new SaveToGstnSectionWiseReqDto(pair.getValue0(), pair.getValue1(),
				userRequestId, sections);
		String sectionJson = gson.toJson(sectionDto);
		createAsyncJobForSaveItc04(groupCode, userName, sectionJson);
		// status code 10 says status as USER REQUEST INITIATED
		saveToGstnEventStatus.Itc04EventEntry(pair.getValue1(), pair.getValue0(), 10, groupCode);
	}

	private void createAsyncJobForSaveItc04(String groupCode, String userName, String sectionJson) {
		asyncJobsService.createJob(groupCode, JobConstants.ITC04_SAVETOGSTN, sectionJson, userName,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID, JobConstants.SCHEDULE_AFTER_IN_MINS);
	}
}

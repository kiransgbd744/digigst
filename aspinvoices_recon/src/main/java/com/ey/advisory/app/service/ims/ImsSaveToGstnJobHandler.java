/**
 * 
 */
package com.ey.advisory.app.service.ims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.ImsSaveJobQueueRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("ImsSaveToGstnJobHandler")
public class ImsSaveToGstnJobHandler {

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Autowired
	private ImsSectionSaveHandler saveHandler;
	
	@Autowired
	private ImsSaveJobQueueRepository queueRepo;
	

	public String createJobs(String gstin, ImsSaveToGstnReqDto dto,
			String groupCode, String tableType, String action) {

		try {

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";			

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("gstin", gstin);
			jsonParams.addProperty("tableType", tableType);
			jsonParams.addProperty("action", action);

			queueRepo.updateInProgressStatus("InProgress", gstin, tableType, action);

			asyncJobsService.createJob(groupCode, JobConstants.IMS_SECTION_SAVE,
					jsonParams.toString(), userName, 1L, null, null);
			
			//saveHandler.saveActiveInvoices(gstin, groupCode, tableType);

		} catch ( Exception e) {
			LOGGER.error("error occured while submitting IMS save job {} :", e);
			throw new AppException(e);
		}
		return " Save to GSTN initiated successfully";
	}

}

package com.ey.advisory.app.services.refidpolling.gstr6;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.ReturnStatusRefIdDto;
import com.ey.advisory.app.docs.dto.GstnStatusDto;
import com.ey.advisory.app.services.refidpolling.gstr1.SaveBatchIdPollingManager;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */

@Slf4j
@Component("DefaultGstr6BatchIdPollingManager")
public class DefaultGstr6BatchIdPollingManager
		implements SaveBatchIdPollingManager {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("DefaultGstr6GstnResponseHandler")
	private Gstr6GstnResponseHandler gstr6GstnResponseHandler;
	
	/*@Autowired
    private AsyncJobsService asyncJobsService;*/

	public static final String PE = "PE";
	public static final String ER = "ER";
	public static final String P = "P";
	
	@Override
	public ReturnStatusRefIdDto processBatch(Gstr1SaveBatchEntity batch) {
		APIResponse resp = hitGstnServer.poolingApiCall(batch.getGroupCode(),
				batch.getSgstin(), batch.getReturnPeriod(), batch.getRefId(),
				batch.getId());
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- Sandbox response is {} ",
					resp);
		}
		TenantContext.setTenantId(batch.getGroupCode());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("groupCode {} is set", batch.getGroupCode());
		}

		if (!resp.isSuccess()) {
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_FAILED,now);
			String msg = "failed to get RefId satus from Gstn";
			LOGGER.error(msg);
			return null;
		}

		String apiResp = resp.getResponse();
		JsonObject jsonRoot = null;
		ReturnStatusRefIdDto refIdStatus = null;

		try {
			jsonRoot = (new JsonParser()).parse(apiResp).getAsJsonObject();
			String gstnStatus = jsonRoot.get("status_cd").getAsString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr6 ReturnStatus Sandbox status is {} ",
						gstnStatus);
			}
			refIdStatus = new ReturnStatusRefIdDto();
			refIdStatus.setGstnBatchId(batch.getId());
			refIdStatus.setRefId(batch.getRefId());
			refIdStatus.setStatus(gstnStatus);
			refIdStatus.setSection(batch.getSection());
			Gson gson = new Gson();
			GstnStatusDto dto = gson.fromJson(jsonRoot, GstnStatusDto.class);
			refIdStatus.setReturnType(batch.getReturnType());

			if (P.equalsIgnoreCase(gstnStatus)) {
				LOGGER.info("P is processed.");
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 40, batch.getGroupCode());
				gstr6GstnResponseHandler.phandleResponse(null, batch);
				/*insertForCalculateR6(batch.getSgstin(), batch.getReturnPeriod(),
						batch.getGroupCode(), batch.getId());*/
			} else if (PE.equalsIgnoreCase(gstnStatus)) {
				LOGGER.info("PE is processed.");
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 50, batch.getGroupCode());
				gstr6GstnResponseHandler.pEhandleResponse(jsonRoot, batch);
			} else if (ER.equalsIgnoreCase(gstnStatus)) {
				String errorMesg = dto.getError_report().getError_msg();
				String errorCode = dto.getError_report().getError_cd();
				batchSaveStatusRepository.updateErrorMesg(batch.getId(),
						errorCode, errorMesg,now);
				LOGGER.info("ER is processed.");
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 60, batch.getGroupCode());
				gstr6GstnResponseHandler.ehandleResponse(null, batch);
			}else {				
				LOGGER.error("Pooling Gstr6 ReturnStatus- intermediate status"
						+ gstnStatus);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Pooling Gstr6 ReturnStatus- IP/REC response handling started for {} ",
							batch.getSection());
				}
				Integer errorCount = 0;				
				batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(),
						gstnStatus, errorCount, now);
			}			
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_COMPLETED,now);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr6 ReturnStatus Processing Completed.");
			}
		} catch (Exception ex) {			
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_FAILED,now);
			String msg = "failed to parse the response";
			LOGGER.error(msg, ex);
		}
		return refIdStatus;
	}
	
	/*private void insertForCalculateR6(String gstin, String retPeriod,
			String groupCode, Long batchId) {

		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("gstin", gstin);
		jsonParams.addProperty("ret_period", retPeriod);
		jsonParams.addProperty("batchId", batchId);

		asyncJobsService.createJob(groupCode, "Gstr6CalculateR6",
				jsonParams.toString(), APIConstants.SYSTEM,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);

	}*/

}

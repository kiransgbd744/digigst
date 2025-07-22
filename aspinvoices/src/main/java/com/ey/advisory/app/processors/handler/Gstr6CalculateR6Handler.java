/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.gstr6.Gstr6CalculateR6Dto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.BatchHandler;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr6CalculateR6Handler")
public class Gstr6CalculateR6Handler {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;
	
	@Autowired
    private AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("batchHandler")
	private BatchHandler batchHandler;
	
	@Transactional(value = "clientTransactionManager")
	public void calculateR6(String jsonString, String groupCode,
			String userName) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr6CalculateR6Handler with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			Gstr6CalculateR6Dto dto = gson.fromJson(jsonString,
					Gstr6CalculateR6Dto.class);
			
			TenantContext.setTenantId(groupCode);
			
			Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(dto.getGstin(),
					dto.getRetPeriod(), "", groupCode, APIConstants.GSTR6, 0,
					APIConstants.CALCULATE_R6, 0l, dto.getUserRequestId());
			Long gstnBatchId = saveBatch.getId();
			APIResponse response = hitGstnServer.gstr6CalculateR6Call(groupCode,
					jsonString, dto.getGstin(), dto.getRetPeriod(), gstnBatchId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6CalculateR6Handler Sandbox response {} ",
						response);
			}

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			// Gstr6CalculateR6AndSaveCrossItcEntity entity = new
			// Gstr6CalculateR6AndSaveCrossItcEntity();
			/*Gstr1SaveBatchEntity entity = new Gstr1SaveBatchEntity();
			entity.setSgstin(dto.getGstin());
			entity.setReturnPeriod(dto.getRetPeriod());
			entity.setGroupCode(groupCode);
			entity.setReturnType(APIConstants.GSTR6.toUpperCase());
			entity.setDerivedTaxperiod(
					GenUtil.convertTaxPeriodToInt(dto.getRetPeriod()));
			entity.setDelete(false);
			// entity.setBatchId(dto.getBatchId());
			// entity.setR6JobTime(now);
			entity.setUserRequestId(dto.getUserRequestId());
			entity.setOperationType(APIConstants.CALCULATE_R6);*/
			/*entity.setStatus(APIConstants.POLLING_COMPLETED);
			entity.setBatchDate(now);
			entity.setCreatedOn(now);
			entity.setCreatedBy(userName);
			// Generating Unique Dummy refId
			UUID uuid = UUID.randomUUID();
			entity.setRefId(uuid.toString());*/

			UUID uuid = UUID.randomUUID();
			String refId = uuid.toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 Calculate R6 ref_id {} ", refId);
			}
					
			if (response.isSuccess()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr6 Calculate R6 call got success for the input {} "
									+ "and response {} ",
							jsonString, response);
				}
				String status = response.getResponse();
				// entity.setR6Status(status);

			/*	entity.setGstnStatus(APIConstants.P);
				entity.setTxnId(response.getTxnId());*/
				//Gstr1SaveBatchEntity save = batchRepo.save(entity);
				
				String txnId = response.getTxnId();
				batchRepo.updateBatchRefID(refId, gstnBatchId, txnId, now);
				batchRepo.updateGstr1SaveBatchbyBatchId(gstnBatchId, APIConstants.P, 0, now);
				batchRepo.updateStatusById(gstnBatchId, APIConstants.POLLING_COMPLETED, now);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr6 Calculate R6 P - Save Batch entity id {} ",
							gstnBatchId);
				}

				insertForSaveCrossItc(dto.getGstin(), dto.getRetPeriod(),
						groupCode, dto.getUserRequestId(), userName);

			} else {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr6 Calculate R6 call got falied for the input {} "
									+ "and response {} ",
							jsonString, response);
				}
				APIError error = response.getError();
				/*if (response.getError() != null) {
					entity.setErrorCode(error.getErrorCode());
					entity.setErrorDesc(error.getErrorCode());
				}
				entity.setGstnStatus(APIConstants.ER);
				Gstr1SaveBatchEntity save = batchRepo.save(entity);*/
				
				String txnId = response != null ? response.getTxnId() : null;
				batchRepo.updateBatchRefID(refId, gstnBatchId, txnId, now);
				batchRepo.updateGstr1SaveBatchbyBatchId(gstnBatchId, APIConstants.ER, 0, now);
				if (response.getError() != null) {
					String errorCode = error.getErrorCode();
					String errorDesc = error.getErrorDesc();
					batchRepo.updateErrorMesg(gstnBatchId, errorCode, errorDesc, now);
					}
				batchRepo.updateStatusById(gstnBatchId, APIConstants.POLLING_COMPLETED, now);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr6 Calculate R6 ER - Save Batch entity id {} ",
							gstnBatchId);
				}
				
			}
		} catch (Exception ex) {
			String msg = "App Exception";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}
	
	private void insertForSaveCrossItc(String gstin, String retPeriod,
			String groupCode, Long userRequestId, String userName) {

		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("gstin", gstin);
		jsonParams.addProperty("ret_period", retPeriod);
		jsonParams.addProperty("userRequestId", userRequestId);

		asyncJobsService.createJob(groupCode, "Gstr6SaveCrossITCDetails",
				jsonParams.toString(), userName,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Async job created with job_categ as Gstr6SaveCrossITCDetails");
		}

	}
	
}

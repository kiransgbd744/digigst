package com.ey.advisory.processors.test;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.domain.client.Gstr2bRegenerateSaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr2bRegenerateBatchRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Component("Gstr2BRegenerateSaveToGstnProcessor")
@Slf4j
public class Gstr2BRegenerateSaveToGstnProcessor implements TaskProcessor {
	
	@Autowired
	@Qualifier("Gstr2bRegenerateBatchRepository")
	private Gstr2bRegenerateBatchRepository saveStatusRepo;
	
	@Autowired
	private GetAnx1BatchRepository anx1Repo;
	
	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	
	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2B SaveToGstn Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}
		
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		String gstin = requestObject.get(APIConstants.GSTIN).getAsString();
		String taxPeriod = requestObject.get(APIConstants.TAXPERIOD)
				.getAsString();
		String refId = requestObject.get(APIConstants.REFID).getAsString();
		
		try{
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("GSTR RETURN STATUS API call for -> GSTIN: '%s', "
							+ "Taxperiod: '%s'", gstin, taxPeriod);
			LOGGER.debug(msg);
		}
		callGstrReturnStatusApi(gstin, taxPeriod, refId, groupCode);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"2B Transaction polling is done -> GSTIN: '%s', "
							+ "Taxperiod: '%s', refId : '%s'",
					gstin, taxPeriod, refId);
			LOGGER.debug(msg);
		}
	} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
	}

		private void callGstrReturnStatusApi(String gstin, String taxPeriod,
		String refId, String groupCode) {
	try {

		APIResponse resp = gstnServer.poolingApiCallForGstr2b(groupCode, refId,
				gstin, taxPeriod);

		if (!resp.isSuccess()) {

			String errResp = resp.getError().toString();
			Clob errRespClob = GenUtil.convertStringToClob(errResp);

			// update
			saveStatusRepo.updateStatusAndReponseForRefId(
					APIConstants.POLLING_FAILED, null, errRespClob, refId,
					LocalDateTime.now());
			
			//TODO
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR2B RETURN STATUS API call response -> '%s'",
					resp.getResponse());
			LOGGER.debug(msg);
		}
		JsonObject respObj = (new JsonParser()).parse(resp.getResponse())
				.getAsJsonObject();

		String status = respObj.get("status_cd").toString().replace("\"",
				"");
		
		String errorCode = respObj.get("err_cd").toString().replace("\"",
				"");

		String errorMessage = respObj.get("err_msg").toString().replace("\"",
				"");

		String successResp = resp.getResponse();
		Clob successRespClob = GenUtil.convertStringToClob(successResp);

		if ("ER".equalsIgnoreCase(status)) {
			saveStatusRepo.updateErrorStatusAndReponseForRefId(
					APIConstants.POLLING_COMPLETED, status, successRespClob,
					refId, LocalDateTime.now(), errorCode, errorMessage);
			
			//update batch status as well 
			List<Gstr2bRegenerateSaveBatchEntity> saveEntity = saveStatusRepo.findByGstnSaveRefIdAndIsDeleteFalse(refId);
			Long batchId = saveEntity.get(0).getBatchId();
			anx1Repo.updateGstr2bStatus("FAILED", batchId, errorMessage, errorCode);
			return ;
			//TODO
		}else if ("P".equalsIgnoreCase(status)) 
		{
			saveStatusRepo.updateStatusAndReponseForRefId(
					APIConstants.POLLING_COMPLETED, status, successRespClob,
					refId, LocalDateTime.now());
			
			//update batch status as well 
			List<Gstr2bRegenerateSaveBatchEntity> saveEntity = saveStatusRepo.findByGstnSaveRefIdAndIsDeleteFalse(refId);
			Long batchId = saveEntity.get(0).getBatchId();
			anx1Repo.updateGstr2bStatus("SUCCESS", batchId, null, null);
			return;
		}else {
			LOGGER.error(" else block status {} ", status);

			saveStatusRepo.updateStatusAndReponseForRefId(
			        APIConstants.POLLING_FAILED, status, null, refId,
			        LocalDateTime.now());

			List<Gstr2bRegenerateSaveBatchEntity> saveEntity = saveStatusRepo
			        .findByGstnSaveRefIdAndIsDeleteFalse(refId);
			Long batchId = saveEntity.get(0).getBatchId();
			anx1Repo.updateGstr2bStatus("FAILED", batchId, errorMessage,
			        errorCode);
		}
		LOGGER.debug(" Polling Completed of gstr2b regeneration ");
		
	} catch (Exception ex) {
		saveStatusRepo.updateStatusAndReponseForRefId(
				APIConstants.POLLING_FAILED, null, null, refId,
				LocalDateTime.now());
		List<Gstr2bRegenerateSaveBatchEntity> saveEntity = saveStatusRepo
		        .findByGstnSaveRefIdAndIsDeleteFalse(refId);
		Long batchId = saveEntity.get(0).getBatchId();

		anx1Repo.updateGstr2bStatus("FAILED", batchId, "APP EXCEPTION",
		        null);
		String msg = "Exception while Transaction polling of GSTR2B REGENERATE SAVE GET";
		LOGGER.error(msg, ex);
		throw new AppException(ex, msg);
	}
}
}

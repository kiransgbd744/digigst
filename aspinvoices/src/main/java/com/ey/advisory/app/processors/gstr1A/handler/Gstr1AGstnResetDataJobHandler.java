package com.ey.advisory.app.processors.gstr1A.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.DeleteGstr1;
import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.BatchHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("Gstr1AGstnResetDataJobHandler")
public class Gstr1AGstnResetDataJobHandler {

	@Autowired
	@Qualifier("batchHandler")
	private BatchHandler batchHandler;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	public void gstnReset(String jsonReq, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1A AutoDraft SaveToGstn Job has Started");
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		SaveToGstnReqDto dto = gson.fromJson(requestObject,
				SaveToGstnReqDto.class);

		String gstin = dto.getGstin();
		String retPeriod = dto.getReturnPeriod();
		Long userRequestId = dto.getUserRequestId();
		/*String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
		}*/
	//	SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		try {
			if ((gstin != null && !gstin.isEmpty()) && (retPeriod != null && !retPeriod.isEmpty())) {
				DeleteGstr1 deleteData = new DeleteGstr1();
				deleteData.setGstin(gstin);
				deleteData.setRetPeroid(retPeriod);
				if (deleteData != null) {
					saveToGstnEventStatus.EventEntry(retPeriod, gstin, 20, groupCode);
					
					String batch = gson.toJson(deleteData);
					Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(
							gstin, retPeriod, null, groupCode,
							APIConstants.GSTR1A, 1,
							APIConstants.DELETE_FULL_DATA, 0l, userRequestId);
					Long gstnBatchId = saveBatch.getId();
					APIResponse resp = null;
					try {
						saveToGstnEventStatus.EventEntry(retPeriod, gstin, 30, groupCode);
						resp = gstnServer.gstr1ADeleteApiCall(groupCode, batch, gstin, retPeriod, gstnBatchId);
					} catch (java.lang.UnsupportedOperationException | AppException ex) {
						LOGGER.error("GSTR1A delete from gstn api call exception", ex.getMessage());
						batchHandler.deleteBatch(gstnBatchId, groupCode);
						saveToGstnEventStatus.EventEntry(retPeriod, gstin, 60, groupCode);
						throw new AppException(ex, "{} error while saving batch to Gstn");
					}
					String refId = null;
					if (resp.getResponse() != null) {
						refId = batchHandler.updateGstr1ARefIdAndTxnId(groupCode, gstnBatchId, resp);
					
					} else {
						batchHandler.deleteBatch(gstnBatchId, groupCode);
					}
					LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId, refId);
				}
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while Deleting from Gstn GSTIN: {} , RETPEROID: {}, Error: {}";
			LOGGER.error(msg, gstin, retPeriod, ex);
			throw new APIException(msg, ex);
		}
		//return oneResp;
	}

}

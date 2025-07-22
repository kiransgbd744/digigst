package com.ey.advisory.app.services.refidpolling.gstr1a;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.GstnStatusDto;
import com.ey.advisory.app.docs.dto.ReturnStatusRefIdDto;
import com.ey.advisory.app.services.refidpolling.gstr1.Gstr1GstnResponseHandler;
import com.ey.advisory.app.services.refidpolling.gstr1.SaveBatchIdPollingManager;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1ADefaultBatchIdPollingManager")
public class Gstr1ADefaultBatchIdPollingManager
		implements SaveBatchIdPollingManager {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	@Qualifier("DefaultGstr1APResponseHandler")
	private Gstr1GstnResponseHandler gstr1ASuccessRespHandler;

	@Autowired
	@Qualifier("DefaultGstr1APEResponseHandler")
	private Gstr1GstnResponseHandler gstr1ApeResponseHandler;

	@Autowired
	@Qualifier("DefaultGstr1AErrorResponseHandler")
	private Gstr1GstnResponseHandler gstr1AerrorReponseHandler;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	public static final String PE = "PE";
	public static final String ER = "ER";
	public static final String P = "P";

	@Override
	public ReturnStatusRefIdDto processBatch(Gstr1SaveBatchEntity batch) {

		APIResponse resp = hitGstnServer.poolingApiCallForGstr1A(batch.getGroupCode(),
				batch.getSgstin(), batch.getReturnPeriod(), batch.getRefId());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr1A ReturnStatus- Sandbox response is {} ",
					resp);
		}
		TenantContext.setTenantId(batch.getGroupCode());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("groupCode {} is set", batch.getGroupCode());
		}

		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_FAILED, now);
			String msg = "failed to get RefId satus from Gstn";
			LOGGER.error(msg);
			return null;
		}

		// Build the Json Object.
		String apiResp = resp.getResponse();
		JsonObject jsonRoot = null;
		ReturnStatusRefIdDto refIdStatus = null;
		try {
			jsonRoot = JsonParser.parseString(apiResp).getAsJsonObject();
			// Get the Processing status from the Json
			String gstnStatus = jsonRoot.get("status_cd").getAsString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr1A ReturnStatus Sandbox status is {} ",
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
			// String errorCode=dto.getError_report().getError_cd();

			if (P.equalsIgnoreCase(gstnStatus)) {
				LOGGER.info("P is processed.");
				// status code 40 says status as SAVE SUCCESSFUL AT GSTIN
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 40, batch.getGroupCode(),
						!Strings.isNullOrEmpty(batch.getSection())
								? batch.getSection().toUpperCase() : null);

				gstr1ASuccessRespHandler.handleResponse(null, batch);

			} else if (PE.equalsIgnoreCase(gstnStatus)) {

				LOGGER.info("PE is processed.");
				// status code 50 says status as SAVE SUCCESSFUL WITH ERRORS
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 50, batch.getGroupCode(),
						!Strings.isNullOrEmpty(batch.getSection())
								? batch.getSection().toUpperCase() : null);
				gstr1ApeResponseHandler.handleResponse(null, batch);

			} else if (ER.equalsIgnoreCase(gstnStatus)) {
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				String errorMesg = dto.getError_report().getError_msg();
				String errorCode = dto.getError_report().getError_cd();
				batchSaveStatusRepository.updateErrorMesg(batch.getId(),
						errorCode, errorMesg, now);
				LOGGER.info("ER is processed.");
				// status code 60 says status as SAVE FAILED AT GSTIN
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 60, batch.getGroupCode(),
						!Strings.isNullOrEmpty(batch.getSection())
								? batch.getSection().toUpperCase() : null);
				gstr1AerrorReponseHandler.handleResponse(null, batch);

			} else {

				LOGGER.error("Pooling Gstr1A ReturnStatus- intermediate status"
						+ gstnStatus);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Pooling Gstr1A ReturnStatus- IP/REC response handling started for {} ",
							batch.getSection());
				}
				Integer errorCount = 0;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(
						batch.getId(), gstnStatus, errorCount, now);
			}
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_COMPLETED, now);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr1A ReturnStatus Processing Completed.");
			}
		} catch (Exception ex) {
			String msg = "Failed to parse the Gstr1A pooling response";
			LOGGER.error(msg, ex);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_FAILED, now);
		}
		return refIdStatus;
	}

}

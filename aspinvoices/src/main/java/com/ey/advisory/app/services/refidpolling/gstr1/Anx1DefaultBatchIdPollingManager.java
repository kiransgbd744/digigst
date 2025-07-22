package com.ey.advisory.app.services.refidpolling.gstr1;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.GstnStatusDto;
import com.ey.advisory.app.docs.dto.ReturnStatusRefIdDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("Anx1DefaultBatchIdPollingManager")
public class Anx1DefaultBatchIdPollingManager implements SaveBatchIdPollingManager {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	@Qualifier("Anx1DefaultGstnErrorResponseHandler")
	private Anx1GstnResponseHandler errorReponseHandler;

	@Autowired
	@Qualifier("DefaultAnx1PEResponseHandler")
	private Anx1GstnResponseHandler peResponseHandler;

	@Autowired
	@Qualifier("Anx1DefaultGstnPResponseHandler")
	private Anx1GstnResponseHandler successRespHandler;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	public static final String P = "P";
	public static final String PE = "PE";
	public static final String ER = "ER";

	@Override
	public ReturnStatusRefIdDto processBatch(Gstr1SaveBatchEntity batch) {

		APIResponse resp = hitGstnServer.newPoolingApiCall(batch);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr1 ReturnStatus- Sandbox response is {} ",
					resp);
		}
		TenantContext.setTenantId(batch.getGroupCode());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("groupCode {} is set", batch.getGroupCode());
		}
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_FAILED, now);
			String msg = "failed to get RefId satus from Gstn";
			LOGGER.error(msg);
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

		// Build the Json Object.
		String apiResp = resp.getResponse();
		JsonObject jsonRoot = null;
		ReturnStatusRefIdDto b2bStatus = new ReturnStatusRefIdDto();
		try {
			jsonRoot = (new JsonParser()).parse(apiResp).getAsJsonObject();

			// Get the Processing status from the Json
			String gstnStatus = jsonRoot.get("status_cd").getAsString();
			LOGGER.debug("gstnStatus is" + gstnStatus);
			// if the processing status is 'P' then invoke the steps for
			// processing 'P' status

			Gson gson = new Gson();
			GstnStatusDto dto = gson.fromJson(jsonRoot, GstnStatusDto.class);

			b2bStatus.setGstnBatchId(batch.getId());
			b2bStatus.setRefId(batch.getRefId());
			b2bStatus.setStatus(gstnStatus);
			if (P.equalsIgnoreCase(gstnStatus)) {
				LOGGER.info("P is processed.");
				// status code 40 says status as SAVE SUCCESSFUL AT GSTIN
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 40, batch.getGroupCode());

				successRespHandler.handleResponse(null, batch);
			} else if (PE.equalsIgnoreCase(gstnStatus)) {
				LOGGER.info("PE is processed.");
				// status code 50 says status as SAVE SUCCESSFUL WITH ERRORS
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 50, batch.getGroupCode());

				peResponseHandler.handleResponse(jsonRoot, batch);
			} else if (ER.equalsIgnoreCase(gstnStatus)) {
				LOGGER.info("ER is processed.");
				// status code 60 says status as SAVE FAILED AT GSTIN
				saveToGstnEventStatus.EventEntry(batch.getReturnPeriod(),
						batch.getSgstin(), 60, batch.getGroupCode());
				LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				batchSaveStatusRepository.updateErrorMesg(batch.getId(),
						dto.getErrorCd(),dto.getErrorMsg(), now);
				errorReponseHandler.handleResponse(null, batch);
			}
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_COMPLETED, now);
		} catch (Exception ex) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			batchSaveStatusRepository.updateStatusById(batch.getId(),
					APIConstants.POLLING_FAILED, now);
			String msg = "failed to parse the response";
			LOGGER.error(msg, ex);
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return b2bStatus;
	}

}

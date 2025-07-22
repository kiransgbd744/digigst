package com.ey.advisory.app.services.refidpolling.submit;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GstnDistributionSaveRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.ReturnStatusRefIdDto;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Service("Gstr6SubmitBatchIdPollingManager")
public class Gstr6SubmitBatchIdPollingManager implements SubmitBatchIdPollingManager {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	private GstnSubmitRepository gstr1SubmitRepo;
	
	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;
	
	@Autowired
	@Qualifier("Gstr6GstnDistributionSaveRepository")
	private Gstr6GstnDistributionSaveRepository distributionRepository;

	public static final String PE = "PE";
	public static final String ER = "ER";
	public static final String P = "P";

	@Override
	public ReturnStatusRefIdDto processBatch(String groupCode, GstnSubmitEntity batch) {

		APIResponse resp = hitGstnServer.poolingApiCall(groupCode, batch.getGstin(), batch.getRetPeriod(),
				batch.getRefId(), null);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 Submit ReturnStatus- Sandbox response is {} ", resp);
		}
		TenantContext.setTenantId(groupCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("groupCode {} is set", groupCode);
		}
		String gstnStatus = null;
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDate modifiedDate = EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now());
		// Build the Json Object.
		String apiResp = resp.getResponse();
		JsonObject jsonRoot = null;
		ReturnStatusRefIdDto refIdStatus = null;
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			gstr1SubmitRepo.updateGstr1SaveBatchbyBatchId(batch.getId(), gstnStatus, APIConstants.POLLING_FAILED, now);
			String msg = "failed to get RefId satus from Gstn";
			LOGGER.error(msg);
			return null;
		}

		try {
			jsonRoot = (new JsonParser()).parse(apiResp).getAsJsonObject();
			// Get the Processing status from the Json
			gstnStatus = jsonRoot.get("status_cd").getAsString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr6 Submit ReturnStatus Sandbox status is {} ", gstnStatus);
			}

			if (P.equalsIgnoreCase(gstnStatus)) {

				LOGGER.info("Pooling Gstr6 Submit ReturnStatus P is processed.");
				inwardTransDocRepository.markSubmittedAsTrue(batch.getRetPeriod(), batch.getGstin(), modifiedDate, now);
				distributionRepository.markSubmittedAsTrue(batch.getRetPeriod(), batch.getGstin(), modifiedDate, now);

			} else if (PE.equalsIgnoreCase(gstnStatus)) {

				LOGGER.info("Pooling Gstr6 Submit ReturnStatus PE is processed.");

			} else if (ER.equalsIgnoreCase(gstnStatus)) {

				LOGGER.info("Pooling Gstr6 Submit ReturnStatus ER is processed.");

			} else {

				LOGGER.error("Pooling Gstr6 Submit ReturnStatus- intermediate status" + gstnStatus);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Pooling Gstr6 Submit ReturnStatus- IP/REC response handling started");
				}

			}

			// updating gstn status in batch table.
			gstr1SubmitRepo.updateGstr1SaveBatchbyBatchId(batch.getId(), gstnStatus, APIConstants.POLLING_COMPLETED,
					now);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr6 Submit ReturnStatus Processing Completed.");
			}
		} catch (Exception ex) {
			gstr1SubmitRepo.updateGstr1SaveBatchbyBatchId(batch.getId(), gstnStatus, APIConstants.POLLING_FAILED, now);
			String msg = "failed to parse the response";
			LOGGER.error(msg, ex);
		}
		return refIdStatus;
	}

}

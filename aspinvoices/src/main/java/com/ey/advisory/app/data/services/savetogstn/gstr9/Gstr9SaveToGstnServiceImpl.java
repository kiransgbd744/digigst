package com.ey.advisory.app.data.services.savetogstn.gstr9;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9SaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9SaveStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.data.services.gstr9.Gstr9InwardUtil;
import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */

@Slf4j
@Component("Gstr9SaveToGstnServiceImpl")
public class Gstr9SaveToGstnServiceImpl implements Gstr9SaveToGstnService {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	Gstr9SaveToGstnDataUtil gstr9SaveToGstnDataUtil;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private Gstr9SaveStatusRepository gstr9SaveStatusRepository;

	@Autowired
	@Qualifier("Gstr9SavetoGstnApiHandlerServiceImpl")
	private Gstr9SavetoGstnApiHandlerService gstr9SavetoGstnApiHandlerService;

	@Autowired
	private GstrReturnStatusRepository returnStatusRepo;

	@Autowired
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	private Gstr9HsnProcessedRepository gstr9HsnProcessedRepository;

	@Autowired
	@Qualifier("Gstr9SaveStatusEntryHandlerImpl")
	private Gstr9SaveStatusEntryHandler gstr9SaveStatusEntryHandler;

	@Override
	public String saveGstr9DataToGstn(String gstin, String fy) {

		String retPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fy);
		String respBody = null;
		String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);

		if (authStatus.equals("A")) {

			Long saveInprogressCnt = gstr9SaveStatusRepository
					.findByGstinAndTaxPeriodAndStatus(gstin, retPeriod,
							APIConstants.SAVE_INITIATED);
			if (saveInprogressCnt > 0) {
				respBody = "Save is already Inprogress.";
			} else {
				GstrReturnStatusEntity entity = returnStatusRepo
						.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
								gstin, retPeriod, APIConstants.GSTR9);
				if (entity != null && APIConstants.FILED
						.equalsIgnoreCase(entity.getStatus())) {
					respBody = "GSTR9 is Filed for Selected Financial period.";
				} else {

					boolean isEligibleForSave = isActiveRecordsAvailableForSave(
							gstin, retPeriod);

					if (!isEligibleForSave) {
						String msg = "Zero eligible documents found to perform Gstr9 Save to Gstn.";
						LOGGER.error(msg);

						return msg;
					}

					Gstr9SaveStatusEntity saveStatusentity = new Gstr9SaveStatusEntity();
					saveStatusentity.setTaxPeriod(retPeriod);
					saveStatusentity.setGstin(gstin);
					saveStatusentity.setStatus("REQUEST_SUBMITTED");
					saveStatusentity.setErrorCount(0);
					saveStatusentity.setCreatedOn(
							EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
					saveStatusentity.setUpdatedOn(
							EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
					gstr9SaveStatusRepository.save(saveStatusentity);

					JsonObject jsonParams = new JsonObject();
					jsonParams.addProperty(APIConstants.GSTIN, gstin);
					jsonParams.addProperty("fy", fy);


					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.SAVE_GSTR9_TO_GSTN,
							jsonParams.toString(), "SYSTEM", 1L, null, null);
					

					respBody = "GSTR9 Save Request Submitted Successfully.";

				}
			}
		} else {
			respBody = "Auth Token is Inactive, Please Activate";
		}
		return respBody;
	}

	private boolean isActiveRecordsAvailableForSave(String gstin,
			String retPeriod) {

		Long activeUserUnputCount = gstr9UserInputRepository
				.findActiveCounts(gstin, retPeriod);

		Long activeHsnCount = gstr9HsnProcessedRepository
				.findActiveHsnCounts(gstin, retPeriod);

		return activeUserUnputCount > 0 || activeHsnCount > 0;

	}

	@Override
	public void callGstr9SaveToGstn(String gstin, String fy, String groupCode) {

		String isDigigst="true";
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(
					"Executing Gstr9 callGstr9SaveToGstn method with args {}{}",
					gstin, fy);

		try {
			GetDetailsForGstr9ReqDto getDetailsForGstr9ReqDto = gstr9SaveToGstnDataUtil
					.populateGstr9SavePayload(gstin, fy, isDigigst);

			gstr9SavetoGstnApiHandlerService
					.executeGstr9SaveToGstn(getDetailsForGstr9ReqDto);
		} catch (Exception ex) {

			String msg = "Exception while in callGstr9SaveToGstn";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		}

	}

}

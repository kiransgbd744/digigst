package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.GSTR2aAutoReconRevIntgReqDto;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.GSTR2aAutoReconRevIntgService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("GSTR2aAutoReconRevIntgProcessor")
public class GSTR2aAutoReconRevIntgProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GSTR2aAutoReconRevIntgServiceImpl")
	private GSTR2aAutoReconRevIntgService service;

	@Autowired
	private AutoReconStatusRepository autoReconStatusRepo;
	
	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin GSTR2aAutoReconRevIntgProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR2aAutoReconRevIntgProcessor Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		GSTR2aAutoReconRevIntgReqDto dto = null;
		try {

			 dto = gson.fromJson(jsonString,
					GSTR2aAutoReconRevIntgReqDto.class);
			dto.setGroupCode(groupCode);
			LOGGER.debug(
					"GSTR2aAutoReconRevIntgProcessor status is in progress ");
			
			if (dto.getAutoReconId() != null) {
				autoReconStatusRepo.updateERPPushStatus("INPROGRESS", null,
						dto.getAutoReconId());
			}
			
			Integer respcode = service.autoReconGet2AToErpPush(dto);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" Auto Recon Get2A  ERP Push response code is {}",
						respcode);
			}
		} catch (Exception e) {
			Long reconId = (dto != null && dto.getAutoReconId() != null) ? dto.getAutoReconId() : 0L;
			String msg = "GSTR2aAutoReconRevIntgProcessor got interrupted. ";
			LOGGER.error(msg, e);
			
			failedBatAltUtility.prepareAndTriggerAlert(
					String.valueOf(reconId),
					"Auto Recon 2A-RevIntg", msg);
			
			throw new AppException(msg, e);
		}
	}

}

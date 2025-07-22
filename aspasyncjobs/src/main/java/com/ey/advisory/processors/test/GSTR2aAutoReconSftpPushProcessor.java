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
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("GSTR2aAutoReconSftpPushProcessor")
public class GSTR2aAutoReconSftpPushProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GSTR2aAutoReconRevIntgServiceImpl")
	private GSTR2aAutoReconRevIntgService service;

	@Autowired
	private AutoReconStatusRepository autoReconStatusRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin GSTR2aAutoReconSftpPushProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR2aAutoReconSftpPushProcessor Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			GSTR2aAutoReconRevIntgReqDto dto = gson.fromJson(jsonString,
					GSTR2aAutoReconRevIntgReqDto.class);
			dto.setGroupCode(groupCode);
			LOGGER.debug(
					"GSTR2aAutoReconSftpPushProcessor status is in progress ");

			autoReconStatusRepo.updateERPPushStatus("INPROGRESS", null,
					dto.getAutoReconId());

			Integer respcode = service.autoReconGet2AToErpPush(dto);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" Auto Recon Get2A SFTP Push response code is {}",
						respcode);
			}
		} catch (Exception e) {
			String msg = "GSTR2aAutoReconSftpPushProcessor got interrupted. ";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}
}

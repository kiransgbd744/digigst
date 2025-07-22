/**
 * 
 */
package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.erp.OutwardSftpRequestDto;
import com.ey.advisory.app.services.jobs.erp.OutwardSftpResponseRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("OutwardSftpResponseRevIntProcessor")
public class OutwardSftpResponseRevIntProcessor implements TaskProcessor {

	private static Logger LOGGER = LoggerFactory
			.getLogger(OutwardSftpResponseRevIntProcessor.class);

	@Autowired
	@Qualifier("OutwardSftpResponseRevIntHandler")
	private OutwardSftpResponseRevIntHandler handler;
	
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Long fileId = null;
		try {
			String groupcode = message.getGroupCode();
			String json = message.getParamsJson();

			OutwardSftpRequestDto dto = gson.fromJson(json,
					OutwardSftpRequestDto.class);
			Long scenarioId = dto.getScenarioId();
			Long erpId = dto.getErpId();
			 fileId = dto.getFileId();
			if (groupcode != null && scenarioId != null && erpId != null) {
				Integer respCode = handler.getOutwardSftpResp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{} ", respCode);
				}
			}
		} catch (Exception e) {
			gstr1FileStatusRepository
			.updateChildCreatedFlagToFalse(fileId);
			LOGGER.error("Exception Occured:", e);
		}
	}
}

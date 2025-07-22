package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.erp.ComplainceHistoryRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service("ComplainceHistoryRevIntProcessor")
@Slf4j
public class ComplainceHistoryRevIntProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ComplainceHistoryRevIntHandler")
	private ComplainceHistoryRevIntHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ComplainceHistoryRevIntHandler Begin");
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupCode = message.getGroupCode();
			String jsonParam = message.getParamsJson();
			RevIntegrationScenarioTriggerDto dto = gson.fromJson(jsonParam,
					RevIntegrationScenarioTriggerDto.class);
			dto.setGroupcode(groupCode);
			if (dto.getGroupcode() != null && dto.getGstin() != null) {
				handler.complainceHistoryReqToErp(dto);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
			throw new AppException(e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ComplainceHistoryRevIntHandler End");
		}
	}
}

package com.ey.advisory.processors.test;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.services.jobs.erp.Ret1ApprovalReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

@Service("Ret1ApprovalReqRevIntegrationProcessor")
public class Ret1ApprovalReqRevIntegrationProcessor implements TaskProcessor {

	private static final  Logger LOGGER = LoggerFactory
			.getLogger(Ret1ApprovalReqRevIntegrationProcessor.class);

	@Autowired
	@Qualifier("Ret1ApprovalReqRevIntegrationHandler")
	private Ret1ApprovalReqRevIntegrationHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String groupCode = message.getGroupCode();
			String jsonParam = message.getParamsJson();
			ApprovalStatusReqDto dto = gson.fromJson(jsonParam,
					ApprovalStatusReqDto.class);
			String destinationName = dto.getDestinationName();
			String returnPeriod = dto.getReturnPeriod();
			Map<Long, String> gstins = dto.getGstinIds();
			Long entityId = dto.getEntityId();
			if (groupCode != null && destinationName != null
					&& returnPeriod != null && entityId != null
					&& gstins != null) {
				Integer approvalStatus = handler.ret1ApprovalToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Approval Status:{}", approvalStatus);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Excption Eccur: {}", e);
		}
	}

}

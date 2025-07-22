package com.ey.advisory.processors.test;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.services.jobs.erp.Gstr1ApprovalReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

@Service("Gstr1ApprovalReqRevIntegrationProcessor")
public class Gstr1ApprovalReqRevIntegrationProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ApprovalReqRevIntegrationProcessor.class);

	@Autowired
	@Qualifier("Gstr1ApprovalReqRevIntegrationHandler")
	private Gstr1ApprovalReqRevIntegrationHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupcode = message.getGroupCode();
			String json = message.getParamsJson();
			LOGGER.debug(
					"Anx1 Approval Req DocsRevIntegration Data Execute method is "
							+ "ON with groupcode {} and params {}",
					groupcode, json);
			Gson gson = GsonUtil.newSAPGsonInstance();
			ApprovalStatusReqDto dto = gson.fromJson(json,
					ApprovalStatusReqDto.class);
			String destinationName = dto.getDestinationName();
			String returnPeriod = dto.getReturnPeriod();
			Map<Long,String> gstins = dto.getGstinIds();
			Long entityId = dto.getEntityId();
			if (groupcode != null && destinationName != null
					&& returnPeriod != null && gstins != null
					&& entityId != null) {

				Integer respcodeOutward = handler
						.gsrt1ApprovalRequestToErp(dto);

				LOGGER.debug("Outward response code is {}", respcodeOutward);
				LOGGER.info(
						"Anx1ErrorDocsRevIntegration Processed with args {} ",
						json);

			} else {
				LOGGER.debug(
						"Partial Request Params groupcode, entityId, "
								+ "destinationName, returnPeriod and gstin are mandatory",
						dto);
			}
		} catch (Exception e) {
			LOGGER.error("Excption Eccur: {}",e);
		}
	}

}

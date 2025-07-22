package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.jobs.erp.Gstr6AProcAndRevSumRevHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

@Component("Gstr6AProcAndRevSumRevProcessor")
public class Gstr6AProcAndRevSumRevProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr6AProcAndRevSumRevHandler")
	private static Gstr6AProcAndRevSumRevHandler gstr6AProcAndRevSumRevHandler;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6AProcAndRevSumRevProcessor.class);

	@Override
	public void execute(Message message, AppExecContext context) {
		Integer responseCode = 0;
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String json = message.getParamsJson();
			RevIntegrationScenarioTriggerDto reqDto = gson.fromJson(json,
					RevIntegrationScenarioTriggerDto.class);
			if (reqDto.getDestinationName() != null
					&& reqDto.getEntityId() != null && reqDto.getGstin() != null
					&& reqDto.getScenarioId() != null) {
				responseCode = gstr6AProcAndRevSumRevHandler
						.processReviewSummaryToERP(reqDto);
				LOGGER.debug("Response Code:{} ", responseCode);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Partial Request Params groupcode, "
									+ "destinationName, entityId and gstin are mandatory",
							reqDto);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occred:{}", e);
		}
	}
}

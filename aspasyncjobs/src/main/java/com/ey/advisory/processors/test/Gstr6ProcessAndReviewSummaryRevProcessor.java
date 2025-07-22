package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.erp.Gstr6ProcessAndReviewSummaryRevHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

@Service("Gstr6ProcessAndReviewSummaryRevProcessor")
public class Gstr6ProcessAndReviewSummaryRevProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ProcessAndReviewSummaryRevProcessor.class);

	@Autowired
	@Qualifier("Gstr6ProcessAndReviewSummaryRevHandler")
	private Gstr6ProcessAndReviewSummaryRevHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {
		Integer response = 0;
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String groupCode = message.getGroupCode();
			String json = message.getParamsJson();

			RevIntegrationScenarioTriggerDto dto = gson.fromJson(json,
					RevIntegrationScenarioTriggerDto.class);
			String destName = dto.getDestinationName();
			Long entityId = dto.getEntityId();
			String gstinName = dto.getGstin();
			if (groupCode != null && destName != null && entityId != null
					&& gstinName != null) {
				response = handler.getProcessAndReviewSev(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{} ", response);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Partial Request Params groupcode, "
									+ "destinationName, entityId and gstin are mandatory",
							dto);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occred:", e);
		}
	}

}

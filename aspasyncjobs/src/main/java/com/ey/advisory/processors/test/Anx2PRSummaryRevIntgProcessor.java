package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.daos.prsummary.Anx2PRSummaryRevHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

@Service("Anx2PRSummaryRevIntgProcessor")
public class Anx2PRSummaryRevIntgProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2PRSummaryRevIntgProcessor.class);
	@Autowired
	@Qualifier("Anx2PRSummaryRevHandler")
	private Anx2PRSummaryRevHandler handler;

	public void execute(Message message, AppExecContext context) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupcode = message.getGroupCode();
			String json = message.getParamsJson();

			RevIntegrationScenarioTriggerDto dto = gson.fromJson(json,
					RevIntegrationScenarioTriggerDto.class);
			String destinationName = dto.getDestinationName();
			Long entityId = dto.getEntityId();
			String gstin = dto.getGstin();
			if (groupcode != null && destinationName != null && entityId != null
					&& gstin != null) {
				Integer respCode = handler.getAnx2PRSummary(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{} ", respCode);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
	}
}

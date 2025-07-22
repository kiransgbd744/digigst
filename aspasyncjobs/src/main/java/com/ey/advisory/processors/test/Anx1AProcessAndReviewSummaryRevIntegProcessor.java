package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.erp.Anx1AProcessAndReviewSummaryRevIntegHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

@Service("Anx1AProcessAndReviewSummaryRevIntegProcessor")
public class Anx1AProcessAndReviewSummaryRevIntegProcessor
		implements TaskProcessor {

	private static Logger LOGGER = LoggerFactory
			.getLogger(Anx1AProcessAndReviewSummaryRevIntegProcessor.class);
	@Autowired
	@Qualifier("Anx1AProcessAndReviewSummaryRevIntegHandler")
	private Anx1AProcessAndReviewSummaryRevIntegHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupdCode = message.getGroupCode();

		String json = message.getParamsJson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		RevIntegrationScenarioTriggerDto reqDto = gson.fromJson(json,
				RevIntegrationScenarioTriggerDto.class);
		String gstin = reqDto.getGstin();
		String destinationName = reqDto.getDestinationName();
		Long entityId = reqDto.getEntityId();

		if (groupdCode != null && gstin != null && destinationName != null
				&& entityId != null) {
			Integer processReview = handler.processReviewSummryToErp(reqDto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Process Review: {}", processReview);
			}
		}
	}
}

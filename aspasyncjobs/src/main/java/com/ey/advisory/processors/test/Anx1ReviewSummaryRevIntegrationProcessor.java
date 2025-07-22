/**
 * 
 */
package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.erp.Anx1ReviewSummaryReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1ReviewSummaryRevIntegrationProcessor")
public class Anx1ReviewSummaryRevIntegrationProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummaryRevIntegrationProcessor.class);

	@Autowired
	private Anx1ReviewSummaryReqRevIntegrationHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		LOGGER.debug(
				"Anx1ReviewSummary RevIntegration Data Execute method is ON with "
						+ "groupcode {} and params {}",
				groupcode, json);
		Gson gson = GsonUtil.newSAPGsonInstance();

		RevIntegrationScenarioTriggerDto dto = gson.fromJson(json,
				RevIntegrationScenarioTriggerDto.class);
		String destinationName = dto.getDestinationName();
		Long entityId = dto.getEntityId();
		String gstin = dto.getGstin();
		if (groupcode != null && destinationName != null && entityId != null
				&& gstin != null) {
			Integer respcodeOutward = handler.reviewSummaryRequestToErp(dto);
			LOGGER.debug("Response code is {}", respcodeOutward);
		} else {
			LOGGER.debug(
					"Partial Request Params groupcode, "
					+ "destinationName, entityId and gstin are mandatory",
					dto);
		}

	}

}
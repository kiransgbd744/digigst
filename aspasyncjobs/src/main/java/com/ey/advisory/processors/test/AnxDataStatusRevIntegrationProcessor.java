/**
 * 
 */
package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.AnxDataStatusRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
@Service("AnxDataStatusRevIntegrationProcessor")
public class AnxDataStatusRevIntegrationProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AnxDataStatusRevIntegrationProcessor.class);

	@Autowired
	private AnxDataStatusRevIntegrationHandler dataStatusHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String json = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"AnxDataStatusRevIntegration Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupcode, json);
		}

		RevIntegrationScenarioTriggerDto dto = gson.fromJson(json,
				RevIntegrationScenarioTriggerDto.class);
		String destinationName = dto.getDestinationName();
		Long entityId = dto.getEntityId();
		String gstin = dto.getGstin();

		if (groupcode != null && destinationName != null && entityId != null
				&& gstin != null) {

			Integer respcodeOutward = dataStatusHandler
					.dataStatusCountToErp(dto);
				LOGGER.debug("outward DataStatus Response code is {}",
						respcodeOutward);

		} else {
			LOGGER.debug(
					"Partial Request Params groupcode, destinationName, entityId and gstin are mandatory",
					dto);
		}

	}

}
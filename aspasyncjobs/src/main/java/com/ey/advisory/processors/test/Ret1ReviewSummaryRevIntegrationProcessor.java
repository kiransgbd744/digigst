package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.erp.Ret1ProcessReviewSummaryHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
/**
 * 
 * @author Umesha.M
 *
 */
@Service("Ret1ReviewSummaryRevIntegrationProcessor")
public class Ret1ReviewSummaryRevIntegrationProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1ReviewSummaryRevIntegrationProcessor.class);
	@Autowired
	@Qualifier("Ret1ProcessReviewSummaryHandler")
	private Ret1ProcessReviewSummaryHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		Gson gson = GsonUtil.newSAPGsonInstance();

		RevIntegrationScenarioTriggerDto dto = gson.fromJson(json,
				RevIntegrationScenarioTriggerDto.class);
		String destinationName = dto.getDestinationName();
		Long entityId = dto.getEntityId();
		String gstin = dto.getGstin();
		if (groupcode != null && destinationName != null && entityId != null
				&& gstin != null) {
			Integer respCode = handler.processReviewSummryToErp(dto,"RET1");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Review Summary response code is {}", respCode);
			} 
		}else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Partial Request Params groupcode, "
								+ "destinationName, entityId and gstin are mandatory",
						dto);
			}
		}
	}
}

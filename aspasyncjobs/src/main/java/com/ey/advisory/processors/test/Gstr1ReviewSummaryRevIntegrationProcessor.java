/**
	* 
	*/
package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.erp.Gstr1ReviewSummaryReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

/**
 * @author Umesh
 *
 */
@Service("Gstr1ReviewSummaryRevIntegrationProcessor")
public class Gstr1ReviewSummaryRevIntegrationProcessor
		implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ReviewSummaryRevIntegrationProcessor.class);

	@Autowired
	private Gstr1ReviewSummaryReqRevIntegrationHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			String groupcode = message.getGroupCode();
			String json = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1ReviewSummary RevIntegration Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupcode, json);
			}

			Gson gson = GsonUtil.newSAPGsonInstance();

			RevIntegrationScenarioTriggerDto dto = gson.fromJson(json,
					RevIntegrationScenarioTriggerDto.class);
			String destinationName = dto.getDestinationName();
			Long entityId = dto.getEntityId();
			String gstin = dto.getGstin();
			if (groupcode != null && destinationName != null && entityId != null
					&& gstin != null) {

				Integer respcode = handler.reviewSummaryRequestToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Review Summary response code is {}",
							respcode);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Partial Request Params groupcode, "
							+ "destinationName, entityId and gstin are mandatory",
							dto);
				}
			}

		} catch (Exception ex) {
			String msg = "Exception occured while reverse integarting gstr1";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

}
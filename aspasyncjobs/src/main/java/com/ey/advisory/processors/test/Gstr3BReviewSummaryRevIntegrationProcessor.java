/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.erp.Gstr3BReviewSummaryReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva
 *
 */
@Slf4j
@Service("Gstr3BReviewSummaryRevIntegrationProcessor")
public class Gstr3BReviewSummaryRevIntegrationProcessor
		implements TaskProcessor {

	@Autowired
	private Gstr3BReviewSummaryReqRevIntegrationHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr3B RevIntegration Data "
					+ "Execute method is ON with "
					+ "groupcode {} and params {}", groupcode, json);
		}

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			RevIntegrationScenarioTriggerDto dto = gson.fromJson(json,
					RevIntegrationScenarioTriggerDto.class);
			Integer respcode = handler.reviewSummaryRequestToErp(dto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Review Summary response code is {}", respcode);
			}

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			LOGGER.error("Exception while Gstr3B RevIntegration", ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

}
/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Anx1ErrorDocsRevIntegrationReqDto;
import com.ey.advisory.app.services.jobs.erp.Anx1ErrorDocsRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1ErrorDocsRevIntegrationProcessor")
@Slf4j
public class Anx1ErrorDocsRevIntegrationProcessor implements TaskProcessor {


	@Autowired
	private Anx1ErrorDocsRevIntegrationHandler errorHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		PerfUtil.logEventToFile(PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
				PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH_ASYNC_START,
				PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationProcessor,
				PerfamanceEventConstants.execute, null);
		
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		LOGGER.debug(
				"Anx1ErrorDocsRevIntegration Data Execute method is ON with "
						+ "groupcode {} and params {}",
				groupcode, json);
		Gson gson = GsonUtil.newSAPGsonInstance();

		Anx1ErrorDocsRevIntegrationReqDto dto = gson.fromJson(json,
				Anx1ErrorDocsRevIntegrationReqDto.class);
		String scenarioName = dto.getScenarioName();
		//Long entityId = dto.getEntityId();
		String gstin = dto.getGstin();
		dto.setGroupcode(groupcode);
		if (groupcode != null && scenarioName != null && gstin != null) {

			Integer respcode = errorHandler.erpErrorDocsToErp(dto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response code is {}", respcode);
			}
		
			LOGGER.info("Anx1ErrorDocsRevIntegration Processed with args {} ",
					json);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Partial Request Params groupcode, destinationName, entityId and gstin are mandatory",
						dto);
			}
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
				PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH_ASYNC_END,
				PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationProcessor,
				PerfamanceEventConstants.execute, null);
	}

}
/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Anx1ErrorDocsRevIntegrationReqDto;
import com.ey.advisory.app.jsonpushback.PushJsonToErp;
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
 * @author Khalid1.Khan
 *
 */
@Service("JsonErpAspErrorPushProcessor")
@Slf4j
public class JsonErpAspErrorPushProcessor implements TaskProcessor{
	
	@Autowired
	private Anx1ErrorDocsRevIntegrationHandler errorHandler;
	
	@Autowired
	@Qualifier("PushJsonToErpImpl")
	private PushJsonToErp pushJsonToErp;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		LOGGER.debug(
				"Asp Error Push  with "
						+ "groupcode {} and params {}",
				groupcode, json);
		Gson gson = GsonUtil.newSAPGsonInstance();

		Anx1ErrorDocsRevIntegrationReqDto dto = gson.fromJson(json,
				Anx1ErrorDocsRevIntegrationReqDto.class);
		String destinationName = dto.getDestinationName();
		String gstin = dto.getGstin();
		dto.setGroupcode(groupcode);
		if (groupcode != null && destinationName != null && gstin != null) {

			Integer respcode = pushJsonToErp.erpErrorDocsToErp(dto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response code is {}", respcode);
			}
		
			LOGGER.info("Anx1ErrorDocsRevIntegration Processed with args {} ",
					json);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Partial Request Params groupcode, destinationName, "
						+ "entityId and gstin are mandatory",
						dto);
			}
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
				PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH_ASYNC_END,
				PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationProcessor,
				PerfamanceEventConstants.execute, null);
	}

}

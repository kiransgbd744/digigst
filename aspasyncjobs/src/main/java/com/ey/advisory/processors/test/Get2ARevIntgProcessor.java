package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Get2AConsolidatedRevIntgDto;
import com.ey.advisory.app.services.jobs.erp.Get2ARevIntgHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

@Service("Get2ARevIntgProcessor")
public class Get2ARevIntgProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2ARevIntgProcessor.class);

	@Autowired
	@Qualifier("Get2ARevIntgHandler")
	private Get2ARevIntgHandler get2ARevIntgHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			Get2AConsolidatedRevIntgDto dto = gson.fromJson(json,
					Get2AConsolidatedRevIntgDto.class);
			String destinationName = dto.getDestinationName();
			Long entityId = dto.getEntityId();
			String gstin = dto.getGstin();
			dto.setJobId(message.getId());

			if (groupcode != null && destinationName != null && entityId != null
					&& gstin != null) {
				Integer respcode = get2ARevIntgHandler.get2AToErpPush(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get2A response code is {}", respcode);
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
			LOGGER.error("Exception Occured:", e);
		}
	}
}

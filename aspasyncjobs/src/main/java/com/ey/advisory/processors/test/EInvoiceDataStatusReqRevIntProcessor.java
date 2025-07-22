package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.erp.EInvoiceDataStatusReqRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

@Service("EInvoiceDataStatusReqRevIntProcessor")
public class EInvoiceDataStatusReqRevIntProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvoiceDataStatusReqRevIntProcessor.class);
	@Autowired
	@Qualifier("EInvoiceDataStatusReqRevIntHandler")
	private EInvoiceDataStatusReqRevIntHandler einvoiceDataStatusReqRevIntHandler;

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
			Integer respcode = einvoiceDataStatusReqRevIntHandler
					.dataStatusToERP(dto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Review Summary response code is {}", respcode);
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Partial Request Params groupcode, "
								+ "destinationName, entityId and gstin are mandatory",
						dto);
			}
		}
	}
}

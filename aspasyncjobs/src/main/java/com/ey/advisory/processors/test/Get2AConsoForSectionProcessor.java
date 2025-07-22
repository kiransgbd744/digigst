package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.app.services.jobs.erp.Get2AConsoForSectionHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;

/**
 * 
 * @author Umesha.M
 *
 */
@Service("Get2AConsoForSectionProcessor")
public class Get2AConsoForSectionProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2AConsoForSectionProcessor.class);
	@Autowired
	@Qualifier("Get2AConsoForSectionHandler")
	private Get2AConsoForSectionHandler handler;

	/*
	 * When ever calling Get GSTR 2A Procedure getting Group Code,Params and
	 * scenario name as a input parameter.
	 * 
	 */
	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String json = message.getParamsJson();
		String scenarioName = message.getJobCategory() != null
				? message.getJobCategory() : "Get2AConsoForSectionRevIntg";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Scenario Name:{}", scenarioName);
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Get2ARevIntReqDto dto = null;
		try {
			dto = gson.fromJson(json,
					Get2ARevIntReqDto.class);
			dto.setJobId(message.getId());
			dto.setGroupCode(groupCode);
			dto.setScenarioName(scenarioName);
			Integer responseCode = handler.erpToGet2AConsoForSection(dto);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response Code:{}", responseCode);
			}
		
		} catch (Exception e) {
			Long batchId = (dto != null && dto.getBatchId() != null) ? dto.getBatchId() : 0L;
			String errMsg = String.format(
					"Gstr2A Rev Integ Failed for batch id %s for group code %s",
					batchId, TenantContext.getTenantId());
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
	}
}

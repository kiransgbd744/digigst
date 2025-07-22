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
import com.google.gson.Gson;

/**
 * 
 * @author Umesha.M
 *
 */
@Service("Get2AConsoForSectionSFTPProcessor")
public class Get2AConsoForSectionSFTPProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2AConsoForSectionSFTPProcessor.class);
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
				? message.getJobCategory() : "Get2AConsoForSectionSFTPRevIntg";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Scenario Name:{}", scenarioName);
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		try {
			Get2ARevIntReqDto dto = gson.fromJson(json,
					Get2ARevIntReqDto.class);
			// setting Group code into DTO
			dto.setGroupCode(groupCode);
			// setting scenario name into DTO
			dto.setScenarioName(scenarioName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside Get2AConsoForSectionSFTPProcessor ");
			}
			Integer responseCode = handler.erpToGet2AConsoForSection(dto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Ended Get2AConsoForSectionSFTPProcessor ");
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response Code:{}", responseCode);
			}
			
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			throw new AppException("Exception Occured:", e);
		}
	}
}

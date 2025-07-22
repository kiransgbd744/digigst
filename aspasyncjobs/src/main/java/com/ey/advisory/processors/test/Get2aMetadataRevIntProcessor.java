package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Get2aMetadataRevIntReqDto;
import com.ey.advisory.app.processors.handler.Get2aMetadataRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Get2aMetadataRevIntProcessor")
public class Get2aMetadataRevIntProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Get2aMetadataRevIntHandler")
	private Get2aMetadataRevIntHandler metaDataHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OutwardPayloadMetadataRevIntProcessor execute Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String json = message.getParamsJson();
			String groupCode = message.getGroupCode();
			Get2aMetadataRevIntReqDto dto = gson.fromJson(json,
					Get2aMetadataRevIntReqDto.class);
			dto.setGroupcode(groupCode);
			dto.setJobId(message.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Scenario Permission :{}", groupCode);
			}
			if (groupCode != null && dto.getBatchId() != null) {
				Integer response = metaDataHandler.getGet2aMetadataDetails(dto);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{}", response);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OutwardPayloadMetadataRevIntProcessor execute End");
		}
	}
}
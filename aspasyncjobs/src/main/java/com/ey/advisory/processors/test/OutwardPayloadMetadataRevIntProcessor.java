package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.OutwardPayloadRepository;
import com.ey.advisory.app.services.jobs.erp.OutwardPayloadMetadataRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;
import com.google.gson.Gson;

@Service("OutwardPayloadMetadataRevIntProcessor")
public class OutwardPayloadMetadataRevIntProcessor implements TaskProcessor {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(OutwardPayloadMetadataRevIntProcessor.class);

	@Autowired
	@Qualifier("OutwardPayloadMetadataRevIntHandler")
	private OutwardPayloadMetadataRevIntHandler handler;

	@Autowired
	@Qualifier("OutwardPayloadRepository")
	private OutwardPayloadRepository repository;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OutwardPayloadMetadataRevIntProcessor execute Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String json = message.getParamsJson();
			String groupCode = message.getGroupCode();
			PayloadDocsRevIntegrationReqDto dto = gson.fromJson(json,
					PayloadDocsRevIntegrationReqDto.class);
			dto.setGroupcode(groupCode);
			dto.setJobId(message.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Scenario Permission :{}", groupCode);
			}
			if (groupCode != null && dto.getPayloadId() != null) {
				Integer response = handler.getPayloadMetadataDetails(dto,
						"OUTWARD");
				// success if http response code is the series of 200
				if (response != null
						&& String.valueOf(response).charAt(0) == '2') {
					repository.updateRevIntPushStatus(dto.getPayloadId(), 1);
				} else {
					repository
							.updateRevIntPushStatus(dto.getPayloadId(), 0);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{}", response);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OutwardPayloadMetadataRevIntProcessor execute End");
		}
	}
}

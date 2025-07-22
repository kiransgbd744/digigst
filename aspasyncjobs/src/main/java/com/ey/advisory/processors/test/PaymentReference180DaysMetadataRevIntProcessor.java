package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.PaymentReferencePayloadRepository;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;
import com.ey.advisory.services.days180.api.push.PaymentReference180DaysPayloadMetadataRevIntHandler;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Service("PaymentReference180DaysMetadataRevIntProcessor")
public class PaymentReference180DaysMetadataRevIntProcessor
		implements TaskProcessor {

	@Autowired
	@Qualifier("PaymentReference180DaysPayloadMetadataRevIntHandler")
	private PaymentReference180DaysPayloadMetadataRevIntHandler handler;

	@Autowired
	@Qualifier("PaymentReferencePayloadRepository")
	private PaymentReferencePayloadRepository repository;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"PaymentReference180DaysMetadataRevIntProcessor execute Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String json = message.getParamsJson();
			String groupCode = message.getGroupCode();
			PayloadDocsRevIntegrationReqDto dto = gson.fromJson(json,
					PayloadDocsRevIntegrationReqDto.class);
			dto.setGroupcode(groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Scenario Permission :{}", groupCode);
			}
			if (groupCode != null && dto.getPayloadId() != null) {
				Integer response = handler.getPayloadMetadataDetails(dto);
				// success if http response code is the series of 200
				if (response != null
						&& String.valueOf(response).charAt(0) == '2') {
					repository.updateRevIntPushStatus(dto.getPayloadId(), 1);
				} else {
					repository.updateRevIntPushStatus(dto.getPayloadId(), 0);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{}", response);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			throw new AppException(e);
			
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"PaymentReference180DaysMetadataRevIntProcessor execute End");
		}
	}
}

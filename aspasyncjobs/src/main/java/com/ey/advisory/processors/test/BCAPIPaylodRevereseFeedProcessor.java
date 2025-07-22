package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.OutwardPayloadRepository;
import com.ey.advisory.app.services.jobs.erp.BCAPIPaylodRevereseFeedHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("BCAPIPaylodRevereseFeedProcessor")
public class BCAPIPaylodRevereseFeedProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("BCAPIPaylodRevereseFeedHandler")
	private BCAPIPaylodRevereseFeedHandler handler;

	@Autowired
	@Qualifier("OutwardPayloadRepository")
	private OutwardPayloadRepository repository;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("BCAPIPaylodRevereseFeedProcessor execute BEGIN");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String groupCode = message.getGroupCode();
			String reqParams = message.getParamsJson();
			PayloadDocsRevIntegrationReqDto reqDto = gson.fromJson(reqParams,
					PayloadDocsRevIntegrationReqDto.class);
			reqDto.setGroupcode(groupCode);
			if (groupCode != null && reqDto != null
					&& reqDto.getPayloadId() != null) {
				Integer responsCode = handler.bcapiPaylodToErp(reqDto);
				if (responsCode != null
						&& String.valueOf(responsCode).charAt(0) == '2') {
					repository.updateRevIntPushStatus(reqDto.getPayloadId(), 1);
				} else {
					repository.updateRevIntPushStatus(reqDto.getPayloadId(), 0);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("BCAPIPaylodRevereseFeedProcessor execute END");
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
	}

}

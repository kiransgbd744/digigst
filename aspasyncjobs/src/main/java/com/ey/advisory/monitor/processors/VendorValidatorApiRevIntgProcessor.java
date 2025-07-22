package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.vendor.service.VendorValidatorApiRevIntgHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Service("VendorValidatorApiRevIntgProcessor")
public class VendorValidatorApiRevIntgProcessor
		implements TaskProcessor {

	@Autowired
	@Qualifier("VendorValidatorApiRevIntgHandler")
	private VendorValidatorApiRevIntgHandler handler;

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepo;

	@Override
	public void execute(Message message, AppExecContext ctx) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"VendorValidatorApiRevIntgProcessor execute Begin");
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
				Integer response = handler.getDataForErpPush(dto);
				// success if http response code is the series of 200
				if (response != null
						&& response == 200) {
					payloadRepo.updateRevIntPushStatus(dto.getPayloadId(), 1);
				} else {
					payloadRepo.updateRevIntPushStatus(dto.getPayloadId(), 0);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{}", response);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured"
					+ " VendorValidatorApiRevIntgProcessor {}:", e);
			throw new AppException(e);
			
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"VendorValidatorApiRevIntgProcessor execute End");
		}
	}
}

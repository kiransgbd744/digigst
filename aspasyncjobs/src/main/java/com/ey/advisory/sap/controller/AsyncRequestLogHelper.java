package com.ey.advisory.sap.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.LoggerIdContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("AsyncRequestLogHelper")
public class AsyncRequestLogHelper {

	@Autowired
	private LoggerAdviceRepository logAdvRepo;

	private static final List<String> JOBS_ELIGIBLE_TO_LOG = ImmutableList.of(
			JobConstants.EINV_CANCEL, JobConstants.EINVOICE_ASYNC,
			JobConstants.GENERATE_EWAYBILL, JobConstants.EWB_CANCEL);

	public void logJobParams(Message msg) {

		String msgType = msg.getMessageType();

		if (!JOBS_ELIGIBLE_TO_LOG.contains(msgType)) {
			LOGGER.debug("Job Category {} is not Eligible for Logging",
					msgType);
			return;
		}
		LocalDateTime curTime = LocalDateTime.now();
		try {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"About to start Logging for groupCode {} , requrl {}",
						TenantContext.getTenantId(), msg.getMessageType());
			ERPRequestLogEntity logEntity = new ERPRequestLogEntity();
			if (msg.getParamsJson() != null) {
				logEntity.setReqPayload(msg.getParamsJson());
			}
			logEntity.setReqUrl(msg.getMessageType());
			logEntity.setApiType(msg.getMessageType());
			logEntity.setCloudTimestamp(curTime);
			logEntity.setReqType("AsyncJob");
			logAdvRepo.save(logEntity);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Logged for groupCode {} , requrl {}",
						TenantContext.getTenantId(), msg.getMessageType());
			LoggerIdContext.setLoggerId(logEntity);
		} catch (Exception ex) {
			LOGGER.error("Exception while Logging the ERP Req to DB", ex);
		}
	}
}

package com.ey.advisory.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.LoggerIdContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggerAdviceHelper {
	public static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private LoggerAdviceRepository logAdvRepo;

	public void createReqLog(String reqBody, String reqUrl) {
		LocalDateTime curTime = LocalDateTime.now();
		try {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"About to start Logging for groupCode {} , requrl {}",
						TenantContext.getTenantId(), reqUrl);
			ERPRequestLogEntity logEntity = new ERPRequestLogEntity();
		
			if (reqBody != null) {
				logEntity.setReqPayload(reqBody);
			}
			logEntity.setReqUrl(reqUrl);
			logEntity.setApiType(reqUrl);
			logEntity.setCloudTimestamp(curTime);
			logEntity.setCloudTimestampIst(EYDateUtil.toISTDateTimeFromUTC(curTime));

			logEntity.setReqType("UI");
			logAdvRepo.save(logEntity);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Logged for groupCode {} , requrl {}",
						TenantContext.getTenantId(), reqUrl);
			LoggerIdContext.setLoggerId(logEntity);
		} catch (Exception ex) {
			LOGGER.error("Exception while Logging the ERP Req to DB", ex);
		}
	}
}

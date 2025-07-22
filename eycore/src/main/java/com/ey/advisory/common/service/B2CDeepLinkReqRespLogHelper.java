package com.ey.advisory.common.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.LoggerIdContext;
import com.ey.advisory.domain.client.B2CQRCodeRequestLogEntity;
import com.ey.advisory.repositories.client.B2CQRCodeLoggerRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class B2CDeepLinkReqRespLogHelper {

	@Autowired
	private B2CQRCodeLoggerRepository b2clogAdvRepo;

	public void updateResponsePayload(String payload,boolean urlStatus,String pan) {
		try {
			B2CQRCodeRequestLogEntity logEntity = LoggerIdContext
					.getB2CLoggerContext();
			if (logEntity != null) {
				logEntity.setRespPayload(payload);
				logEntity.setRespondedOn(LocalDateTime.now());
				logEntity.setUrlStatus(urlStatus);
				logEntity.setPan(pan);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while updating the B2C Response Payload Log",
					ex);
		}
	}

	public void updateUrlCreatedOn() {
		try {
			B2CQRCodeRequestLogEntity logEntity = LoggerIdContext
					.getB2CLoggerContext();
			if (logEntity != null) {
				logEntity.setUrlCreatedOn(LocalDateTime.now());
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while updating the B2C URL Creation on Column",
					ex);
		}
	}

	public void saveLogEntity() {
		try {
			B2CQRCodeRequestLogEntity logEntity = LoggerIdContext
					.getB2CLoggerContext();
			if (logEntity != null) {
				b2clogAdvRepo.save(logEntity);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while Saving the Final Log Entity", ex);
		} finally {
			LoggerIdContext.clearLoggerId();
		}
	}

}

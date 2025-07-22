package com.ey.advisory.controller.logger;

import java.lang.reflect.Type;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.sql.rowset.serial.SerialClob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.ey.advisory.common.BusinessCommonUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.LoggerIdContext;
import com.ey.advisory.common.MasterRegionCache;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.domain.client.B2CQRCodeRequestLogEntity;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.B2CQRCodeLoggerRepository;
import com.google.common.collect.ImmutableList;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class LoggerAdvice extends RequestBodyAdviceAdapter {

	private static final List<String> EXCLUDED_URLS = ImmutableList.of(
			"/api/generateAccessToken", "executeAsyncJob",
			"/api/saveB2CQROnboardingParamsBCAPI",
			"/api/saveB2CQRAmtOnboardingParamsBCAPI","/api/getPayloads","/api/healthCheck","/api/saveFallBackLogDetails","/api/getEinvEwbData");

	public static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private B2CQRCodeLoggerRepository b2clogAdvRepo;
	
	@Autowired
	private MasterRegionCache regionCache;

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		boolean isAllowedToLog = true;
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		HttpServletRequest req = attrs.getRequest();
		String reqPath = req.getServletPath();
		for (String url : EXCLUDED_URLS) {
			if (reqPath.contains(url)) {
				isAllowedToLog = false;
				break;
			}
		}
		return isAllowedToLog;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
			MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		LocalDateTime curTime = LocalDateTime.now();
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		HttpServletRequest req = attrs.getRequest();
		try {
			String reqUrl = new StringBuilder(req.getRequestURL()).toString();
			String reqBody = (String) body;
			String companyCode = req.getHeader("companyCode");

			if (reqUrl.contains("generateB2cDeepLink")) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Started Logging for DeepLink for groupCode {} , requrl {}",
							TenantContext.getTenantId(), reqUrl);

				B2CQRCodeRequestLogEntity b2clogEntity = new B2CQRCodeRequestLogEntity();
				b2clogEntity.setReqUrl(reqUrl);
				b2clogEntity.setCreatedOn(LocalDateTime.now());
				b2clogEntity.setReqReceivedOn(LocalDateTime.now());
				b2clogEntity.setCompanyCode(companyCode);
				if (reqBody != null) {
					Clob reqPayload = new SerialClob(reqBody.toCharArray());
					b2clogEntity.setReqPayload(reqPayload);
				}
				b2clogAdvRepo.save(b2clogEntity);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Logged for groupCode {} , requrl {}",
							TenantContext.getTenantId(), reqUrl);
				LoggerIdContext.setB2CLoggerContext(b2clogEntity);
			} else {
				String qryParams = req.getQueryString();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"About to start Logging for groupCode {} , requrl {}",
							TenantContext.getTenantId(), reqUrl);
				String payloadId = req.getHeader("payloadId");
				String checkSum = req.getHeader("checkSum");
				String sourceId = req.getHeader("sourceId");
				String erpTimeStamp = req.getHeader("erpTimeStamp");
				ERPRequestLogEntity logEntity = new ERPRequestLogEntity();
				if (erpTimeStamp != null) {
					try {
						LocalDateTime erpTime = LocalDateTime
								.parse(erpTimeStamp, formatter);
						logEntity.setErpTimestamp(erpTime);
					} catch (DateTimeParseException dpe) {
						LOGGER.error(
								"Exception while Parsing the ERP Timestamp",
								dpe);
					}
				}
				if (reqBody != null) {
					logEntity.setReqPayload(reqBody);
				}
				logEntity.setCheckSum(checkSum);
				logEntity.setPayloadId(payloadId);
				logEntity.setCompanyCode(companyCode);
				logEntity.setSourceId(sourceId);
				logEntity.setQryParams(qryParams);
				logEntity.setReqUrl(reqUrl);
				logEntity.setCloudTimestamp(curTime);
				logEntity.setCloudTimestampIst(EYDateUtil.toISTDateTimeFromUTC(curTime));
				logEntity.setReqType("REST");
				logEntity.setApiType(BusinessCommonUtil.apiType(reqUrl));
				logEntity.setOriginRegion(regionCache.findActiveRegion());
				logEntity.setAutoDrafted(false);
//				LoggerIdContext.setLoggerId(logEntity);
//				logAdvRepo.save(logEntity);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Logged for groupCode {} , requrl {}",
							TenantContext.getTenantId(), reqUrl);
				LoggerIdContext.setLoggerId(logEntity);
			}
		} catch (SQLException se) {
			LOGGER.error("Exception while converting the payload to clob", se);
		} catch (Exception ex) {
			String errMsg = String.format(
					"Exception while Logging the ERP Req" + " to DB group %s",
					TenantContext.getTenantId());
			LOGGER.error(errMsg, ex);
		}
		return body;
	}
}

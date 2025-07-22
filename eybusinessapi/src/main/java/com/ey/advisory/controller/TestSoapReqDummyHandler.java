package com.ey.advisory.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.ey.advisory.common.LoggerIdContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSoapReqDummyHandler implements SOAPHandler<SOAPMessageContext> {

	private LoggerAdviceRepository logAdvRepo;

	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public void close(MessageContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		if (TenantContext.getErrorMsg() != null) {
			return true;
		}
		Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (isRequest) {
			return true;
		}

		logAdvRepo = StaticContextHolder.getBean("LoggerAdviceRepository", LoggerAdviceRepository.class);
		ERPRequestLogEntity logEntity = new ERPRequestLogEntity();

		if (!isRequest) {
			try {
				HttpServletRequest request = (HttpServletRequest) context.get(AbstractHTTPDestination.HTTP_REQUEST);
				String reqUrl = request.getRequestURL().toString();
				SOAPMessage soapMsg = context.getMessage();
				SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
				SOAPBody soapBody = soapEnv.getBody();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				soapMsg.writeTo(stream);
				String message = new String(stream.toByteArray(), "utf-8");
				LOGGER.debug("Soap Body : : {} ", message);
				String operationname = "testSoap";
				Map<String, List<String>> headermap = (Map<String, List<String>>) context
						.get(MessageContext.HTTP_REQUEST_HEADERS);
				List<String> payloadIdlist = headermap.get("payloadId");
				List<String> checkSumlist = headermap.get("checkSum");
				List<String> erpTimeStamplist = headermap.get("erpTimeStamp");
				LocalDateTime curTime = LocalDateTime.now();
				if (payloadIdlist != null && !payloadIdlist.isEmpty()) {
					logEntity.setPayloadId(payloadIdlist.get(0));
				}

				if (checkSumlist != null && !checkSumlist.isEmpty()) {
					logEntity.setCheckSum(checkSumlist.get(0));
				}
				if (erpTimeStamplist != null && !erpTimeStamplist.isEmpty()) {
					String erpTimeStamp = checkSumlist.get(0);
					if (erpTimeStamp != null) {
						try {
							LocalDateTime erpTime = LocalDateTime.parse(erpTimeStamp, formatter);
							logEntity.setErpTimestamp(erpTime);
						} catch (DateTimeParseException dpe) {
							LOGGER.error("Exception while Parsing the ERP Timestamp", dpe);
						}
					}
				}
				if (message != null && !message.isEmpty()) {
					logEntity.setReqPayload(message);
				}
				logEntity.setCloudTimestamp(curTime);
				logEntity.setReqUrl(reqUrl + "/" + operationname);
				logEntity.setReqType("SOAP");
				logAdvRepo.save(logEntity);
				LoggerIdContext.setLoggerId(logEntity);
			} catch (Exception ex) {
				LOGGER.error("Exception while Logging the ERP Req to DB", ex);
			}
		}

		return true;
	}

	@Override
	public Set getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.ey.advisory.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ey.advisory.common.BusinessCommonUtil;
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
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ERPSoapReqLogHandler implements SOAPHandler<SOAPMessageContext> {

	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleMessage(SOAPMessageContext context) {

		if (TenantContext.getErrorMsg() != null) {
			return true;
		}
		Boolean isOutboundRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (isOutboundRequest) {
			return true;
		}
		ERPRequestLogEntity logEntity = new ERPRequestLogEntity();

		try {
			LoggerAdviceRepository logAdvRepo = StaticContextHolder.getBean("LoggerAdviceRepository",
					LoggerAdviceRepository.class);
			HttpServletRequest request = (HttpServletRequest) context.get(AbstractHTTPDestination.HTTP_REQUEST);
			String reqUrl = request.getRequestURL().toString();
			SOAPMessage soapMsg = context.getMessage();
			SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
			SOAPBody soapBody = soapEnv.getBody();
			Node firstNode = getFirstChildNode(soapBody);
			Node secondNode = getFirstChildNode(firstNode);

			String operationname = soapBody.getChildNodes().item(0).getLocalName();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapMsg.writeTo(stream);
			String message = new String(stream.toByteArray(), "utf-8");
			Map<String, List<String>> headermap = (Map<String, List<String>>) context
					.get(MessageContext.HTTP_REQUEST_HEADERS);
			List<String> payloadIdlist = headermap.get("payloadId");
			List<String> checkSumlist = headermap.get("checkSum");
			List<String> erpTimeStamplist = headermap.get("erpTimeStamp");

			if (null != secondNode) {
				Node companyCodeNode = getChildNodeByName(secondNode, "cmpny-cd");
				Node sourceIdNode = getChildNodeByName(secondNode, "hdr-src-idntfr");
				if (null != companyCodeNode) {
					String companyCode = companyCodeNode.getFirstChild().getNodeValue();
					logEntity.setCompanyCode(companyCode);
				}
				if (null != sourceIdNode) {
					String sourceId = sourceIdNode.getFirstChild().getNodeValue();
					logEntity.setSourceId(sourceId);
				}
			}

			LocalDateTime curTime = LocalDateTime.now();

			if (payloadIdlist != null && !payloadIdlist.isEmpty()) {
				logEntity.setPayloadId(payloadIdlist.get(0));
			}

			if (checkSumlist != null && !checkSumlist.isEmpty()) {
				logEntity.setCheckSum(checkSumlist.get(0));
			}
			if (erpTimeStamplist != null && !erpTimeStamplist.isEmpty()) {
				String erpTimeStamp = erpTimeStamplist.get(0);
				if (erpTimeStamp != null) {
					try {
						LocalDateTime erpTime = LocalDateTime.parse(erpTimeStamp, formatter);
						logEntity.setErpTimestamp(erpTime);
					} catch (DateTimeParseException dpe) {
						LOGGER.error("Exception while Parsing the ERP Timestamp", dpe);
					}
				}
			}
			if (!message.isEmpty()) {
				logEntity.setReqPayload(message);
			}
			logEntity.setCloudTimestamp(curTime);
			logEntity.setReqUrl(reqUrl + "/" + operationname);
			logEntity.setReqType("SOAP");
			if (!Strings.isNullOrEmpty(operationname)) {
				logEntity.setApiType(BusinessCommonUtil.apiType(operationname));
			}
			logAdvRepo.save(logEntity);
			LoggerIdContext.setLoggerId(logEntity);
		} catch (Exception ex) {
			LOGGER.error("Exception while Logging the ERP Req to DB", ex);
		}

		// continue other handler chain
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return false;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public Set getHeaders() {
		return null;
	}

	private Node getChildNodeByName(Node node, String name) {

		Node returnNode = null;
		NodeList parentNodeList = node.getChildNodes();
		for (int i = 0; i < parentNodeList.getLength(); i++) {
			Node ele = (Node) parentNodeList.item(i);
			if (ele.getNodeType() == Node.ELEMENT_NODE && ele.getLocalName().equals(name)) {
				returnNode = ele;
				break;
			}
		}
		return returnNode;

	}

	private Node getFirstChildNode(Node node) {

		Node returnNode = null;
		NodeList parentNodeList = node.getChildNodes();
		for (int i = 0; i < parentNodeList.getLength(); i++) {
			Node ele = parentNodeList.item(i);
			if (ele.getNodeType() == Node.ELEMENT_NODE) {
				returnNode = ele;
				break;
			}
		}
		return returnNode;
	}

}

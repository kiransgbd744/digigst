package com.ey.advisory.controller;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ey.advisory.common.SoapHeaderPreprocessorUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupResolverHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public boolean handleMessage(SOAPMessageContext context) {

		boolean isOutboundRequest = (boolean) context
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (isOutboundRequest) {
			return true;
		}
		SOAPMessage soapMsg = context.getMessage();
		try {
			SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
			SOAPBody soapBody = soapEnv.getBody();
			Node firstNode = getFirstChildNode(soapBody);
			Node secondNode = getFirstChildNode(firstNode);
			TenantContext.clearTenant();
			if (null != secondNode) {
				String nodeValue = null;
				Node thirdNode = getChildNodeByName(secondNode, "id-token");
				if (null != thirdNode) {
					nodeValue = thirdNode.getFirstChild().getNodeValue();
					SoapHeaderPreprocessorUtil.processSoapHeader(nodeValue);
				} else {
					String errMsg = "IDToken is Mandatory for ERP requests,"
							+ " Please configure as request header";
					logXMLPayload(soapMsg);
					LOGGER.error(errMsg);
					TenantContext.setErrorMsg(errMsg);
				}
			} else {
				logXMLPayload(soapMsg);
				String errMsg = "Invalid Request Format";
				LOGGER.error(errMsg);
				TenantContext.setErrorMsg(errMsg);
			}
		} catch (Exception e) {
			logXMLPayload(soapMsg);
			LOGGER.error("Exception while parsing the Nodes", e);
			TenantContext.setErrorMsg(e.getMessage());
		}
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
	public Set<QName> getHeaders() {
		return null;
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

	private Node getChildNodeByName(Node node, String name) {

		Node returnNode = null;
		NodeList parentNodeList = node.getChildNodes();
		for (int i = 0; i < parentNodeList.getLength(); i++) {
			Node ele = parentNodeList.item(i);
			if (ele.getNodeType() == Node.ELEMENT_NODE
					&& ele.getLocalName().equals(name)) {
				returnNode = ele;
				break;
			}
		}
		return returnNode;

	}

	private void logXMLPayload(SOAPMessage soapMsg) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			soapMsg.writeTo(stream);
			String message = new String(stream.toByteArray(), "utf-8");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("XML Payload is {}", message);
		} catch (Exception ex) {
			LOGGER.error("Exception while extracting the XML Payload", ex);
		}
	}

}

/*package com.ey.advisory.app.services.jobs.erp;

import java.io.ByteArrayOutputStream;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.Gstr3bReviewHeaderSummaryRequestDto;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("Gstr3bReviewSummaryRequestDocsImpl")
public class Gstr3bReviewSummaryRequestDocsImpl
		 {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private DestinationConnectivity destinationConn;

	
	public Integer pushToErp(Gstr3bReviewHeaderSummaryRequestDto reqDto,
			String destinationName, AnxErpBatchEntity batch) {

		try {
			ByteArrayOutputStream headerOut = new ByteArrayOutputStream();
			JAXBContext headerContext = JAXBContext
					.newInstance(Gstr3bReviewHeaderSummaryRequestDto.class);
			Marshaller hdearMarshr = headerContext.createMarshaller();
			hdearMarshr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			hdearMarshr.marshal(reqDto, headerOut);
			String headerxml = headerOut.toString();

			if (headerxml != null && headerxml.length() > 0) {
				headerxml = headerxml.substring(headerxml.indexOf('\n') + 1);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Header: {}", headerxml);
			}
			StringBuilder header = new StringBuilder();
			header.append("<?xml version='1.0' encoding='UTF-8'?>");
			header.append(
					"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
			header.append(
					"xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'>");
			header.append("<soapenv:Header/>");
			header.append("<soapenv:Body>");
			header.append("<urn:_-digigst_-gstr3bSummary>");

			StringBuilder footer = new StringBuilder();
			footer.append(
					" </urn:_-digigst_-gstr3bSummary></soapenv:Body></soapenv:Envelope>");
			String xml = null;
			if (headerxml != null) {
				xml = header + headerxml + footer;
			}

			if (xml != null && destinationName != null) {
				return destinationConn.post(destinationName, xml, batch);
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return null;
	}

}
*/
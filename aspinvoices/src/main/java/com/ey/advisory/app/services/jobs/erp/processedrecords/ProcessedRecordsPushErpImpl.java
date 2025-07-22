package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.io.ByteArrayOutputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.common.DestinationConnectivity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ProcessedRecordsPushErpImpl")
public class ProcessedRecordsPushErpImpl implements ProcessedRecordsPushErp {
	
	@Autowired
	private DestinationConnectivity destinationConn;

	@Override
	public Integer pushToErp(Anx1ProcessedRevRecordsDto dto,
			String destinationName, AnxErpBatchEntity batch) {

		try {
			String xml = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext
					.newInstance(Anx1ProcessedRevRecordsDto.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dto, out);
			xml = out.toString();

			if (xml != null && xml.length() > 0) {
				xml = xml.substring(xml.indexOf('\n') + 1);
			}

			String header = "<soapenv:Envelope xmlns:soapenv="
					+ "\"http://schemas.xmlsoap.org/soap/envelope/\""
					+ " xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">"
					+ "<soapenv:Header/>"
					+ "<soapenv:Body>"
					+ "<urn:ZGST_ANX_1_PROCESS_SUM>";
			
			
			String footer = "</urn:ZGST_ANX_1_PROCESS_SUM></soapenv:Body>"
					           +"</soapenv:Envelope>";
			
			//final payload using header and footer.
			if (xml != null) {
				xml = header + xml + footer;
			}

			if (xml != null && destinationName != null) {
				//return destinationConn.post(destinationName, xml, batch);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

}

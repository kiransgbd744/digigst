package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Gstr1ApprovalItemRequestDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ApprovalRequestHeaderDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service("Gstr1ApprovalRequestDocsImpl")
public class Gstr1ApprovalRequestDocsImpl implements Gstr1ApprovalRequestDocs {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ApprovalRequestDocsImpl.class);
	public static final String Empty = "";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Override
	public Gstr1ApprovalRequestHeaderDto convertDocsAsHeaderDtos(String retPer,
			Long gstinId, String gstinName, String entityName,
			String companyCode) {

		Gstr1ApprovalRequestHeaderDto headerDto = new Gstr1ApprovalRequestHeaderDto();
		List<Gstr1ApprovalItemRequestDto> items = new ArrayList<>();
		Gstr1ApprovalItemRequestDto dto = new Gstr1ApprovalItemRequestDto();
		dto.setAppRejDate(String
				.valueOf(EYDateUtil.toISTDateTimeFromUTC(LocalDate.now())));
		// To avoid the milli seconds
		dto.setAppRejTime(String.valueOf(LocalTime.now()).substring(0, 8));
		dto.setReqstDate(String
				.valueOf(EYDateUtil.toISTDateTimeFromUTC(LocalDate.now())));
		// To avoid the milli seconds
		dto.setReqstTime(String.valueOf(LocalTime.now()).substring(0, 8));
		dto.setRetPer(retPer);
		dto.setId(gstinId);
		dto.setGstinNum(gstinName);
		dto.setEntity(entityName);
		dto.setCompanyCode(companyCode);
		dto.setAppRejUser(APIConstants.SYSTEM);
		items.add(dto);

		headerDto.setItems(items);
		return headerDto;
	}

	/*@Override
	public Integer pushToErp(Gstr1ApprovalRequestHeaderDto headerDto,
			Gstr1ApprovalRequestDto dto, String destinationName,
			AnxErpBatchEntity batch) {

		try {
			ByteArrayOutputStream headerOut = new ByteArrayOutputStream();
			JAXBContext headerContext = JAXBContext
					.newInstance(Gstr1ApprovalRequestHeaderDto.class);
			Marshaller headerMarsr = headerContext.createMarshaller();
			headerMarsr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			headerMarsr.marshal(headerDto, headerOut);
			String headerXml = headerOut.toString();

			if (headerXml != null && headerXml.length() > 0) {
				headerXml = headerXml.substring(headerXml.indexOf('\n') + 1);
			}

			ByteArrayOutputStream itemOut = new ByteArrayOutputStream();
			JAXBContext itemContext = JAXBContext
					.newInstance(Gstr1ApprovalRequestDto.class);
			Marshaller itemMarshr = itemContext.createMarshaller();
			itemMarshr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			itemMarshr.marshal(dto, itemOut);
			String itemXml = itemOut.toString();

			if (itemXml != null && itemXml.length() > 0) {
				itemXml = itemXml.substring(itemXml.indexOf('\n') + 1);
			}

			String xml = headerXml.concat(itemXml);
			StringBuilder header = new StringBuilder();
			header.append(
					"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
			header.append(
					"xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'>");
			header.append("<soapenv:Header/>");
			header.append("<soapenv:Body>");
			header.append("<urn:_-digigst_-gstr1WfSave>");
			String footer = "</urn:_-digigst_-gstr1WfSave></soapenv:Body></soapenv:Envelope>";
			if (xml != null) {
				xml = header + xml + footer;
			}

			if (xml != null && destinationName != null) {
				// return destinationConn.post(destinationName, xml, batch);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}*/
}

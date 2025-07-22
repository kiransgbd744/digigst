/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Anx1ApprovalRequestHeaderDto;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1ApprovalRequestDocsImpl")
public class Anx1ApprovalRequestDocsImpl implements Anx1ApprovalRequestDocs {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApprovalRequestDocsImpl.class);

	public static final String Empty = "";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Anx1ApprovalRequestHeaderDto convertDocsAsHeaderDtos(String retPer,
			String gstinNum, String entityName, String pan,
			String companyCode) {

		Anx1ApprovalRequestHeaderDto dto = new Anx1ApprovalRequestHeaderDto();
		dto.setAppRejDate(String.valueOf(LocalDate.now()));
		// To avoid the milli seconds
		dto.setAppRejTime(String.valueOf(LocalTime.now()).substring(0, 8));
		dto.setReqstDate(String.valueOf(LocalDate.now()));
		// To avoid the milli seconds
		dto.setReqstTime(String.valueOf(LocalTime.now()).substring(0, 8));
		dto.setRetPer(retPer);
		dto.setGstinNum(gstinNum);
		dto.setEntity(entityName);
		dto.setEntityId(pan);
		dto.setCompanyCode(companyCode);
		dto.setAppRejUser(APIConstants.SYSTEM);

		return dto;
	}

	

	/*@Override
	public Integer pushToErp(Anx1ApprovalRequestHeaderDto headerDto,
			Anx1ReviewSummaryRequestDto reqDto, String destinationName,
			AnxErpBatchEntity batch) {

		try {
			ByteArrayOutputStream headerOut = new ByteArrayOutputStream();
			JAXBContext headerContext = JAXBContext
					.newInstance(Anx1ApprovalRequestHeaderDto.class);
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
					.newInstance(Anx1ReviewSummaryRequestDto.class);
			Marshaller itemMarshr = itemContext.createMarshaller();
			itemMarshr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			itemMarshr.marshal(reqDto, itemOut);
			String itemXml = itemOut.toString();

			if (itemXml != null && itemXml.length() > 0) {
				itemXml = itemXml.substring(itemXml.indexOf('\n') + 1);
			}

			String xml = headerXml.concat(itemXml);
			String header = ERPConstants.APPROVAL_REQUEST_HEADER;
			String footer = ERPConstants.APPROVAL_REQUEST_FOOTER;
			if (xml != null) {
				xml = header + xml + footer;
			}
			if (xml != null && destinationName != null) {
				return destinationConn.post(destinationName, xml, batch);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}*/
}

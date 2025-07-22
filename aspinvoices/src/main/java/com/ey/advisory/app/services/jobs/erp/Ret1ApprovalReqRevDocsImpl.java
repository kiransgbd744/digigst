package com.ey.advisory.app.services.jobs.erp;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.data.daos.client.simplified.Ret1BasicDocSummarySectionDaoImpl;
import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ApprovalItemRequestDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ApprovalRequestDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ApprovalRequestHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ReviewSummaryRequestItemDto;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

@Service("Ret1ApprovalReqRevDocsImpl")
public class Ret1ApprovalReqRevDocsImpl implements Ret1ApprovalReqRevDocs {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1ApprovalReqRevDocsImpl.class);

	@Autowired
	private DestinationConnectivity destinationConn;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Ret1BasicDocSummarySectionDaoImpl")
	private Ret1BasicDocSummarySectionDaoImpl ret1BasicDocSummarySectionDao;

	@Override
	public Ret1ApprovalRequestHeaderDto convertDocsAsHeaderDtos(String retPer,
			Map<Long, String> gstinNums, String entityName,
			String companyCode) {
		Ret1ApprovalRequestHeaderDto headerDto = new Ret1ApprovalRequestHeaderDto();
		List<Ret1ApprovalItemRequestDto> items = new ArrayList<>();
		gstinNums.forEach((id, gstin) -> {
			Ret1ApprovalItemRequestDto reqDto = new Ret1ApprovalItemRequestDto();
			reqDto.setEntity(entityName);
			reqDto.setCompanyCode(companyCode);
			reqDto.setId(id);
			reqDto.setGstinNum(gstin);
			reqDto.setAppRejDate(String.valueOf(LocalDate.now()));
			// To avoid the milli seconds
			reqDto.setAppRejTime(
					String.valueOf(LocalDate.now()).substring(0, 8));
			reqDto.setAppRejDate(String.valueOf(LocalDate.now()));

			// To Avoid The milli seconds
			reqDto.setReqstTime(
					String.valueOf(LocalDate.now()).substring(0, 8));
			reqDto.setAppRejUser(APIConstants.SYSTEM);
			items.add(reqDto);
		});
		headerDto.setItems(items);
		return headerDto;
	}

	public Ret1ApprovalRequestDto convertObjToDtos(String entityName,
			String entityPan, String companyCode,
			List<Ret1SummarySectionDto> ret1BasicDoc) {
		Ret1ApprovalRequestDto reqDto = new Ret1ApprovalRequestDto();
		List<Ret1ReviewSummaryRequestItemDto> itemDtos = new ArrayList<>();
		ret1BasicDoc.forEach(ret1Doc -> {
			Ret1ReviewSummaryRequestItemDto itemDto = new Ret1ReviewSummaryRequestItemDto();
			itemDto.setEntity(entityName);
			itemDto.setEntityPan(entityPan);
			itemDto.setCompanyCode(companyCode);
			itemDto.setSupplyType(ret1Doc.getSupplyType());

			itemDto.setRetPer(ret1Doc.getReturnPeriod());
			itemDto.setGstinNum(ret1Doc.getSgstn());
			itemDto.setTable(ret1Doc.getTable());
			itemDto.setAspIgst(ret1Doc.getAspIgst());
			itemDto.setAspCgst(ret1Doc.getAspCgst());
			itemDto.setAspSgst(ret1Doc.getAspSgst());
			itemDto.setAspCess(ret1Doc.getAspCess());

			itemDto.setGstinIgst(ret1Doc.getGstnIgst());
			itemDto.setGstinCgst(ret1Doc.getGstnCgst());
			itemDto.setGstinSgst(ret1Doc.getGstnSgst());
			itemDto.setGstinCess(ret1Doc.getGstnCess());

			itemDto.setDiffIgst(
					itemDto.getAspIgst().subtract(itemDto.getGstinIgst()));
			itemDto.setDiffCgst(
					itemDto.getAspCgst().subtract(itemDto.getGstinCgst()));
			itemDto.setDiffSgst(
					itemDto.getAspSgst().subtract(itemDto.getGstinSgst()));
			itemDto.setDiffCess(
					itemDto.getAspCess().subtract(itemDto.getGstinCess()));
			itemDtos.add(itemDto);
		});
		reqDto.setItemDto(itemDtos);
		return reqDto;
	}

	@Override
	public Integer pushToErp(Ret1ApprovalRequestHeaderDto headerDto,
			Ret1ApprovalRequestDto itemDto, String destName, AnxErpBatchEntity batch) {
		try {
			ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
			JAXBContext headerContext = JAXBContext
					.newInstance(Ret1ApprovalRequestHeaderDto.class);
			Marshaller headerMarshaller = headerContext.createMarshaller();
			headerMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			headerMarshaller.marshal(headerMarshaller, headerStream);

			String headerXml = headerStream.toString();
			if (headerXml != null && headerXml.length() > 0) {
				headerXml = headerXml.substring(headerXml.indexOf('\n' + 1));
			}
			ByteArrayOutputStream itemStream = new ByteArrayOutputStream();
			JAXBContext itemContext = JAXBContext
					.newInstance(Ret1ApprovalRequestDto.class);
			Marshaller itemMarshaller = itemContext.createMarshaller();
			itemMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			itemMarshaller.marshal(itemMarshaller, itemStream);

			String itemXml = itemStream.toString();

			if (itemXml != null && itemXml.length() > 0) {
				itemXml = itemXml.substring(itemXml.indexOf('\n' + 1));
			}
			String xml = headerXml.concat(itemXml);
			String header = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
					+ "xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'><soapenv:Header/>"
					+ "<soapenv:Body><urn:ZwfRet1save>";
			String footer = "</urn:ZwfRet1save></soapenv:Body></soapenv:Envelope>";

			if (xml != null) {
				xml = header + xml + footer;
			}
			if (xml != null && destName != null) {
				//return destinationConn.post(destName, xml, batch);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return null;
	}
}

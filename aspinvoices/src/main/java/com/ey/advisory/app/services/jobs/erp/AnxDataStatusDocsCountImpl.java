/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestItemDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusResultDto;
import com.ey.advisory.common.DestinationConnectivity;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
@Service("AnxDataStatusDocsCountImpl")
public class AnxDataStatusDocsCountImpl implements AnxDataStatusDocsCount {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AnxDataStatusDocsCountImpl.class);

	@Autowired
	private DestinationConnectivity destinationConn;

	@Override
	public AnxDataStatusRequestDataHeaderDto convertDocsAsDtos(
			List<AnxDataStatusResultDto> results, String dataType) {

		List<AnxDataStatusRequestItemDto> itemList = new ArrayList<>();

		results.forEach(result -> {

			AnxDataStatusRequestItemDto item = new AnxDataStatusRequestItemDto();

			LocalDate recievedDate = result.getReceivedDate();
			Integer aspTotal = result.getTotalRecords();
			Integer activeProcess = result.getProcessedActive();
			Integer inactiveProcess = result.getProcessedInactive();
			Integer activeError = result.getErrorActive();
			Integer inactiveError = result.getErrorInactive();
			Integer activeInfo = result.getInfoActive();
			Integer inactiveInfo = result.getInfoInactive();

			String gstin = result.getGstin();
			String retPeriod = result.getRetPeriod();
			String division = result.getDivision();
			String profitCentre = result.getProfitCentre();
			String location = result.getLocation();
			String plantcode = result.getPlantcode();
			String salesOrg = result.getSalesOrg();
			String purchaseOrg = result.getPurchaseOrg();
			String distributionChannel = result.getDistributionChannel();
			String userAccess1 = result.getUserAccess1();
			String userAccess2 = result.getUserAccess2();
			String userAccess3 = result.getUserAccess3();
			String userAccess4 = result.getUserAccess4();
			String userAccess5 = result.getUserAccess5();
			String userAccess6 = result.getUserAccess6();

			String entity = result.getEntity();
			String entityName = result.getEntityName();
			String companyCode = result.getCompanyCode();

			item.setDate(
					recievedDate != null ? String.valueOf(recievedDate) : null);
			item.setAspTotal(aspTotal);
			item.setCompanyCode(companyCode != null ? companyCode : "");
			item.setActiveProcess(activeProcess);
			item.setInactiveProcess(inactiveProcess);
			item.setActiveError(activeError);
			item.setInactiveError(inactiveError);
			item.setActiveInfo(activeInfo);
			item.setInactiveInfo(inactiveInfo);
			item.setDatatype(
					dataType != null ? String.valueOf(dataType) : null);
			item.setGstin(gstin != null ? String.valueOf(gstin) : null);
			item.setRetPeriod(
					retPeriod != null ? String.valueOf(retPeriod) : null);
			item.setDivision(
					division != null ? String.valueOf(division) : null);
			item.setProfitCentre(
					profitCentre != null ? String.valueOf(profitCentre) : null);
			item.setLocation(
					location != null ? String.valueOf(location) : null);
			item.setPlantcode(
					plantcode != null ? String.valueOf(plantcode) : null);
			item.setSalesOrg(
					salesOrg != null ? String.valueOf(salesOrg) : null);
			item.setPurchaseOrg(
					purchaseOrg != null ? String.valueOf(purchaseOrg) : null);
			item.setDistributionChannel(distributionChannel != null
					? String.valueOf(distributionChannel) : null);
			item.setUserAccess1(
					userAccess1 != null ? String.valueOf(userAccess1) : null);
			item.setUserAccess2(
					userAccess2 != null ? String.valueOf(userAccess2) : null);
			item.setUserAccess3(
					userAccess3 != null ? String.valueOf(userAccess3) : null);
			item.setUserAccess4(
					userAccess4 != null ? String.valueOf(userAccess4) : null);
			item.setUserAccess5(
					userAccess5 != null ? String.valueOf(userAccess5) : null);
			item.setUserAccess6(
					userAccess6 != null ? String.valueOf(userAccess6) : null);
			item.setEntity(entity != null ? String.valueOf(entity) : null);
			item.setEntityName(
					entityName != null ? String.valueOf(entityName) : null);
			itemList.add(item);
		});

		AnxDataStatusRequestDataHeaderDto dto = new AnxDataStatusRequestDataHeaderDto();

		dto.setImItem(itemList);

		return dto;

	}

	@Override
	public Integer pushToErp(AnxDataStatusRequestHeaderDto dto,
			String destinationName, AnxErpBatchEntity batch) {

		try {
			String xml = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext
					.newInstance(AnxDataStatusRequestHeaderDto.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dto, out);
			xml = out.toString();
			if (xml != null && xml.length() > 0) {
				xml = xml.substring(xml.indexOf('\n') + 1);
			}

			// String header = "<soapenv:Envelope
			// xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'
			// xmlns:urn='urn:sap-com:document:sap:rfc:functions'><soapenv:Header/><soapenv:Body><urn:ZDATA_STATUS>";
			String header = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:rfc:functions'><soapenv:Header/><soapenv:Body>";
			String footer = "</soapenv:Body></soapenv:Envelope>";
			// final payload using header and footer.
			if (xml != null) {
				xml = header + xml + footer;
			}
			if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("xml data-------------------------->" + xml);
			}
			if (xml != null && destinationName != null) {
				//return destinationConn.post(destinationName, xml, batch);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("destinationName with xml ", xml, destinationName);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

}

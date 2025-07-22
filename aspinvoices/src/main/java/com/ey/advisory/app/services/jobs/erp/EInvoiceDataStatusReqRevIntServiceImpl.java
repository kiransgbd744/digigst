package com.ey.advisory.app.services.jobs.erp;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.data.daos.client.EInvoiceDataStatusSearchScreenDaoImpl;
import com.ey.advisory.app.data.entities.client.Anx1NewDataStatusEntity;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusReqDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataSummaryHeaderDto;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataHeaderDto;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataSummaryDto;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.EinvoiceDataStatusRequestItemDto;
import com.ey.advisory.app.services.search.apisummarysearch.DataStatusApiSummaryDaoImpl;
import com.ey.advisory.app.services.search.apisummarysearch.DataStatusApiSummaryService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.DataStatusApiSummaryReqDto;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

@Service("EInvoiceDataStatusReqRevIntServiceImpl")
public class EInvoiceDataStatusReqRevIntServiceImpl implements EInvoiceDataStatusReqRevIntService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EInvoiceDataStatusReqRevIntServiceImpl.class);

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	@Qualifier("EInvoiceDataStatusSearchScreenDaoImpl")
	private EInvoiceDataStatusSearchScreenDaoImpl eInvoiceDataStatusSearchScreenDao;

	@Autowired
	@Qualifier("DataStatusApiSummaryDaoImpl")
	private DataStatusApiSummaryDaoImpl dataStatusApiSumDao;

	@Autowired
	@Qualifier("DataStatusApiSummaryService")
	private DataStatusApiSummaryService dataStatusApiSummaryService;

	@Autowired
	@Qualifier("AnxDataStatusDataFetcherForErpImpl")
	private AnxDataStatusDataFetcherForErp anx1DataStatusService;

	public List<EinvoiceDataStatusRequestItemDto> getDataStatusProcessData(String companyCode, String entityName,
			Long entityId, String panNumber, String dataType, String gstin,
			List<EinvoiceDataStatusRequestItemDto> itemDtos) {

		DataStatusApiSummaryReqDto dataStatusApiSummaryReqDto = new DataStatusApiSummaryReqDto();
		dataStatusApiSummaryReqDto.setDataType(dataType);

		LocalDate dataRecvFrom = LocalDate.now();
		LocalDate dataRecvTo = LocalDate.now();

		AnxDataStatusReqDto searchParams = new AnxDataStatusReqDto();
		searchParams.setDataRecvFrom(dataRecvFrom);
		searchParams.setDataRecvTo(dataRecvTo);
		searchParams.setGstin(gstin);
		searchParams.setDataType(dataType);

		List<Object[]> outSumArrs = anx1DataStatusService.findDataStatusApiSummary(searchParams);
		if (APIConstants.OUTWARD_SUMMARY.equalsIgnoreCase(dataType)) {
			AnxDataStatusRequestDataSummaryHeaderDto dataSummaryHeader = anx1DataStatusService
					.calculateDataByDocTypeAndReturnPeiod(anx1DataStatusService.convertDataToOutwarSummary(panNumber,
							entityName, outSumArrs, entityId, companyCode), entityId);
			dataSummaryHeader.getImItem().forEach(dataStatusApiSum -> {
				EinvoiceDataStatusRequestItemDto itemDto = new EinvoiceDataStatusRequestItemDto();
				itemDto.setEntity(panNumber);
				itemDto.setEntityName(entityName);
				itemDto.setCompanyCode(companyCode);
				itemDto.setDataType("OUTWARD");
				itemDto.setRetPer(dataStatusApiSum.getRetPeriod());
				itemDto.setGstinNum(gstin);
				itemDto.setPushedDt(dataStatusApiSum.getDate());
				itemDto.setTCount(dataStatusApiSum.getTCount() != null
						? new BigInteger(String.valueOf(dataStatusApiSum.getTCount())) : BigInteger.ZERO);
				itemDto.setRetType(dataStatusApiSum.getRetType());
				itemDto.setRetSection(dataStatusApiSum.getRetSection());
				itemDto.setTaxableValue(dataStatusApiSum.getTaxableValue());
				itemDto.setTotalTax(dataStatusApiSum.getTotalTax());
				itemDto.setIgst(dataStatusApiSum.getIgst());
				itemDto.setSgst(dataStatusApiSum.getSgst());
				itemDto.setCgst(dataStatusApiSum.getCgst());
				itemDto.setCess(dataStatusApiSum.getCess());
				itemDtos.add(itemDto);
			});
		} else if (APIConstants.INWARD_SUMMARY.equalsIgnoreCase(dataType)) {
			AnxDataStatusRequestDataSummaryHeaderDto dataSummaryHeader = anx1DataStatusService
					.calculateDataByDocTypeAndReturnPeiod(anx1DataStatusService.convertDataToOutwarSummary(panNumber,
							entityName, outSumArrs, entityId, companyCode), entityId);
			dataSummaryHeader.getImItem().forEach(dataStatusApiSum -> {
				EinvoiceDataStatusRequestItemDto itemDto = new EinvoiceDataStatusRequestItemDto();
				itemDto.setEntity(panNumber);
				itemDto.setEntityName(entityName);
				itemDto.setCompanyCode(companyCode);
				itemDto.setDataType("INWARD");
				itemDto.setRetPer(dataStatusApiSum.getRetPeriod());
				itemDto.setGstinNum(gstin);
				itemDto.setRetType(dataStatusApiSum.getRetType());
				itemDto.setRetSection(dataStatusApiSum.getRetSection());
				itemDto.setPushedDt(dataStatusApiSum.getDate());
				itemDto.setTCount(dataStatusApiSum.getTCount() != null
						? new BigInteger(String.valueOf(dataStatusApiSum.getTCount())) : BigInteger.ZERO);
				itemDto.setTaxableValue(dataStatusApiSum.getTaxableValue());
				itemDto.setTotalTax(dataStatusApiSum.getTotalTax());
				itemDto.setIgst(dataStatusApiSum.getIgst());
				itemDto.setSgst(dataStatusApiSum.getSgst());
				itemDto.setCgst(dataStatusApiSum.getCgst());
				itemDto.setCess(dataStatusApiSum.getCess());
				itemDtos.add(itemDto);
			});
		}
		return itemDtos;
	}

	public void eInvoiceDataStatus(String companyCode, String entityName, String panNumber, String gstin,
			String dataType, List<EInvoiceDataStatusRequestDataSummaryItemDto> dataSummItemDtos) {
		DataStatusSearchReqDto reqDto = new DataStatusSearchReqDto(null);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		reqDto.setDataSecAttrs(dataSecAttrs);
		StringBuilder build = new StringBuilder();
		// Object dataRecvFrom = LocalDate.now();
		// Object dataRecvTo = LocalDate.now();
		if ("outward".equalsIgnoreCase(dataType)) {
			if (!gstins.isEmpty()) {
				build.append(" HDR.SUPPLIER_GSTIN IN :gstinList");
			}
		} else {
			if (!gstins.isEmpty()) {
				build.append(" HDR.CUST_GSTIN  IN :gstinList");

			}
		}
		/*
		 * if (dataRecvFrom != null && dataRecvTo != null) {
		 * build.append(" AND RECEIVED_DATE BETWEEN :recivedFromDate " +
		 * "AND :recivedToDate"); }
		 */
		List<Anx1NewDataStatusEntity> dataStatusEntities = eInvoiceDataStatusSearchScreenDao
				.dataAnx1NewStatusSection(null, reqDto, build.toString(), dataType, null, null);

		dataStatusEntities.forEach(dataStatusEntity -> {
			EInvoiceDataStatusRequestDataSummaryItemDto itemDto = new EInvoiceDataStatusRequestDataSummaryItemDto();
			itemDto.setCompanyCode(companyCode);
			itemDto.setGstinNum(gstin);
			itemDto.setRetPer(String.valueOf(dataStatusEntity.getDerivedRetPeriod()));

			itemDto.setDataType(dataType);
			itemDto.setEntity(panNumber);
			itemDto.setEntityName(entityName);
			
			itemDto.setReceivedDate(String.valueOf(dataStatusEntity.getReceivedDate()));
			
			itemDto.setTotalRecords(dataStatusEntity.getTotalRecords());

			itemDto.setProcessedActive(dataStatusEntity.getProcessedActive());
			itemDto.setProcessedInactive(dataStatusEntity.getProcessedInactive());
			itemDto.setErrorActive(dataStatusEntity.getErrorActive());
			itemDto.setErrorInactive(dataStatusEntity.getErrorInactive());

			itemDto.setEinvNotApplicable(dataStatusEntity.getInvNotApplicable());
			itemDto.setEinvErrorsDigigst(dataStatusEntity.getInvAspError());
			itemDto.setEinvAspProcess(dataStatusEntity.getInvAspProcessed());
			itemDto.setEinvErrorsIrp(dataStatusEntity.getInvIrnInError());
			itemDto.setEinvInrInitated(dataStatusEntity.getInvIrnInProgress());
			itemDto.setEinvInrGenerated(dataStatusEntity.getInvIrnProcessed());
			itemDto.setEinvCancelled(dataStatusEntity.getInvIrnCancelled());
			itemDto.setEinvInfoError(dataStatusEntity.getEinvVInfoError());
			itemDto.setEinvNotOpted(dataStatusEntity.getEinvNotOpted());

			itemDto.setEwbNotApplicable(dataStatusEntity.getEwbNotApplicable());
			itemDto.setEwbErrorDigigst(dataStatusEntity.getEwbAspError());
			itemDto.setEwbAspProcess(dataStatusEntity.getEwbAspProcessed());
			itemDto.setEwbErrorNic(dataStatusEntity.getEwbNicError());
			itemDto.setEwbInitated(dataStatusEntity.getEwbGenInProgress());
			itemDto.setEwbGenerated(dataStatusEntity.getEwbPartAGenerated());
			itemDto.setEwbCancelled(dataStatusEntity.getEwbCancelled());
			itemDto.setEwbInitated(dataStatusEntity.getEwbGenInProgress());
			itemDto.setEwbNotGeneratedOnErp(dataStatusEntity.getEwbNotGeneratedOnErp());

			itemDto.setAspNA(dataStatusEntity.getAspNA());
			itemDto.setAspProcess(dataStatusEntity.getAspProcess());
			itemDto.setAspErrorsGstin(dataStatusEntity.getAspErrorsGstin());
			itemDto.setAspSavedGstin(dataStatusEntity.getAspSavedGstin());
			itemDto.setAspSaveInitiated(dataStatusEntity.getAspSaveInitiated());
			itemDto.setAspError(dataStatusEntity.getAspError());

			dataSummItemDtos.add(itemDto);
		});
	}

	public Integer pushToErp(EInvoiceDataStatusRequestDataHeaderDto headerDto,
			EInvoiceDataStatusRequestDataSummaryDto sumDto, String destName, AnxErpBatchEntity batch) {
		try {
			ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
			JAXBContext headerContext = JAXBContext.newInstance(EInvoiceDataStatusRequestDataHeaderDto.class);
			Marshaller headerMarshal = headerContext.createMarshaller();
			headerMarshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			headerMarshal.marshal(headerDto, headerStream);
			String headerXml = headerStream.toString();

			if (headerXml != null && headerXml.length() > 0) {
				headerXml = headerXml.substring(headerXml.indexOf('\n') + 1);
			}

			ByteArrayOutputStream summaryStream = new ByteArrayOutputStream();
			JAXBContext summaryContext = JAXBContext.newInstance(EInvoiceDataStatusRequestDataSummaryDto.class);
			Marshaller summaryMarshal = summaryContext.createMarshaller();
			summaryMarshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			summaryMarshal.marshal(sumDto, summaryStream);
			String summaryXml = summaryStream.toString();
			if (summaryXml != null && summaryXml.length() > 0) {
				summaryXml = summaryXml.substring(summaryXml.indexOf('\n') + 1);
			}

			StringBuilder header = new StringBuilder();
			header.append("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
			header.append("xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'>");
			header.append("<soapenv:Header/>");
			header.append("<soapenv:Body>");
			header.append("<urn:_-digigst_-dataStatus>");

			StringBuilder footer = new StringBuilder();

			footer.append("</urn:_-digigst_-dataStatus>");
			footer.append("</soapenv:Body>");
			footer.append("</soapenv:Envelope>");
			String xml = null;
			if (headerXml != null && summaryXml != null && !headerXml.isEmpty() && !summaryXml.isEmpty()) {
				xml = header + headerXml + summaryXml + footer;
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

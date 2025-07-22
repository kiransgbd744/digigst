package com.ey.advisory.app.services.jobs.erp;

import java.io.ByteArrayOutputStream;
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
import com.ey.advisory.app.data.daos.client.Ret1ProcessedRecordsDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.Ret1BasicDocSummarySectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.Ret1Int6BasicDocSummarySectionDao;
import com.ey.advisory.app.data.daos.client.simplified.Ret1InteLateFeeBasicDocSummarySectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.Ret1Payment7BasicDocSummarySectionDao;
import com.ey.advisory.app.data.daos.client.simplified.Ret1Refund8SummarySectionDaoImpl;
import com.ey.advisory.app.docs.dto.Ret1LateFeeSummarySectionDto;
import com.ey.advisory.app.docs.dto.Ret1PaymentSummarySectionDto;
import com.ey.advisory.app.docs.dto.Ret1RefundSummarySectionDto;
import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
import com.ey.advisory.app.docs.dto.erp.Ret1HeaderItemProcessReviewSummaryReqDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ReviewSummaryRequestItemDto;
import com.ey.advisory.app.services.ret1.Ret1ProcessedRecordsResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Ret1ReviewSummaryRequestDocsImpl")
public class Ret1ReviewSummaryRequestDocsImpl
		implements Ret1ReviewSummaryRequestDocs {

	Logger LOGGER = LoggerFactory
			.getLogger(Ret1ReviewSummaryRequestDocsImpl.class);

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	@Qualifier("Ret1ProcessedRecordsDaoImpl")
	private Ret1ProcessedRecordsDaoImpl ret1ProcessedRecordsDao;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Ret1BasicDocSummarySectionDaoImpl")
	private Ret1BasicDocSummarySectionDaoImpl ret1BasDocSummSecDao;

	@Autowired
	@Qualifier("Ret1Int_6_BasicDocSummarySectionDaoImpl")
	private Ret1Int6BasicDocSummarySectionDao basic6DocSummarySectionDao;

	@Autowired
	@Qualifier("Ret1Payment_7_BasicDocSummarySectionDaoImpl")
	private Ret1Payment7BasicDocSummarySectionDao basic7DocSummarySectionDao;

	@Autowired
	@Qualifier("Ret1Refund8SummarySectionDaoImpl")
	private Ret1Refund8SummarySectionDaoImpl ref8SumSecDao;

	@Autowired
	@Qualifier("Ret1InteLateFeeBasicDocSummarySectionDaoImpl")
	private Ret1InteLateFeeBasicDocSummarySectionDaoImpl lateFeeBasicDocSummSecDao;

	/*
	 * Ret1 Process Summary Conversion
	 * 
	 */
	@Override
	public List<Ret1ReviewSummaryRequestItemDto> itemProcessSummary(
			String gstin, String entityPan, String stateName, String entityName,
			String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> processData) {

		List<Ret1ReviewSummaryRequestItemDto> listOfObjects = new ArrayList<>();
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);

		List<Ret1ProcessedRecordsResponseDto> ret1ProcessedRecords = ret1ProcessedRecordsDao
				.fetchRet1ProcessedRecords(gstins, null);
		for (Ret1ProcessedRecordsResponseDto ret1ProcessedRecord : ret1ProcessedRecords) {
			Ret1ReviewSummaryRequestItemDto child = new Ret1ReviewSummaryRequestItemDto();
			child.setGstinNum(ret1ProcessedRecord.getGstin());
			child.setRetPer(ret1ProcessedRecord.getRetPeriod());
			child.setEyStatus(ret1ProcessedRecord.getStatus());
			child.setTotalTaxLiability(ret1ProcessedRecord.getLiability());
			child.setRevCharge(ret1ProcessedRecord.getRevCharge());
			child.setOthRevCharge(ret1ProcessedRecord.getOtherCharge());
			child.setNetAvaiItc(ret1ProcessedRecord.getItc());
			child.setTds(ret1ProcessedRecord.getTds());
			child.setTcs(ret1ProcessedRecord.getTcs());
			listOfObjects.add(child);
		}
		return listOfObjects;
	}

	/*
	 * Ret1 Review Summary Conversions for All Sections
	 * 
	 */
	public void getReviewSummary(String gstinNum, String retPeriod,
			String entityPan, String entityName, String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos) {
		outwardSupplyReviewSummary(gstinNum, retPeriod, entityPan, entityName,
				companyCode, summReqItemDtos);
		basic6DocReviewSummary(gstinNum, retPeriod, entityPan, entityName,
				companyCode, summReqItemDtos);
		basic7DocReviewSummary(gstinNum, retPeriod, entityPan, entityName,
				companyCode, summReqItemDtos);
		lateFeeReviewSummary(gstinNum, retPeriod, entityPan, entityName,
				companyCode, summReqItemDtos);
		ref8SumSecReviewSummary(gstinNum, retPeriod, entityPan, entityName,
				companyCode, summReqItemDtos);
	}

	private void outwardSupplyReviewSummary(String gstinNum, String retPeriod,
			String entityPan, String entityName, String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos) {
		Annexure1SummaryReqDto anx1SumReqDto = new Annexure1SummaryReqDto();
		anx1SumReqDto.setTaxPeriod(retPeriod);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstinNum);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		anx1SumReqDto.setDataSecAttrs(dataSecAttrs);
		List<Ret1SummarySectionDto> ret1SummSecDtos = ret1BasDocSummSecDao
				.loadBasicSummarySection(anx1SumReqDto);
		ret1SummSecDtos.forEach(ret1SummSecDto -> {
			Ret1ReviewSummaryRequestItemDto itemDto = new Ret1ReviewSummaryRequestItemDto();
			itemDto.setEntity(entityName);
			itemDto.setEntityPan(entityPan);
			itemDto.setCompanyCode(companyCode);
			itemDto.setSupplyType(ret1SummSecDto.getSupplyType());
			itemDto.setRetPer(retPeriod);
			itemDto.setGstinNum(gstinNum);
			itemDto.setTable(ret1SummSecDto.getTable());
			itemDto.setAspIgst(ret1SummSecDto.getAspIgst());
			itemDto.setAspCgst(ret1SummSecDto.getAspCgst());
			itemDto.setAspSgst(ret1SummSecDto.getAspSgst());
			itemDto.setAspCess(ret1SummSecDto.getAspCess());

			itemDto.setGstinIgst(ret1SummSecDto.getGstnIgst());
			itemDto.setGstinCgst(ret1SummSecDto.getGstnCgst());
			itemDto.setGstinSgst(ret1SummSecDto.getGstnSgst());
			itemDto.setGstinCess(ret1SummSecDto.getGstnCess());

			itemDto.setDiffIgst(
					itemDto.getAspIgst().subtract(itemDto.getGstinIgst()));
			itemDto.setDiffCgst(
					itemDto.getAspCgst().subtract(itemDto.getGstinCgst()));
			itemDto.setDiffSgst(
					itemDto.getAspSgst().subtract(itemDto.getGstinSgst()));
			itemDto.setDiffCess(
					itemDto.getAspCess().subtract(itemDto.getGstinCess()));
			summReqItemDtos.add(itemDto);
		});
	}

	private void basic6DocReviewSummary(String gstinNum, String retPeriod,
			String entityPan, String entityName, String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos) {
		Annexure1SummaryReqDto anx1SumReqDto = new Annexure1SummaryReqDto();
		anx1SumReqDto.setTaxPeriod(retPeriod);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstinNum);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		anx1SumReqDto.setDataSecAttrs(dataSecAttrs);
		List<Ret1LateFeeSummarySectionDto> ret1SummSecDtos = basic6DocSummarySectionDao
				.lateBasicSummarySection(anx1SumReqDto);
		ret1SummSecDtos.forEach(ret1SummSecDto -> {
			Ret1ReviewSummaryRequestItemDto itemDto = new Ret1ReviewSummaryRequestItemDto();
			itemDto.setEntity(entityName);
			itemDto.setEntityPan(entityPan);
			itemDto.setCompanyCode(companyCode);
			itemDto.setSupplyType(ret1SummSecDto.getSupplyType());
			itemDto.setRetPer(retPeriod);
			itemDto.setGstinNum(gstinNum);
			itemDto.setTable(ret1SummSecDto.getTable());
			itemDto.setAspIgst(ret1SummSecDto.getAspIgst());
			itemDto.setAspCgst(ret1SummSecDto.getAspCgst());
			itemDto.setAspSgst(ret1SummSecDto.getAspSgst());
			itemDto.setAspCess(ret1SummSecDto.getAspCess());

			itemDto.setGstinIgst(ret1SummSecDto.getGstnIgst());
			itemDto.setGstinCgst(ret1SummSecDto.getGstnCgst());
			itemDto.setGstinSgst(ret1SummSecDto.getGstnSgst());
			itemDto.setGstinCess(ret1SummSecDto.getGstnCess());

			itemDto.setDiffIgst(
					itemDto.getAspIgst().subtract(itemDto.getGstinIgst()));
			itemDto.setDiffCgst(
					itemDto.getAspCgst().subtract(itemDto.getGstinCgst()));
			itemDto.setDiffSgst(
					itemDto.getAspSgst().subtract(itemDto.getGstinSgst()));
			itemDto.setDiffCess(
					itemDto.getAspCess().subtract(itemDto.getGstinCess()));
			summReqItemDtos.add(itemDto);
		});
	}

	private void basic7DocReviewSummary(String gstinNum, String retPeriod,
			String entityPan, String entityName, String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos) {
		Annexure1SummaryReqDto anx1SumReqDto = new Annexure1SummaryReqDto();
		anx1SumReqDto.setTaxPeriod(retPeriod);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstinNum);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		anx1SumReqDto.setDataSecAttrs(dataSecAttrs);
		List<Ret1PaymentSummarySectionDto> ret1SummSecDtos = basic7DocSummarySectionDao
				.lateBasicSummarySection(anx1SumReqDto);
		ret1SummSecDtos.forEach(ret1SummSecDto -> {
			Ret1ReviewSummaryRequestItemDto itemDto = new Ret1ReviewSummaryRequestItemDto();
			itemDto.setEntity(entityName);
			itemDto.setEntityPan(entityPan);
			itemDto.setCompanyCode(companyCode);
			itemDto.setSupplyType(ret1SummSecDto.getSupplyType());
			itemDto.setRetPer(retPeriod);
			itemDto.setGstinNum(gstinNum);
			itemDto.setTable(ret1SummSecDto.getTable());
			/*
			 * itemDto.setAspIgst(ret1SummSecDto.getAspIgst());
			 * itemDto.setAspCgst(ret1SummSecDto.getAspCgst());
			 * itemDto.setAspSgst(ret1SummSecDto.getAspSgst());
			 * itemDto.setAspCess(ret1SummSecDto.getAspCess());
			 * 
			 * itemDto.setGstinIgst(ret1SummSecDto.getGstnIgst());
			 * itemDto.setGstinCgst(ret1SummSecDto.getGstnCgst());
			 * itemDto.setGstinSgst(ret1SummSecDto.getGstnSgst());
			 * itemDto.setGstinCess(ret1SummSecDto.getGstnCess());
			 */

			itemDto.setDiffIgst(
					itemDto.getAspIgst().subtract(itemDto.getGstinIgst()));
			itemDto.setDiffCgst(
					itemDto.getAspCgst().subtract(itemDto.getGstinCgst()));
			itemDto.setDiffSgst(
					itemDto.getAspSgst().subtract(itemDto.getGstinSgst()));
			itemDto.setDiffCess(
					itemDto.getAspCess().subtract(itemDto.getGstinCess()));
			summReqItemDtos.add(itemDto);
		});
	}

	private void lateFeeReviewSummary(String gstinNum, String retPeriod,
			String entityPan, String entityName, String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos) {
		Annexure1SummaryReqDto anx1SumReqDto = new Annexure1SummaryReqDto();
		anx1SumReqDto.setTaxPeriod(retPeriod);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstinNum);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		anx1SumReqDto.setDataSecAttrs(dataSecAttrs);
		List<Ret1LateFeeSummarySectionDto> ret1SummSecDtos = lateFeeBasicDocSummSecDao
				.lateBasicSummarySection(anx1SumReqDto);
		ret1SummSecDtos.forEach(ret1SummSecDto -> {
			Ret1ReviewSummaryRequestItemDto itemDto = new Ret1ReviewSummaryRequestItemDto();
			itemDto.setEntity(entityName);
			itemDto.setEntityPan(entityPan);
			itemDto.setCompanyCode(companyCode);
			itemDto.setSupplyType(ret1SummSecDto.getSupplyType());
			itemDto.setRetPer(retPeriod);
			itemDto.setGstinNum(gstinNum);
			itemDto.setTable(ret1SummSecDto.getTable());
			itemDto.setAspIgst(ret1SummSecDto.getAspIgst());
			itemDto.setAspCgst(ret1SummSecDto.getAspCgst());
			itemDto.setAspSgst(ret1SummSecDto.getAspSgst());
			itemDto.setAspCess(ret1SummSecDto.getAspCess());

			itemDto.setGstinIgst(ret1SummSecDto.getGstnIgst());
			itemDto.setGstinCgst(ret1SummSecDto.getGstnCgst());
			itemDto.setGstinSgst(ret1SummSecDto.getGstnSgst());
			itemDto.setGstinCess(ret1SummSecDto.getGstnCess());

			itemDto.setDiffIgst(
					itemDto.getAspIgst().subtract(itemDto.getGstinIgst()));
			itemDto.setDiffCgst(
					itemDto.getAspCgst().subtract(itemDto.getGstinCgst()));
			itemDto.setDiffSgst(
					itemDto.getAspSgst().subtract(itemDto.getGstinSgst()));
			itemDto.setDiffCess(
					itemDto.getAspCess().subtract(itemDto.getGstinCess()));
			summReqItemDtos.add(itemDto);
		});
	}

	private void ref8SumSecReviewSummary(String gstinNum, String retPeriod,
			String entityPan, String entityName, String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos) {
		Annexure1SummaryReqDto anx1SumReqDto = new Annexure1SummaryReqDto();
		anx1SumReqDto.setTaxPeriod(retPeriod);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstinNum);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		anx1SumReqDto.setDataSecAttrs(dataSecAttrs);
		List<Ret1RefundSummarySectionDto> ret1SummSecDtos = ref8SumSecDao
				.lateBasicSummarySection(anx1SumReqDto);
		ret1SummSecDtos.forEach(ret1SummSecDto -> {
			Ret1ReviewSummaryRequestItemDto itemDto = new Ret1ReviewSummaryRequestItemDto();
			itemDto.setEntity(entityName);
			itemDto.setEntityPan(entityPan);
			itemDto.setCompanyCode(companyCode);
			itemDto.setRetPer(retPeriod);
			itemDto.setGstinNum(gstinNum);
			itemDto.setTable(ret1SummSecDto.getTable());
			itemDto.setGstinCashTax(ret1SummSecDto.getGstnTax());
			/* itemDto.setGstinCessInt(ret1SummSecDto.getUsrTotal()); */
			/*
			 * itemDto.set(ret1SummSecDto.getAspIgst());
			 * itemDto.setAspCgst(ret1SummSecDto.getAspCgst());
			 * itemDto.setAspSgst(ret1SummSecDto.getAspSgst());
			 * itemDto.setAspCess(ret1SummSecDto.getAspCess());
			 * 
			 * itemDto.setGstinIgst(ret1SummSecDto.getGstnIgst());
			 * itemDto.setGstinCgst(ret1SummSecDto.getGstnCgst());
			 * itemDto.setGstinSgst(ret1SummSecDto.getGstnSgst());
			 * itemDto.setGstinCess(ret1SummSecDto.getGstnCess());
			 */

			itemDto.setDiffIgst(
					itemDto.getAspIgst().subtract(itemDto.getGstinIgst()));
			itemDto.setDiffCgst(
					itemDto.getAspCgst().subtract(itemDto.getGstinCgst()));
			itemDto.setDiffSgst(
					itemDto.getAspSgst().subtract(itemDto.getGstinSgst()));
			itemDto.setDiffCess(
					itemDto.getAspCess().subtract(itemDto.getGstinCess()));
			summReqItemDtos.add(itemDto);
		});
	}

	@Override
	public Integer pushToErp(Ret1HeaderItemProcessReviewSummaryReqDto reqDto,
			String destName, AnxErpBatchEntity batch) {
		Integer respCode = null;
		try {
			ByteArrayOutputStream outwardStream = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext.newInstance(
					Ret1HeaderItemProcessReviewSummaryReqDto.class);
			Marshaller hdearMarshr = context.createMarshaller();
			hdearMarshr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			hdearMarshr.marshal(reqDto, outwardStream);
			String headerXml = outwardStream.toString();
			if (headerXml != null && headerXml.length() > 0) {
				headerXml = headerXml.substring(headerXml.indexOf('\n') + 1);
			}
			StringBuilder header = new StringBuilder();
			header.append(
					"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
			header.append(
					"xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'>");
			header.append("<soapenv:Header/>");
			header.append("<soapenv:Body>");
			header.append("<urn:ZupdateRet1Aspdata>");

			StringBuilder footer = new StringBuilder();

			footer.append("</urn:ZupdateRet1Aspdata>");
			footer.append("</soapenv:Body>");
			footer.append("</soapenv:Envelope>");

			String xml = null;
			if (headerXml != null) {
				xml = header + headerXml + footer;
			}
			if (xml != null && destName != null) {
				//respCode = destinationConn.post(destName, xml, batch);
				
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
		}
		return respCode;
	}

}

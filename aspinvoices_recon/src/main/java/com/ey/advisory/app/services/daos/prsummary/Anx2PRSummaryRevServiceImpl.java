package com.ey.advisory.app.services.daos.prsummary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Anx2PRSummaryHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Anx2PRSummaryItemDto;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailB2BDeafault;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailDEDeafault;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailDao;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailHeaderDto;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailISDDeafault;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailResponseDto;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailSEZWOPDeafault;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailSEZWPDeafault;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailsB2B;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailsDeemedExports;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailsISD;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailsSEZWOP;
import com.ey.advisory.app.services.daos.anx2prsdetail.Anx2PRSDetailsSEZWP;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;
import com.ey.advisory.core.dto.Anx2PRSProcessedResponseDto;

@Service("Anx2PRSummaryRevServiceImpl")
public class Anx2PRSummaryRevServiceImpl implements Anx2PRSummaryRevService {

	@Autowired
	@Qualifier("Anx2PRSDetailDaoImpl")
	Anx2PRSDetailDao anx2PRSDetailDao;

	@Autowired
	@Qualifier("Anx2PRSDetailsB2B")
	Anx2PRSDetailsB2B anx2PRSDetailsB2B;

	@Autowired
	@Qualifier("Anx2PRSDetailsSEZWP")
	Anx2PRSDetailsSEZWP anx2PRSDetailsSEZWP;

	@Autowired
	@Qualifier("Anx2PRSDetailsSEZWOP")
	Anx2PRSDetailsSEZWOP anx2PRSDetailsSEZWOP;

	@Autowired
	@Qualifier("Anx2PRSDetailsISD")
	Anx2PRSDetailsISD anx2PRSDetailsISD;

	@Autowired
	@Qualifier("Anx2PRSDetailsDeemedExports")
	Anx2PRSDetailsDeemedExports anx2PRSDetailsDeemedExports;

	@Autowired
	@Qualifier("Anx2PRSDetailB2BDeafault")
	private Anx2PRSDetailB2BDeafault anx2PRSDetailB2BDeafault;

	@Autowired
	@Qualifier("Anx2PRSDetailDEDeafault")
	private Anx2PRSDetailDEDeafault anx2PRSDetailDEDeafault;

	@Autowired
	@Qualifier("Anx2PRSDetailISDDeafault")
	private Anx2PRSDetailISDDeafault anx2PRSDetailISDDeafault;

	@Autowired
	@Qualifier("Anx2PRSDetailSEZWOPDeafault")
	private Anx2PRSDetailSEZWOPDeafault anx2PRSDetailSEZWOPDeafault;

	@Autowired
	@Qualifier("Anx2PRSDetailSEZWPDeafault")
	private Anx2PRSDetailSEZWPDeafault anx2PRSDetailSEZWPDeafault;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager manager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2PRSummaryRevServiceImpl.class);

	@Override
	public Anx2PRSummaryHeaderDto convertObjToHeader(String entityName,
			String entityPan, String companyCode, String state,
			List<Anx2PRSProcessedResponseDto> respDtos) {

		Anx2PRSummaryHeaderDto headerDto = new Anx2PRSummaryHeaderDto();
		List<Anx2PRSummaryItemDto> headerList = new ArrayList<>();

		respDtos.forEach(respDto -> {
			Anx2PRSummaryItemDto itemDto = new Anx2PRSummaryItemDto();
			itemDto.setEntity(entityPan);
			itemDto.setEntityName(entityName);
			itemDto.setCompanyCode(companyCode);
			itemDto.setState(state);
			itemDto.setRetPer(respDto.getReturnPeriod());
			itemDto.setGstinNum(respDto.getGstin());

			itemDto.setInvValue(respDto.getInvValue());
			itemDto.setTaxableValue(respDto.getTaxableValue());
			itemDto.setTotalTax(respDto.getTotalTaxPayable());

			itemDto.setTaxIgst(respDto.getTpIGST());
			itemDto.setTaxCgst(respDto.getTpCGST());
			itemDto.setTaxSgst(respDto.getTpSGST());
			itemDto.setTaxCess(respDto.getTpCess());
			itemDto.setTCount(
					new BigInteger(String.valueOf(respDto.getCount())));

			itemDto.setTotalCreditE(respDto.getTotalCreditEligible());
			itemDto.setCreditEIgst(respDto.getCeIGST());
			itemDto.setCreditECgst(respDto.getCeCGST());
			itemDto.setCreditESgst(respDto.getCeSGST());
			itemDto.setCreditECess(respDto.getCeCess());

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String updateDate = dtf.format(now);
			itemDto.setLastUpdate(updateDate);
			headerList.add(itemDto);
		});

		headerDto.setHeader(headerList);
		return headerDto;
	}

	private void convertObject(List<Anx2PRSDetailHeaderDto> headerDtos,
			List<Anx2PRSDetailResponseDto> recResponseDtos) {

		if (recResponseDtos != null && !recResponseDtos.isEmpty()) {
			Anx2PRSDetailHeaderDto b2bdetails = anx2PRSDetailsB2B
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(b2bdetails);

			Anx2PRSDetailHeaderDto sezWpDetails = anx2PRSDetailsSEZWP
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(sezWpDetails);

			Anx2PRSDetailHeaderDto deemedExportsdetails = anx2PRSDetailsDeemedExports
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(deemedExportsdetails);

			Anx2PRSDetailHeaderDto sezWopHeaderDetials = anx2PRSDetailsSEZWOP
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(sezWopHeaderDetials);

			Anx2PRSDetailHeaderDto isdDetails = anx2PRSDetailsISD
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(isdDetails);

			Anx2PRSDetailHeaderDto b2bDefault = anx2PRSDetailB2BDeafault
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(b2bDefault);

			Anx2PRSDetailHeaderDto deDefault = anx2PRSDetailDEDeafault
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(deDefault);

			Anx2PRSDetailHeaderDto sezwopDefault = anx2PRSDetailSEZWOPDeafault
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(sezwopDefault);

			Anx2PRSDetailHeaderDto isDefault = anx2PRSDetailISDDeafault
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(isDefault);

			Anx2PRSDetailHeaderDto sezWpDefault = anx2PRSDetailSEZWPDeafault
					.getAnx2PRSummaryDetail(recResponseDtos, null);
			headerDtos.add(sezWpDefault);
		}

	}

	public void convertObjToDetails(String gstin, String entityName,
			String entityPan, String companyCode, String state,
			String returnPeriod, List<Anx2PRSummaryItemDto> itemDtos) {

		Anx2PRSProcessedRequestDto dto = new Anx2PRSProcessedRequestDto();
		dto.setTaxPeriod(returnPeriod);
		Map<String, List<String>> securityAttributes = new HashMap<>();
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		securityAttributes.put(OnboardingConstant.GSTIN, gstins);
		dto.setDataSecAttrs(securityAttributes);
		List<Anx2PRSDetailHeaderDto> headerDtos = new ArrayList<>();
		List<Anx2PRSDetailResponseDto> recResponseDtos = anx2PRSDetailDao
				.getAnx2PRSDetail(dto);
		convertObject(headerDtos, recResponseDtos);

		headerDtos.forEach(detailsObj -> {
			Anx2PRSummaryItemDto itemDto = new Anx2PRSummaryItemDto();
			itemDto.setEntity(entityPan);
			itemDto.setEntityName(entityName);
			itemDto.setCompanyCode(companyCode);
			itemDto.setState(state);

			itemDto.setRetPer(returnPeriod);
			itemDto.setGstinNum(gstin);
			itemDto.setTableName(detailsObj.getTable());
			itemDto.setTableType(detailsObj.getInvType());

			itemDto.setTCount(detailsObj.getCount() > 0
					? new BigInteger(String.valueOf(detailsObj.getCount()))
					: BigInteger.ZERO);

			itemDto.setInvValue(detailsObj.getInvValue() != null
					? detailsObj.getInvValue() : BigDecimal.ZERO);

			itemDto.setTaxableValue(detailsObj.getTaxableValue() != null
					? detailsObj.getTaxableValue() : BigDecimal.ZERO);

			itemDto.setTotalTax(detailsObj.getTotalTaxPayable() != null
					? detailsObj.getTotalTaxPayable() : BigDecimal.ZERO);

			itemDto.setTaxIgst(detailsObj.getIGST() != null
					? detailsObj.getIGST() : BigDecimal.ZERO);

			itemDto.setTaxCgst(detailsObj.getCGST() != null
					? detailsObj.getCGST() : BigDecimal.ZERO);
			itemDto.setTaxSgst(detailsObj.getSGST() != null
					? detailsObj.getSGST() : BigDecimal.ZERO);
			itemDto.setTaxCess(detailsObj.getCess() != null
					? detailsObj.getCess() : BigDecimal.ZERO);

			itemDto.setTotalCreditE(detailsObj.getTotalCreditEligible() != null
					? detailsObj.getTotalCreditEligible() : BigDecimal.ZERO);
			itemDto.setCreditEIgst(detailsObj.getCeIGST() != null
					? detailsObj.getCeIGST() : BigDecimal.ZERO);
			itemDto.setCreditECgst(detailsObj.getCeCGST() != null
					? detailsObj.getCeCGST() : BigDecimal.ZERO);
			itemDto.setCreditESgst(detailsObj.getCeSGST() != null
					? detailsObj.getCeSGST() : BigDecimal.ZERO);
			itemDto.setCreditECess(detailsObj.getCeCess() != null
					? detailsObj.getCeCess() : BigDecimal.ZERO);
			itemDtos.add(itemDto);
		});

	}

	public List<Object[]> getSummary(String gstin, List<String> retPeriods,
			String type) {
		List<Object[]> obj = new ArrayList<>();

		try {
			String sql = null;
			if ("PRProcessSummary".equalsIgnoreCase(type)) {
				sql = prProcessSummary();
				Query q = manager.createNativeQuery(sql);
				q.setParameter("gstin", gstin);
				obj = q.getResultList();
			} else if ("PRReviewSummary".equalsIgnoreCase(type)) {
				sql = prReviewSummary();
				Query q = manager.createNativeQuery(sql);
				q.setParameter("gstin", gstin);
				q.setParameter("returnPeriod", retPeriods);
				obj = q.getResultList();
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return obj;
	}

	private String prReviewSummary() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("RETURN_PERIOD,CUST_GSTIN,");
		sql.append("DOC_TYPE,AN_TAX_DOC_TYPE,");
		sql.append("count(ID) AS count,SUM(DOC_AMT) AS INVOICE_VALUE,");
		sql.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,");
		sql.append("SUM(TAX_PAYABLE) AS TOTAL_TAX_PAYABLE,");
		sql.append("SUM(IGST_AMT) AS IGST_AMT,");
		sql.append("SUM(CGST_AMT) AS CGST_AMT,SUM(SGST_AMT) AS SGST_AMT,");
		sql.append(
				"SUM(IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) AS CESS_AMT,");
		sql.append("SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL(AVAILABLE_CGST,0)+");
		sql.append("IFNULL(AVAILABLE_SGST,0)+IFNULL(AVAILABLE_CESS,0)) AS  ");
		sql.append(
				"TOTAL_CREDIT_ELIGIBLE,SUM(AVAILABLE_IGST) AS AVAILABLE_IGST,");
		sql.append("SUM(AVAILABLE_CGST) AS AVAILABLE_CGST,");
		sql.append("SUM(AVAILABLE_SGST) AS AVAILABLE_SGST,");
		sql.append("SUM(AVAILABLE_CESS) AS AVAILABLE_CESS,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,");
		sql.append("PURCHASE_ORGANIZATION,DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6 ");
		sql.append("FROM ");
		sql.append("ANX_INWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND ");
		sql.append(
				"IS_DELETE = FALSE AND AN_RETURN_TYPE = 'ANX2' AND AN_TAX_DOC_TYPE ");
		sql.append("IN ('B2B','DXP','SEZWP','SEZWOP') ");
		sql.append(
				"AND RETURN_PERIOD IN (:returnPeriod ) AND CUST_GSTIN = :gstin ");
		sql.append(
				"GROUP BY RETURN_PERIOD,CUST_GSTIN,DOC_TYPE,AN_TAX_DOC_TYPE,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,");
		sql.append("PURCHASE_ORGANIZATION,DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6 ");
		return sql.toString();
	}

	private String prProcessSummary() {

		return null;
	}

	/*@Override
	public Integer pushToErp(Anx2PRSummaryHeaderDto headerDto,
			Anx2PRSummaryDetailDto detailsDto, final String destName,
			AnxErpBatchEntity batch) {
		try {
			ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
			JAXBContext headerContext = JAXBContext
					.newInstance(Anx2PRSummaryHeaderDto.class);
			Marshaller headerMarshaller = headerContext.createMarshaller();
			headerMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			headerMarshaller.marshal(headerDto, headerStream);

			String headerXml = headerStream.toString();
			if (headerXml != null && headerXml.length() > 0) {
				headerXml = headerXml.substring(headerXml.indexOf('\n') + 1);
			}

			ByteArrayOutputStream detailsStream = new ByteArrayOutputStream();
			JAXBContext detailsContext = JAXBContext
					.newInstance(Anx2PRSummaryDetailDto.class);
			Marshaller detailsMarshaller = detailsContext.createMarshaller();
			detailsMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			detailsMarshaller.marshal(detailsDto, detailsStream);

			String detailsXml = detailsStream.toString();
			if (detailsXml != null && detailsXml.length() > 0) {
				detailsXml = detailsXml.substring(detailsXml.indexOf('\n') + 1);
			}

			StringBuilder header = new StringBuilder();
			header.append(
					"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
			header.append(
					"xmlns:urn='urn:sap-com:document:sap:rfc:functions'>");
			header.append("<soapenv:Header/>");
			header.append("<soapenv:Body>");
			header.append("<urn:ZANX2_PR_SUMMARY>");
			StringBuilder footer = new StringBuilder();
			footer.append("</urn:ZANX2_PR_SUMMARY>");
			footer.append("</soapenv:Body>");
			footer.append("</soapenv:Envelope>");
			String xml = null;
			if (headerXml != null) {
				xml = header + headerXml + detailsXml + footer;
			}
			if (xml != null && destName != null) {
				// return destinationConn.post(destName, xml, batch);
			}
		} catch (Exception e) {
			LOGGER.error("Exeption Occured:", e);
		}
		return null;
	}*/
}

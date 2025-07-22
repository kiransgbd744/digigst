/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ConsolidatedGstr2ADto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr2AconsolidatedReportDaoImpl")
public class Gstr2AconsolidatedReportDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2AconsolidatedReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	public List<Object> getGstr1RSReports(
			Gstr2ProcessedRecordsReqDto criteria) {

		// String taxPeriod = criteria.getTaxPeriod();
		String taxPeriodFrom = criteria.getTaxPeriodFrom();
		String taxPeriodTo = criteria.getTaxPeriodTo();
		List<String> tableType = criteria.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		List<String> docType = criteria.getDocType();
		List<String> docTypeUpperCase = docType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		String cgstin = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					cgstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

					}
				}
			}
		}
		StringBuilder buildQuery = new StringBuilder();

		if (StringUtils.isNotBlank(cgstin)
				&& CollectionUtils.isNotEmpty(gstinList)) {
			buildQuery.append(" WHERE CGSTIN IN :gstinList");
		}

		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			buildQuery.append(" AND  TABLE_TYPE IN :tableTypeUpperCase");
		}

		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			buildQuery.append(" AND  DOC_TYPE IN :docTypeUpperCase");
		}

		/*
		 * if (StringUtils.isNotBlank(taxPeriod)) {
		 * buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
		 * 
		 * }
		 */

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildQuery.append(" AND DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":taxPeriodFrom AND :taxPeriodTo");

		}

		String queryStr = creategstnTransQueryString(buildQuery.toString());

		Query outquery = entityManager.createNativeQuery(queryStr);

		if (StringUtils.isNotBlank(cgstin)
				&& CollectionUtils.isNotEmpty(gstinList)) {
			outquery.setParameter("gstinList", gstinList);
		}

		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			outquery.setParameter("tableTypeUpperCase", tableTypeUpperCase);
		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			outquery.setParameter("docTypeUpperCase", docTypeUpperCase);
		}
		if (taxPeriodFrom != null && taxPeriodTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(criteria.getTaxPeriodFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(criteria.getTaxPeriodTo());
			outquery.setParameter("taxPeriodFrom", derivedRetPeriodFrom);
			outquery.setParameter("taxPeriodTo", derivedRetPeriodTo);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = outquery.getResultList();
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ConsolidatedGstr2ADto convertTransactionalLevel(Object[] arr) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();

		obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? arr[8].toString() : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
			obj.setTaxableValue(
					bigDecimalTaxVal.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		// obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		// obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			obj.setIgstAmt(
					bigDecimalIGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			obj.setCgstAmt(
					bigDecimalCGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			obj.setSgstAmt(
					bigDecimalSGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			obj.setCessAmt(
					bigDecimalCESS.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
			obj.setTotalTaxAmt(
					bigDecimalTOT.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
			obj.setInvoiceValue(
					bigDecimalINV.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		/*
		 * obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		 * obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		 * obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		 * obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		 * obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);
		 */
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*
		 * BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31]; if
		 * (bigDecimalORGTAX != null) { BigDecimal Orgtax = new
		 * BigDecimal(bigDecimalORGTAX.longValue());
		 * obj.setOriginalTaxableValue(Orgtax); } BigDecimal bigDecimalORGIGST =
		 * (BigDecimal) arr[32]; if (bigDecimalORGIGST != null) { BigDecimal
		 * Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
		 * obj.setOriginalIGSTAmount(Orgigst); } BigDecimal bigDecimalORGCESS =
		 * (BigDecimal) arr[33]; if (bigDecimalORGCESS != null) { BigDecimal
		 * Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
		 * obj.setOriginalCessAmount(Orgcess); }
		 */
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		// obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		if (arr[54] == null || arr[54] == "null") {
			obj.setInitiatedTime("");
		} else {
			Timestamp date = (Timestamp) arr[54];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			obj.setInitiatedTime(newdate);
		}
		obj.setIrnNum(arr[56] != null ? arr[56].toString() : null);
		obj.setIrnGenDate(arr[57] != null ? arr[57].toString() : null);
		obj.setIrnSourceType(arr[58] != null ? arr[58].toString() : null);

		return obj;

	}

	private String creategstnTransQueryString(String buildQuery) {

		return "SELECT RETURN_PERIOD,CGSTIN,SGSTIN,LEGAL_NAME,TRADE_NAME, DOC_TYPE,SUPPLY_TYPE ,TABLE_TYPE, "
				+ "DOC_NUM,DOC_DATE,TAXABLE_VALUE, TAX_RATE,IGST_AMT,CGST_AMT,SGST_AMT ,CESS_AMT,TOTAL_TAX, "
				+ "INVOICE_VALUE,POS,STATE_NAME,PORT_CODE,BOE_NUM,BOE_CREATED_DATE , BOE_REF_DATE,IS_AMENDMENT_BOE ,"
				+ "ORIGINAL_SUPPLIER_GSTIN, ORIGINAL_SUPPLIER_TRADENAME ,ORIGINAL_PORT_CODE, ORIGINAL_BILL_OF_ENTRY_NUMBER,"
				+ "ORIGINAL_BILL_OF_ENTRY_DATE , ORIGINAL_BILL_OF_ENTRY_REF_DATE,ORIGINAL_TAXABLE_VALUE, ORIGINAL_IGST_AMOUNT,"
				+ "ORIGINAL_CESS_AMOUNT ,ORG_DOC_NUM, ORG_DOC_DATE,INV_NUM,INV_DATE,ORG_INV_AMD_PERIOD,ORG_INV_AMD_TYPE, "
				+ "RCHRG, GSTR1_FILING_STATUS,GSTR1_FILING_DATE , GSTR1_FILING_PERIOD,GSTR3B_FILING_STATUS,CANCELLATION_DATE, "
				+ "CDN_DELINKING_FLAG ,CR_DR_PRE_GST,ITC_ELIGIBLE,DIFF_PERCENT, ITEM_NUMBER,ECOM_GSTIN,MERCHANT_ID ,INITIATED_DATE, "
				+ "INITIATED_TIME,DERIVED_RET_PERIOD, "
				+ "IRN_NUM,IRN_GEN_DATE,IRN_SOURCE_TYPE "
				+ " FROM ( SELECT COUNT(DISTINCT HDR.ID) CNT,TAX_PERIOD RETURN_PERIOD,CGSTIN,SGSTIN,"
				+ "LEGAL_NAME_BS LEGAL_NAME,TRADE_NAME, 'B2B' DOC_TYPE,INV_TYPE SUPPLY_TYPE, 'B2B' TABLE_TYPE,SUPPLIER_INV_NUM DOC_NUM, "
				+ "SUPPLIER_INV_DATE DOC_DATE, IFNULL(HDR.TAXABLE_VALUE,0) TAXABLE_VALUE,TAX_RATE, IFNULL(HDR.IGST_AMT,0) IGST_AMT, "
				+ "IFNULL(HDR.CGST_AMT,0) CGST_AMT, IFNULL(HDR.SGST_AMT,0) SGST_AMT, IFNULL(HDR.CESS_AMT,0) CESS_AMT, "
				+ "(IFNULL(HDR.IGST_AMT,0)+IFNULL(HDR.CGST_AMT,0) +IFNULL(HDR.SGST_AMT,0) +IFNULL(HDR.CESS_AMT,0)) AS TOTAL_TAX, "
				+ "IFNULL(HDR.SUPPLIER_INV_VAL,0) INVOICE_VALUE, POS, STATE_NAME,'' PORT_CODE,0 BOE_NUM,'' BOE_CREATED_DATE, "
				+ " '' BOE_REF_DATE,'' IS_AMENDMENT_BOE ,'' ORIGINAL_SUPPLIER_GSTIN, '' ORIGINAL_SUPPLIER_TRADENAME,'' ORIGINAL_PORT_CODE , "
				+ " '' ORIGINAL_BILL_OF_ENTRY_NUMBER,'' ORIGINAL_BILL_OF_ENTRY_DATE, '' ORIGINAL_BILL_OF_ENTRY_REF_DATE ,"
				+ " '' ORIGINAL_TAXABLE_VALUE, '' ORIGINAL_IGST_AMOUNT,'' ORIGINAL_CESS_AMOUNT ,'' ORG_DOC_NUM, '' ORG_DOC_DATE,"
				+ " '' INV_NUM,'' INV_DATE,ORG_INV_AMD_PERIOD, ORG_INV_AMD_TYPE,RCHRG, (CASE WHEN HDR.FILE_DATE IS NOT NULL AND HDR.FILE_PERIOD "
				+ "IS NOT NULL THEN 'Y' ELSE 'N' END) GSTR1_FILING_STATUS ,HDR.FILE_DATE GSTR1_FILING_DATE,HDR.FILE_PERIOD GSTR1_FILING_PERIOD,"
				+ "HDR.CFS_GSTR3B GSTR3B_FILING_STATUS ,HDR.CANCEL_DATE CANCELLATION_DATE ,(CASE WHEN HDR.FILE_DATE IS NOT NULL AND "
				+ "HDR.FILE_PERIOD IS NOT NULL THEN 'Y' ELSE 'N' END) CDN_DELINKING_FLAG ,'' CR_DR_PRE_GST,'' ITC_ELIGIBLE,DIFF_PERCENT,"
				+ "ITEM_NUMBER,'' ECOM_GSTIN,'' MERCHANT_ID ,TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') INITIATED_DATE,SUBSTR(HDR.CREATED_ON,12) "
				+ "INITIATED_TIME ,HDR.DERIVED_RET_PERIOD,HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE "
				+ "FROM GETGSTR2A_B2B_HEADER HDR INNER JOIN GETGSTR2A_B2B_ITEM "
				+ "ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN VENDOR_GSTIN_DETAILS VD "
				+ "ON HDR.CGSTIN = VD.GSTIN LEFT JOIN MASTER_STATE SC ON HDR.POS = SC.STATE_CODE "
				+ "WHERE HDR.IS_DELETE = FALSE  GROUP BY TAX_PERIOD,CFS,CGSTIN,SGSTIN,LEGAL_NAME_BS ,"
				+ "TRADE_NAME,SUPPLIER_INV_NUM ,SUPPLIER_INV_DATE,HDR.TAXABLE_VALUE,HDR.IGST_AMT,HDR.CGST_AMT,HDR.SGST_AMT ,HDR.CESS_AMT,"
				+ "HDR.SUPPLIER_INV_VAL,TAX_RATE, POS,STATE_NAME,ORG_INV_AMD_PERIOD ,ORG_INV_AMD_TYPE,RCHRG,HDR.FILE_DATE,"
				+ "HDR.FILE_PERIOD,HDR.CANCEL_DATE ,HDR.CFS_GSTR3B,DIFF_PERCENT,ITEM_NUMBER,HDR.CREATED_ON,"
				+ "HDR.DERIVED_RET_PERIOD,INV_TYPE,HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE "
				+ " UNION ALL SELECT COUNT(DISTINCT HDR.ID) CNT,TAX_PERIOD RETURN_PERIOD,"
				+ "CGSTIN,SGSTIN, LEGAL_NAME_BS LEGAL_NAME,TRADE_NAME,'B2BA' DOC_TYPE,INV_TYPE SUPPLY_TYPE, 'B2BA' TABLE_TYPE,"
				+ "SUPPLIER_INV_NUM DOC_NUM, SUPPLIER_INV_DATE DOC_DATE, IFNULL(HDR.TAXABLE_VALUE,0) TAXABLE_VALUE,TAX_RATE, "
				+ "IFNULL(HDR.IGST_AMT,0) IGST_AMT, IFNULL(HDR.CGST_AMT,0) CGST_AMT, IFNULL(HDR.SGST_AMT,0) SGST_AMT, "
				+ "IFNULL(HDR.CESS_AMT,0) CESS_AMT, (IFNULL(HDR.IGST_AMT,0)+IFNULL(HDR.CGST_AMT,0) +IFNULL(HDR.SGST_AMT,0) +IFNULL(HDR.CESS_AMT,0)) "
				+ "AS TOTAL_TAX, IFNULL(HDR.SUPPLIER_INV_VAL,0) INVOICE_VALUE, POS,STATE_NAME,'' PORT_CODE,0 BOE_NUM, '' BOE_CREATED_DATE,"
				+ " '' BOE_REF_DATE,'' IS_AMENDMENT_BOE , '' ORIGINAL_SUPPLIER_GSTIN,'' ORIGINAL_SUPPLIER_TRADENAME, '' ORIGINAL_PORT_CODE ,"
				+ " '' ORIGINAL_BILL_OF_ENTRY_NUMBER, '' ORIGINAL_BILL_OF_ENTRY_DATE,'' ORIGINAL_BILL_OF_ENTRY_REF_DATE, '' ORIGINAL_TAXABLE_VALUE,"
				+ " '' ORIGINAL_IGST_AMOUNT, '' ORIGINAL_CESS_AMOUNT ,HDR.ORG_INV_NUM ORG_DOC_NUM,HDR.ORG_INV_DATE ORG_DOC_DATE, '' INV_NUM,"
				+ " '' INV_DATE ,ORG_INV_AMD_PERIOD,ORG_INV_AMD_TYPE,RCHRG, (CASE WHEN HDR.FILE_DATE IS NOT NULL AND HDR.FILE_PERIOD IS NOT NULL "
				+ "THEN 'Y' ELSE 'N' END) GSTR1_FILING_STATUS ,HDR.FILE_DATE GSTR1_FILING_DATE,HDR.FILE_PERIOD GSTR1_FILING_PERIOD ,"
				+ "HDR.CFS_GSTR3B GSTR3B_FILING_STATUS,HDR.CANCEL_DATE CANCELLATION_DATE ,(CASE WHEN HDR.FILE_DATE IS NOT NULL "
				+ "AND HDR.FILE_PERIOD IS NOT NULL THEN 'Y' ELSE 'N' END) CDN_DELINKING_FLAG ,'' CR_DR_PRE_GST,"
				+ " '' ITC_ELIGIBLE,DIFF_PERCENT,ITEM_NUMBER,'' ECOM_GSTIN ,'' MERCHANT_ID ,TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') "
				+ "INITIATED_DATE ,SUBSTR(HDR.CREATED_ON,12) INITIATED_TIME,HDR.DERIVED_RET_PERIOD, "
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ "FROM GETGSTR2A_B2BA_HEADER "
				+ "HDR INNER JOIN GETGSTR2A_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN VENDOR_GSTIN_DETAILS VD "
				+ "ON HDR.CGSTIN = VD.GSTIN LEFT JOIN MASTER_STATE SC ON HDR.POS = SC.STATE_CODE WHERE HDR.IS_DELETE = FALSE "
				+ " GROUP BY TAX_PERIOD,CFS,CGSTIN,SGSTIN,LEGAL_NAME_BS,TRADE_NAME,SUPPLIER_INV_NUM ,SUPPLIER_INV_DATE,"
				+ "TAX_RATE, POS,STATE_NAME,HDR.ORG_INV_NUM,HDR.ORG_INV_DATE ,HDR.TAXABLE_VALUE,HDR.IGST_AMT,HDR.CGST_AMT,HDR.SGST_AMT,"
				+ "HDR.CESS_AMT,HDR.SUPPLIER_INV_VAL ,ORG_INV_AMD_PERIOD,ORG_INV_AMD_TYPE,RCHRG,HDR.FILE_DATE,HDR.FILE_PERIOD,HDR.CANCEL_DATE ,"
				+ "HDR.CFS_GSTR3B,DIFF_PERCENT,ITEM_NUMBER,HDR.CREATED_ON,HDR.DERIVED_RET_PERIOD,INV_TYPE "
				+ " UNION ALL SELECT COUNT(DISTINCT HDR.ID) "
				+ "CNT,TAX_PERIOD RETURN_PERIOD,HDR.CTIN CGSTIN,HDR.GSTIN SGSTIN, LEGAL_NAME_BS LEGAL_NAME,TRADE_NAME, (CASE WHEN NOTE_TYPE = 'C' "
				+ "THEN 'CN' WHEN NOTE_TYPE = 'D' THEN 'DN' END) DOC_TYPE, NOTE_TYPE SUPPLY_TYPE, 'CDN' TABLE_TYPE,NOTE_NUMBER DOC_NUM,"
				+ "NOTE_DATE DOC_DATE, IFNULL(HDR.TAXABLE_VALUE,0) TAXABLE_VALUE,TAX_RATE, IFNULL(HDR.IGST_AMT,0) IGST_AMT, IFNULL(HDR.CGST_AMT,0) "
				+ "GST_AMT, IFNULL(HDR.SGST_AMT,0) SGST_AMT, IFNULL(HDR.CESS_AMT,0) CESS_AMT, (IFNULL(HDR.IGST_AMT,0)+IFNULL(HDR.CGST_AMT,0) "
				+ " +IFNULL(HDR.SGST_AMT,0) +IFNULL(HDR.CESS_AMT,0)) AS TOTAL_TAX, IFNULL(HDR.NOTE_VALUE,0) INVOICE_VALUE, POS,STATE_NAME, "
				+ " '' PORT_CODE,0 BOE_NUM,'' BOE_CREATED_DATE,'' BOE_REF_DATE, '' IS_AMENDMENT_BOE ,'' ORIGINAL_SUPPLIER_GSTIN, "
				+ " '' ORIGINAL_SUPPLIER_TRADENAME,'' ORIGINAL_PORT_CODE , '' ORIGINAL_BILL_OF_ENTRY_NUMBER,'' ORIGINAL_BILL_OF_ENTRY_DATE, "
				+ " '' ORIGINAL_BILL_OF_ENTRY_REF_DATE ,'' ORIGINAL_TAXABLE_VALUE, '' ORIGINAL_IGST_AMOUNT,'' ORIGINAL_CESS_AMOUNT ,'' ORG_DOC_NUM, "
				+ " '' ORG_DOC_DATE,INV_NUMBER INV_NUM,INV_DATE INV_DATE , ORG_INV_AMD_PERIOD,ORG_INV_AMD_TYPE,RCHRG, (CASE WHEN HDR.FILE_DATE "
				+ "IS NOT NULL AND HDR.FILE_PERIOD IS NOT NULL THEN 'Y' ELSE 'N' END) GSTR1_FILING_STATUS , HDR.FILE_DATE GSTR1_FILING_DATE,"
				+ "HDR.FILE_PERIOD GSTR1_FILING_PERIOD , HDR.CFS_GSTR3B GSTR3B_FILING_STATUS,HDR.CANCEL_DATE CANCELLATION_DATE , "
				+ "(CASE WHEN HDR.FILE_DATE IS NOT NULL AND HDR.FILE_PERIOD IS NOT NULL THEN 'Y' ELSE 'N' END) CDN_DELINKING_FLAG , "
				+ " '' CR_DR_PRE_GST,'' ITC_ELIGIBLE,DIFF_PERCENT,ITEM_NUMBER, '' ECOM_GSTIN,'' MERCHANT_ID , TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') "
				+ "INITIATED_DATE, SUBSTR(HDR.CREATED_ON,12) INITIATED_TIME,HDR.DERIVED_RET_PERIOD, HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE "
				+ " FROM GETGSTR2A_CDN_HEADER "
				+ "HDR INNER JOIN GETGSTR2A_CDN_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN VENDOR_GSTIN_DETAILS VD ON HDR.CTIN = VD.GSTIN LEFT JOIN MASTER_STATE SC "
				+ "ON HDR.POS = SC.STATE_CODE WHERE HDR.IS_DELETE = FALSE  GROUP BY TAX_PERIOD,CFS,CTIN, "
				+ "HDR.GSTIN,LEGAL_NAME_BS,TRADE_NAME,NOTE_TYPE,NOTE_NUMBER,NOTE_DATE, TAX_RATE ,POS,STATE_NAME,INV_NUMBER,INV_DATE, HDR.TAXABLE_VALUE,"
				+ "HDR.IGST_AMT,HDR.CGST_AMT,HDR.SGST_AMT,HDR.CESS_AMT,HDR.NOTE_VALUE, ORG_INV_AMD_PERIOD,ORG_INV_AMD_TYPE,RCHRG,HDR.FILE_DATE,"
				+ "HDR.FILE_PERIOD,HDR.CANCEL_DATE , HDR.CFS_GSTR3B,DIFF_PERCENT,ITEM_NUMBER,HDR.CREATED_ON, HDR.DERIVED_RET_PERIOD,INV_TYPE, "
				+ "HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE "
				+ " UNION ALL SELECT COUNT(DISTINCT HDR.ID) CNT,TAX_PERIOD RETURN_PERIOD,HDR.CTIN CGSTIN,HDR.GSTIN SGSTIN, LEGAL_NAME_BS LEGAL_NAME,"
				+ "TRADE_NAME, (CASE WHEN NOTE_TYPE = 'C' THEN 'CNA' WHEN NOTE_TYPE = 'D' THEN 'DNA' END) DOC_TYPE, NOTE_TYPE SUPPLY_TYPE, "
				+ " 'CDNA' TABLE_TYPE,NOTE_NUMBER DOC_NUM,NOTE_DATE DOC_DATE, IFNULL(HDR.TAXABLE_VALUE,0) TAXABLE_VALUE,TAX_RATE, IFNULL(HDR.IGST_AMT,0) "
				+ "IGST_AMT, IFNULL(HDR.CGST_AMT,0) CGST_AMT, IFNULL(HDR.SGST_AMT,0) SGST_AMT, IFNULL(HDR.CESS_AMT,0) CESS_AMT, "
				+ "(IFNULL(HDR.IGST_AMT,0)+IFNULL(HDR.CGST_AMT,0) +IFNULL(HDR.SGST_AMT,0) +IFNULL(HDR.CESS_AMT,0)) AS TOTAL_TAX, "
				+ "IFNULL(HDR.NOTE_VALUE,0) INVOICE_VALUE, POS,STATE_NAME, '' PORT_CODE,0 BOE_NUM,'' BOE_CREATED_DATE,'' BOE_REF_DATE, "
				+ " '' IS_AMENDMENT_BOE ,'' ORIGINAL_SUPPLIER_GSTIN, '' ORIGINAL_SUPPLIER_TRADENAME,'' ORIGINAL_PORT_CODE , "
				+ " '' ORIGINAL_BILL_OF_ENTRY_NUMBER,'' ORIGINAL_BILL_OF_ENTRY_DATE, '' ORIGINAL_BILL_OF_ENTRY_REF_DATE ,'' ORIGINAL_TAXABLE_VALUE, "
				+ " '' ORIGINAL_IGST_AMOUNT,'' ORIGINAL_CESS_AMOUNT , ORG_NOTE_NUMBER ORG_DOC_NUM,ORG_NOTE_DATE ORG_DOC_DATE, INV_NUMBER INV_NUM,"
				+ "INV_DATE INV_DATE ,ORG_INV_AMD_PERIOD, ORG_INV_AMD_TYPE,RCHRG, (CASE WHEN HDR.FILE_DATE IS NOT NULL AND HDR.FILE_PERIOD "
				+ "IS NOT NULL THEN 'Y' ELSE 'N' END) GSTR1_FILING_STATUS ,HDR.FILE_DATE GSTR1_FILING_DATE, HDR.FILE_PERIOD GSTR1_FILING_PERIOD ,"
				+ "HDR.CFS_GSTR3B GSTR3B_FILING_STATUS, HDR.CANCEL_DATE CANCELLATION_DATE ,(CASE WHEN HDR.FILE_DATE IS NOT NULL AND HDR.FILE_PERIOD "
				+ "IS NOT NULL THEN 'Y' ELSE 'N' END) CDN_DELINKING_FLAG , '' CR_DR_PRE_GST,'' ITC_ELIGIBLE,DIFF_PERCENT,ITEM_NUMBER, '' ECOM_GSTIN,"
				+ " '' MERCHANT_ID , TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') INITIATED_DATE , SUBSTR(HDR.CREATED_ON,12) INITIATED_TIME,"
				+ "HDR.DERIVED_RET_PERIOD, "
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ "FROM GETGSTR2A_CDNA_HEADER HDR INNER JOIN GETGSTR2A_CDNA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN VENDOR_GSTIN_DETAILS VD "
				+ "ON HDR.CTIN = VD.GSTIN LEFT JOIN MASTER_STATE SC ON HDR.POS = SC.STATE_CODE WHERE HDR.IS_DELETE = FALSE "
				+ " GROUP BY TAX_PERIOD,CFS,CTIN,HDR.GSTIN,LEGAL_NAME_BS,TRADE_NAME,NOTE_TYPE, NOTE_NUMBER,NOTE_DATE,"
				+ "TAX_RATE ,POS,STATE_NAME,ORG_NOTE_NUMBER, ORG_NOTE_DATE,INV_NUMBER,INV_DATE , HDR.TAXABLE_VALUE,HDR.IGST_AMT,HDR.CGST_AMT,"
				+ "HDR.SGST_AMT,HDR.CESS_AMT,HDR.NOTE_VALUE, ORG_INV_AMD_PERIOD,ORG_INV_AMD_TYPE,RCHRG,HDR.FILE_DATE,HDR.FILE_PERIOD , "
				+ "HDR.CANCEL_DATE,HDR.CFS_GSTR3B,DIFF_PERCENT,ITEM_NUMBER, HDR.CREATED_ON,HDR.DERIVED_RET_PERIOD,INV_TYPE "
				+ " UNION ALL SELECT COUNT(DISTINCT HDR.ID) CNT,RET_PERIOD RETURN_PERIOD,HDR.CTIN CGSTIN,HDR.GSTIN SGSTIN, LEGAL_NAME_BS LEGAL_NAME,"
				+ "TRADE_NAME, ISD_DOC_TYPE DOC_TYPE, '' SUPPLY_TYPE,'ISD' TABLE_TYPE,DOC_NUM,DOC_DATE, 0 TAXABLE_VALUE,0 TAX_RATE , "
				+ "IFNULL(HDR.IGST_AMT,0) IGST_AMT, IFNULL(HDR.CGST_AMT,0) CGST_AMT, IFNULL(HDR.SGST_AMT,0) SGST_AMT, IFNULL(HDR.CESS_AMT,0) "
				+ "CESS_AMT, (IFNULL(HDR.IGST_AMT,0)+IFNULL(HDR.CGST_AMT,0) +IFNULL(HDR.SGST_AMT,0) +IFNULL(HDR.CESS_AMT,0)) "
				+ "AS TOTAL_TAX,0 INVOICE_VALUE , '' POS,STATE_NAME,'' PORT_CODE,0 BOE_NUM ,'' BOE_CREATED_DATE, '' BOE_REF_DATE,"
				+ " '' IS_AMENDMENT_BOE ,'' ORIGINAL_SUPPLIER_GSTIN, '' ORIGINAL_SUPPLIER_TRADENAME,'' ORIGINAL_PORT_CODE , "
				+ " '' ORIGINAL_BILL_OF_ENTRY_NUMBER,'' ORIGINAL_BILL_OF_ENTRY_DATE, '' ORIGINAL_BILL_OF_ENTRY_REF_DATE ,"
				+ " '' ORIGINAL_TAXABLE_VALUE, '' ORIGINAL_IGST_AMOUNT,'' ORIGINAL_CESS_AMOUNT ,'' ORG_DOC_NUM, '' ORG_DOC_DATE,"
				+ " '' INV_NUM,'' INV_DATE ,'' ORG_INV_AMD_PERIOD, '' ORG_INV_AMD_TYPE,'' RCHRG,'' GSTR1_FILING_STATUS , '' GSTR1_FILING_DATE,"
				+ " '' GSTR1_FILING_PERIOD, '' GSTR3B_FILING_STATUS,'' CANCELLATION_DATE , '' CDN_DELINKING_FLAG,'' CR_DR_PRE_GST,ITC_ELG ITC_ELIGIBLE, "
				+ "0 DIFF_PERCENT,0 ITEM_NUMBER,'' ECOM_GSTIN,'' MERCHANT_ID , TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') INITIATED_DATE, "
				+ "SUBSTR(HDR.CREATED_ON,12) INITIATED_TIME,HDR.DERIVED_RET_PERIOD, "
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ "FROM GETGSTR2A_ISD_HEADER "
				+ "HDR INNER JOIN GETGSTR2A_ISD_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN VENDOR_GSTIN_DETAILS VD ON HDR.GSTIN = VD.GSTIN LEFT JOIN MASTER_STATE SC "
				+ "ON SUBSTR(HDR.GSTIN,1,2) = SC.STATE_CODE WHERE HDR.IS_DELETE = FALSE  GROUP BY RET_PERIOD,CFS,CTIN, "
				+ "HDR.GSTIN,LEGAL_NAME_BS,TRADE_NAME,ISD_DOC_TYPE,DOC_NUM, HDR.IGST_AMT,HDR.CGST_AMT,HDR.SGST_AMT,HDR.CESS_AMT, DOC_DATE,ITC_ELG ,"
				+ "STATE_NAME,HDR.CREATED_ON, HDR.DERIVED_RET_PERIOD "
				+ " UNION ALL SELECT COUNT(DISTINCT HDR.ID) CNT,RET_PERIOD RETURN_PERIOD,"
				+ "HDR.CTIN CGSTIN, HDR.GSTIN SGSTIN,LEGAL_NAME_BS LEGAL_NAME,TRADE_NAME, ISD_DOC_TYPE DOC_TYPE,'' SUPPLY_TYPE,'ISDA' "
				+ "TABLE_TYPE,DOC_NUM, DOC_DATE, 0 TAXABLE_VALUE,0 TAX_RATE , IFNULL(HDR.IGST_AMT,0) IGST_AMT, "
				+ "IFNULL(HDR.CGST_AMT,0) CGST_AMT, IFNULL(HDR.SGST_AMT,0) SGST_AMT, IFNULL(HDR.CESS_AMT,0) CESS_AMT, "
				+ "(IFNULL(HDR.IGST_AMT,0)+IFNULL(HDR.CGST_AMT,0) +IFNULL(HDR.SGST_AMT,0) +IFNULL(HDR.CESS_AMT,0)) "
				+ "AS TOTAL_TAX, 0 INVOICE_VALUE , '' POS,STATE_NAME,'' PORT_CODE,0 BOE_NUM , '' BOE_CREATED_DATE,"
				+ " '' BOE_REF_DATE,'' IS_AMENDMENT_BOE , '' ORIGINAL_SUPPLIER_GSTIN,'' ORIGINAL_SUPPLIER_TRADENAME, "
				+ " '' ORIGINAL_PORT_CODE ,'' ORIGINAL_BILL_OF_ENTRY_NUMBER, '' ORIGINAL_BILL_OF_ENTRY_DATE,"
				+ " '' ORIGINAL_BILL_OF_ENTRY_REF_DATE , '' ORIGINAL_TAXABLE_VALUE,'' ORIGINAL_IGST_AMOUNT, "
				+ " '' ORIGINAL_CESS_AMOUNT ,ORG_DOC_NUM,ORG_DOC_DATE,'' INV_NUM, '' INV_DATE ,'' ORG_INV_AMD_PERIOD,"
				+ " '' ORG_INV_AMD_TYPE, '' RCHRG,'' GSTR1_FILING_STATUS ,'' GSTR1_FILING_DATE, "
				+ " '' GSTR1_FILING_PERIOD,'' GSTR3B_FILING_STATUS, '' CANCELLATION_DATE ,'' CDN_DELINKING_FLAG, "
				+ " '' CR_DR_PRE_GST,ITC_ELG ITC_ELIGIBLE,0 DIFF_PERCENT, 0 ITEM_NUMBER,'' ECOM_GSTIN,"
				+ " '' MERCHANT_ID , TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') INITIATED_DATE,SUBSTR(HDR.CREATED_ON,12) "
				+ "INITIATED_TIME,HDR.DERIVED_RET_PERIOD, "
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ "FROM GETGSTR2A_ISDA_HEADER "
				+ "HDR INNER JOIN GETGSTR2A_ISDA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "VENDOR_GSTIN_DETAILS VD ON HDR.GSTIN = VD.GSTIN LEFT JOIN "
				+ "MASTER_STATE SC ON SUBSTR(HDR.GSTIN,1,2) = SC.STATE_CODE WHERE HDR.IS_DELETE = FALSE "
				+ " GROUP BY RET_PERIOD,CFS,CTIN, HDR.GSTIN,LEGAL_NAME_BS,TRADE_NAME,"
				+ "ISD_DOC_TYPE,DOC_NUM,DOC_DATE, HDR.IGST_AMT,HDR.CGST_AMT,HDR.SGST_AMT,HDR.CESS_AMT, ITC_ELG ,"
				+ "STATE_NAME,ORG_DOC_NUM,ORG_DOC_DATE,HDR.CREATED_ON, HDR.DERIVED_RET_PERIOD "
				+ " UNION ALL "
				+ "SELECT COUNT(DISTINCT HDR.ID) CNT,RET_PERIOD RETURN_PERIOD, HDR.GSTIN CGSTIN,'' SGSTIN,"
				+ "LEGAL_NAME_BS LEGAL_NAME,TRADE_NAME, '' DOC_TYPE,'' SUPPLY_TYPE,'IMPG' TABLE_TYPE,'' DOC_NUM,"
				+ " '' DOC_DATE, IFNULL(HDR.TAXABLE_VALUE,0) TAXABLE_VALUE,0 TAX_RATE , IFNULL(HDR.IGST_AMT,0) "
				+ "IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, IFNULL(HDR.CESS_AMT,0) CESS_AMT, (IFNULL(HDR.IGST_AMT,0) +IFNULL(HDR.CESS_AMT,0)) "
				+ "AS TOTAL_TAX, 0 INVOICE_VALUE,'' POS, '' STATE_NAME,PORT_CODE,BOE_NUM ,BOE_CREATED_DATE,BOE_REF_DATE, "
				+ "TO_CHAR(IS_AMENDMENT_BOE) IS_AMENDMENT_BOE ,'' ORIGINAL_SUPPLIER_GSTIN, '' ORIGINAL_SUPPLIER_TRADENAME,"
				+ " '' ORIGINAL_PORT_CODE , '' ORIGINAL_BILL_OF_ENTRY_NUMBER,'' ORIGINAL_BILL_OF_ENTRY_DATE, "
				+ " '' ORIGINAL_BILL_OF_ENTRY_REF_DATE ,'' ORIGINAL_TAXABLE_VALUE, '' ORIGINAL_IGST_AMOUNT,"
				+ " '' ORIGINAL_CESS_AMOUNT ,'' ORG_DOC_NUM, '' ORG_DOC_DATE,'' INV_NUM,'' INV_DATE,'' ORG_INV_AMD_PERIOD, "
				+ " '' ORG_INV_AMD_TYPE ,'' GSTR1_FILING_STATUS,'' GSTR1_FILING_DATE, '' GSTR1_FILING_PERIOD ,"
				+ " '' RCHRG ,'' GSTR3B_FILING_STATUS, '' CANCELLATION_DATE,'' CDN_DELINKING_FLAG ,'' CR_DR_PRE_GST, "
				+ " '' ITC_ELIGIBLE,0 DIFF_PERCENT,0 ITEM_NUMBER,'' ECOM_GSTIN, '' MERCHANT_ID ,TO_CHAR(HDR.CREATED_ON,"
				+ "'DD-MM-YYYY') INITIATED_DATE, SUBSTR(HDR.CREATED_ON,12) INITIATED_TIME,HDR.DERIVED_RET_PERIOD, "
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ "FROM GETGSTR2A_IMPG_HEADER HDR INNER JOIN GETGSTR2A_IMPG_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "VENDOR_GSTIN_DETAILS VD ON HDR.GSTIN = VD.GSTIN WHERE HDR.IS_DELETE = FALSE AND "
				+ "LENGTH(TRADE_NAME)>0 GROUP BY RET_PERIOD,HDR.GSTIN,LEGAL_NAME_BS,TRADE_NAME, HDR.TAXABLE_VALUE,"
				+ "HDR.IGST_AMT,HDR.CESS_AMT, PORT_CODE,BOE_NUM,BOE_CREATED_DATE,BOE_REF_DATE,IS_AMENDMENT_BOE , "
				+ "HDR.CREATED_ON,HDR.DERIVED_RET_PERIOD "
				+ " UNION ALL SELECT COUNT(DISTINCT HDR.ID) CNT,RET_PERIOD "
				+ "RETURN_PERIOD,HDR.GSTIN CGSTIN,SGSTIN, LEGAL_NAME_BS LEGAL_NAME,VD.TRADE_NAME, '' DOC_TYPE, "
				+ " '' SUPPLY_TYPE,'IMPGSEZ' TABLE_TYPE,'' DOC_NUM,'' DOC_DATE, IFNULL(HDR.TAXABLE_VALUE,0) TAXABLE_VALUE,"
				+ "0 TAX_RATE, IFNULL(HDR.IGST_AMT,0) IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, IFNULL(HDR.CESS_AMT,0) CESS_AMT, "
				+ "(IFNULL(HDR.IGST_AMT,0)+IFNULL(HDR.CESS_AMT,0)) AS TOTAL_TAX, 0 INVOICE_VALUE,'' POS,'' STATE_NAME, "
				+ "PORT_CODE,BOE_NUM,BOE_CREATED_DATE,BOE_REF_DATE, TO_CHAR(IS_AMENDMENT_BOE) IS_AMENDMENT_BOE , "
				+ " '' ORIGINAL_SUPPLIER_GSTIN,'' ORIGINAL_SUPPLIER_TRADENAME, '' ORIGINAL_PORT_CODE ,"
				+ " '' ORIGINAL_BILL_OF_ENTRY_NUMBER, '' ORIGINAL_BILL_OF_ENTRY_DATE,'' ORIGINAL_BILL_OF_ENTRY_REF_DATE , "
				+ " '' ORIGINAL_TAXABLE_VALUE,'' ORIGINAL_IGST_AMOUNT, '' ORIGINAL_CESS_AMOUNT ,'' ORG_DOC_NUM,'' ORG_DOC_DATE, "
				+ " '' INV_NUM,'' INV_DATE,'' ORG_INV_AMD_PERIOD, '' ORG_INV_AMD_TYPE ,'' RCHRG,"
				+ " '' GSTR1_FILING_STATUS, '' GSTR1_FILING_DATE,'' GSTR1_FILING_PERIOD , '' GSTR3B_FILING_STATUS,"
				+ " '' CANCELLATION_DATE, '' CDN_DELINKING_FLAG ,'' CR_DR_PRE_GST,'' ITC_ELIGIBLE, 0 DIFF_PERCENT,"
				+ "0 ITEM_NUMBER,'' ECOM_GSTIN,'' MERCHANT_ID , TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') INITIATED_DATE, "
				+ "SUBSTR(HDR.CREATED_ON,12) INITIATED_TIME,HDR.DERIVED_RET_PERIOD, "
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ "FROM GETGSTR2A_IMPGSEZ_HEADER HDR INNER JOIN GETGSTR2A_IMPGSEZ_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "VENDOR_GSTIN_DETAILS VD ON HDR.GSTIN = VD.GSTIN WHERE HDR.IS_DELETE = FALSE "
				+ " GROUP BY RET_PERIOD,HDR.GSTIN,SGSTIN ,LEGAL_NAME_BS,VD.TRADE_NAME, "
				+ "HDR.TAXABLE_VALUE,HDR.IGST_AMT,HDR.CESS_AMT, PORT_CODE,BOE_NUM,BOE_CREATED_DATE,BOE_REF_DATE,"
				+ "IS_AMENDMENT_BOE, HDR.CREATED_ON,HDR.DERIVED_RET_PERIOD "
				+ " UNION ALL SELECT COUNT(DISTINCT HDR.ID) "
				+ "CNT,RET_PERIOD RETURN_PERIOD,HDR.GSTIN CGSTIN,SGSTIN, LEGAL_NAME_BS LEGAL_NAME,VD.TRADE_NAME, "
				+ " '' DOC_TYPE,'' SUPPLY_TYPE, 'IMPG_SEZ_AMD' TABLE_TYPE,'' DOC_NUM,'' DOC_DATE, "
				+ "IFNULL(HDR.TAXABLE_VALUE,0) TAXABLE_VALUE,0 TAX_RATE, IFNULL(HDR.IGST_AMT,0) IGST_AMT, "
				+ "0 CGST_AMT, 0 SGST_AMT, IFNULL(HDR.CESS_AMT,0) CESS_AMT, (IFNULL(HDR.IGST_AMT,0)+IFNULL(HDR.CESS_AMT,0)) "
				+ "AS TOTAL_TAX, 0 INVOICE_VALUE,'' POS,'' STATE_NAME, PORT_CODE,BOE_NUM, BOE_CREATED_DATE,"
				+ "BOE_REF_DATE,'' IS_AMENDMENT_BOE , '' ORIGINAL_SUPPLIER_GSTIN,'' ORIGINAL_SUPPLIER_TRADENAME, "
				+ " '' ORIGINAL_PORT_CODE ,'' ORIGINAL_BILL_OF_ENTRY_NUMBER, '' ORIGINAL_BILL_OF_ENTRY_DATE,"
				+ " '' ORIGINAL_BILL_OF_ENTRY_REF_DATE , '' ORIGINAL_TAXABLE_VALUE,'' ORIGINAL_IGST_AMOUNT, "
				+ " '' ORIGINAL_CESS_AMOUNT ,'' ORG_DOC_NUM,'' ORG_DOC_DATE, '' INV_NUM,'' INV_DATE,"
				+ " '' ORG_INV_AMD_PERIOD,'' ORG_INV_AMD_TYPE, '' RCHRG ,'' GSTR1_FILING_STATUS ,"
				+ " '' GSTR1_FILING_DATE, '' GSTR1_FILING_PERIOD ,'' GSTR3B_FILING_STATUS, "
				+ " '' CANCELLATION_DATE,'' CDN_DELINKING_FLAG ,'' CR_DR_PRE_GST, '' ITC_ELIGIBLE,0 DIFF_PERCENT,"
				+ "0 ITEM_NUMBER,'' ECOM_GSTIN, '' MERCHANT_ID , TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') INITIATED_DATE, "
				+ "SUBSTR(HDR.CREATED_ON,12) INITIATED_TIME,HDR.DERIVED_RET_PERIOD, "
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ "FROM GETGSTR2A_AMDHIST_HEADER HDR INNER JOIN GETGSTR2A_AMDHIST_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "VENDOR_GSTIN_DETAILS VD ON HDR.GSTIN = VD.GSTIN WHERE HDR.IS_DELETE = FALSE "
				+ " GROUP BY RET_PERIOD,HDR.GSTIN, SGSTIN,LEGAL_NAME_BS,VD.TRADE_NAME , "
				+ "HDR.TAXABLE_VALUE,HDR.IGST_AMT,HDR.CESS_AMT,PORT_CODE,BOE_NUM, BOE_CREATED_DATE,BOE_REF_DATE ,"
				+ " HDR.CREATED_ON, HDR.DERIVED_RET_PERIOD ) " + buildQuery;
	}

}

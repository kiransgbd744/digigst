/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ConsolidatedGstr2ADto;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */

	@Component("Gstr2GetISDReportDaoImpl")
	@Slf4j
	public class Gstr2GetISDReportDaoImpl implements Gstr1aGetDao {

		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;

		/*
		 * @Autowired
		 * 
		 * @Qualifier("batchSaveStatusRepository") private Gstr1BatchRepository
		 * gstr1BatchRepository;
		 */

		static Integer cutoffPeriod = null;

		@Override
		public List<Object> getGstnConsolidatedReports(SearchCriteria criteria) {
			GstnConsolidatedReqDto request = (GstnConsolidatedReqDto) criteria;
			String taxPeriod = request.getTaxPeriod();
			List<String> gstr2aSections = request.getGstr2aSections();
			String gstin = request.getGstin();

			StringBuilder buildQuery = new StringBuilder();

			if (StringUtils.isNotBlank(gstin)) {
				buildQuery.append(" WHERE SGSTIN IN :gstin");
			}

			if (CollectionUtils.isNotEmpty(gstr2aSections)) {
				buildQuery.append(" AND TABLE_TYPE IN :gstr2aSections");
			}

			if (taxPeriod != null) {
				buildQuery.append(" AND DERIVED_RET_PERIOD  = :taxPeriod ");
			}


			String queryStr = creategstnB2CSSummaryQueryString(
					buildQuery.toString());
			Query q = entityManager.createNativeQuery(queryStr);

			if (StringUtils.isNotBlank(gstin)) {
				q.setParameter("gstin", Lists.newArrayList(gstin));
			}

			if (taxPeriod != null) {
				int derivedRetPeriod = GenUtil
						.convertTaxPeriodToInt(request.getTaxPeriod());
				q.setParameter("taxPeriod", derivedRetPeriod);

			}
			if (CollectionUtils.isNotEmpty(gstr2aSections)) {
				q.setParameter("gstr2aSections", gstr2aSections);
			}

			List<Object[]> list = q.getResultList();
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
				BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
				obj.setTaxableValue(aspTax);
			}
			//obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
			obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
			//obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
			BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
			if (bigDecimalIGST != null) {
				BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
				obj.setIgstAmt(aspIGST);
			}
			BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
			if (bigDecimalCGST != null) {
				BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
				obj.setCgstAmt(aspCGST);
			}
			BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
			if (bigDecimalSGST != null) {
				BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
				obj.setSgstAmt(aspSGST);
			}
			BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
			if (bigDecimalCESS != null) {
				BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
				obj.setCessAmt(aspCESS);
			}
			BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
			if (bigDecimalTOT != null) {
				BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
				obj.setTotalTaxAmt(aspTOT);
			}
			BigDecimal bigDecimalINV = (BigDecimal) arr[17];
			if (bigDecimalINV != null) {
				BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
				obj.setInvoiceValue(inv);
			}
			/*obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
			obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
			obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
			obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
			obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);*/
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
			/*BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31];
			if (bigDecimalORGTAX != null) {
				BigDecimal Orgtax = new BigDecimal(bigDecimalORGTAX.longValue());
				obj.setOriginalTaxableValue(Orgtax);
			}
			BigDecimal bigDecimalORGIGST = (BigDecimal) arr[32];
			if (bigDecimalORGIGST != null) {
				BigDecimal Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
				obj.setOriginalIGSTAmount(Orgigst);
			}
			BigDecimal bigDecimalORGCESS = (BigDecimal) arr[33];
			if (bigDecimalORGCESS != null) {
				BigDecimal Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
				obj.setOriginalCessAmount(Orgcess);
			}*/
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
			obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
			/*if (arr[54] == null || arr[54] == "null") {
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
			}*/

			return obj;
		}

		private String creategstnB2CSSummaryQueryString(String buildQuery) {

			return "SELECT RETURN_PERIOD,CGSTIN,SGSTIN,LEGAL_NAME,TRADE_NAME,"
					+ "DOC_TYPE,SUPPLY_TYPE ,TABLE_TYPE,"
					+ "DOC_NUM,DOC_DATE,TAXABLE_VALUE,"
					+ "IGST_AMT,CGST_AMT,SGST_AMT ,CESS_AMT,TOTAL_TAX,"
					+ "INVOICE_VALUE,POS,STATE_NAME,PORT_CODE,BOE_NUM,BOE_CREATED_DATE ,"
					+ "BOE_REF_DATE,IS_AMENDMENT_BOE ,ORIGINAL_SUPPLIER_GSTIN,"
					+ "ORIGINAL_SUPPLIER_TRADENAME ,ORIGINAL_PORT_CODE,"
					+ "ORIGINAL_BILL_OF_ENTRY_NUMBER,ORIGINAL_BILL_OF_ENTRY_DATE ,"
					+ "ORIGINAL_BILL_OF_ENTRY_REF_DATE,ORIGINAL_TAXABLE_VALUE,"
					+ "ORIGINAL_IGST_AMOUNT,ORIGINAL_CESS_AMOUNT ,ORG_DOC_NUM,"
					+ "ORG_DOC_DATE,INV_NUM,INV_DATE,ORG_INV_AMD_PERIOD,"
					+ "ORG_INV_AMD_TYPE,RCHRG, GSTR1_FILING_STATUS,"
					+ "GSTR1_FILING_DATE ,GSTR1_FILING_PERIOD,GSTR3B_FILING_STATUS,"
					+ "CANCELLATION_DATE,CDN_DELINKING_FLAG ,CR_DR_PRE_GST,"
					+ "ITC_ELIGIBLE,DIFF_PERCENT,ITEM_NUMBER,ECOM_GSTIN,"
					+ "MERCHANT_ID ,INITIATED_DATE, INITIATED_TIME,"
					+ "DERIVED_RET_PERIOD FROM( SELECT RET_PERIOD RETURN_PERIOD,"
					+ "HDR.CTIN CGSTIN,HDR.GSTIN SGSTIN,LEGAL_NAME_BS LEGAL_NAME,TRADE_NAME,"
					+ "ISD_DOC_TYPE DOC_TYPE,'' SUPPLY_TYPE, 'ISD' TABLE_TYPE,"
					+ "DOC_NUM,DOC_DATE, 0 TAXABLE_VALUE,0 TAX_RATE, "
					+ "SUM(IFNULL(ITM.IGST_AMT,0)) IGST_AMT, SUM(IFNULL(ITM.CGST_AMT,0)) "
					+ "CGST_AMT, SUM(IFNULL(ITM.SGST_AMT,0)) SGST_AMT, "
					+ "SUM(IFNULL(ITM.CESS_AMT,0)) CESS_AMT, "
					+ "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) "
					+ " +IFNULL(ITM.SGST_AMT,0) +IFNULL(ITM.CESS_AMT,0)) "
					+ "AS TOTAL_TAX, 0 INVOICE_VALUE, '' POS,STATE_NAME,"
					+ " '' PORT_CODE,'' BOE_NUM,'' BOE_CREATED_DATE,'' BOE_REF_DATE,"
					+ " '' IS_AMENDMENT_BOE ,'' ORIGINAL_SUPPLIER_GSTIN,"
					+ " '' ORIGINAL_SUPPLIER_TRADENAME,'' ORIGINAL_PORT_CODE ,"
					+ " '' ORIGINAL_BILL_OF_ENTRY_NUMBER,"
					+ " '' ORIGINAL_BILL_OF_ENTRY_DATE,"
					+ " '' ORIGINAL_BILL_OF_ENTRY_REF_DATE ,"
					+ " '' ORIGINAL_TAXABLE_VALUE,'' ORIGINAL_IGST_AMOUNT,"
					+ " '' ORIGINAL_CESS_AMOUNT ,'' ORG_DOC_NUM,'' ORG_DOC_DATE,"
					+ " '' INV_NUM,'' INV_DATE ,'' ORG_INV_AMD_PERIOD,"
					+ " '' ORG_INV_AMD_TYPE,'' RCHRG,'' GSTR1_FILING_STATUS ,"
					+ " '' GSTR1_FILING_DATE,'' GSTR1_FILING_PERIOD,"
					+ " '' GSTR3B_FILING_STATUS,'' CANCELLATION_DATE ,"
					+ " '' CDN_DELINKING_FLAG,'' CR_DR_PRE_GST,ITC_ELG ITC_ELIGIBLE,"
					+ "0 DIFF_PERCENT,0 ITEM_NUMBER,'' ECOM_GSTIN,"
					+ " '' MERCHANT_ID ,"
					+ "TO_CHAR(HDR.CREATED_ON,'DD-MM-YYYY') INITIATED_DATE,"
					+ "SUBSTR(HDR.CREATED_ON,12) INITIATED_TIME,"
					+ "HDR.DERIVED_RET_PERIOD FROM GETGSTR2A_ISD_HEADER "
					+ "HDR INNER JOIN GETGSTR2A_ISD_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
					+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "LEFT JOIN (SELECT DISTINCT VENDOR_GSTIN AS GSTIN,LEGAL_NAME "
					+ "AS LEGAL_NAME_BS,TRADE_NAME FROM TBL_VENDOR_MASTER_CONFIG "
					+ "WHERE GSTIN_STATUS ='Active' AND IS_ACTIVE = TRUE AND IS_FETCHED = TRUE) "
					+ "VD ON HDR.GSTIN = VD.GSTIN LEFT JOIN MASTER_STATE SC "
					+ "ON SUBSTR(HDR.GSTIN,1,2) = SC.STATE_CODE WHERE HDR.IS_DELETE = FALSE  "
					+ " GROUP BY "
					+ "RET_PERIOD,CFS,CTIN,HDR.GSTIN,LEGAL_NAME_BS,TRADE_NAME,"
					+ "ISD_DOC_TYPE,DOC_NUM,DOC_DATE,"
					+ "ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT,ITC_ELG ,STATE_NAME,"
					+ "HDR.CREATED_ON,HDR.DERIVED_RET_PERIOD) "
					+ buildQuery;

		}
	}


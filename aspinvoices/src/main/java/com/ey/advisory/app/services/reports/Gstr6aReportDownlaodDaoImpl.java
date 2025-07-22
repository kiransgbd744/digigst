package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr6aReportsDownloadRespDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.app.util.OnboardingConstant;

@Component("Gstr6aReportDownlaodDaoImpl")
public class Gstr6aReportDownlaodDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6aReportDownlaodDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Object> getgstr6aReportDownlaodDaoImpl(
			Gstr6AProcessedDataRequestDto request2) {
		Gstr6AProcessedDataRequestDto request = (Gstr6AProcessedDataRequestDto) request2;

		List<String> docType = request.getDocType();
		if (docType != null && !docType.isEmpty()) {
			docType.replaceAll(String::toUpperCase);
		}
		if (docType.contains("CR")) {
			docType.remove("CR");
			docType.add("C");
		}
		if (docType.contains("DR")) {
			docType.remove("DR");
			docType.add("D");
		}

		String taxperiod = request.getTaxPeriod();
		List<String> tableType = request.getTableType();
		if (tableType != null && !tableType.isEmpty()) {
			tableType.replaceAll(String::toUpperCase);
		}
		String fromTaxperiod = request.getFromPeriod();
		String derivedStartPeriod = fromTaxperiod.substring(2, 6)
				+ fromTaxperiod.substring(0, 2);

		String toTaxperiod = request.getToPeriod();
		String derivedEndPeriod = toTaxperiod.substring(2, 6)
				+ toTaxperiod.substring(0, 2);

		LOGGER.info(taxperiod);

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String GSTIN = null;

		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}

		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildHeader = new StringBuilder();

		if (StringUtils.isNotBlank(derivedStartPeriod)
				&& StringUtils.isNotBlank(derivedEndPeriod)) {
			buildQuery.append(
					" WHERE DERIVED_RET_PERIOD BETWEEN :derivedStartPeriod AND :derivedEndPeriod ");

		}
		if (GSTIN != null && !GSTIN.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			buildQuery.append(" AND SUPPLIER_GSTIN IN :gstinList");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND DOC_TYPE IN :docType");
		}
		if (tableType != null && !tableType.isEmpty()) {
			buildQuery.append(" AND TRANS_TYPE IN :tableType");
		}

		String queryStr = createApiProcessedQueryString(buildQuery.toString(),
				buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (StringUtils.isNotBlank(derivedStartPeriod)
				&& StringUtils.isNotBlank(derivedEndPeriod)) {
			q.setParameter("derivedStartPeriod", derivedStartPeriod);
			q.setParameter("derivedEndPeriod", derivedEndPeriod);
		}
		if (docType != null && !docType.isEmpty()) {
			if (docType != null && !docType.isEmpty() && docType.size() > 0) {
				q.setParameter("docType", docType);
			}
		}
		if (tableType != null && !tableType.isEmpty()) {
			if (tableType != null && !tableType.isEmpty()
					&& tableType.size() > 0) {
				q.setParameter("tableType", tableType);
			}
		}
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public List<Object> getgstr6aReportDownlaodDashboardDaoImpl(
			Gstr6AProcessedDataRequestDto request2) {
		Gstr6AProcessedDataRequestDto request = (Gstr6AProcessedDataRequestDto) request2;

		String fy = request.getFy();
		String startMonth = "04";
		String endMonth = "03";
		String appendMonthYear = null;
		String appendMonthYear1 = null;
		if (fy != null && !fy.isEmpty()) {
			String[] arrOfStr = fy.split("-", 2);
			appendMonthYear = arrOfStr[0] + startMonth;
			appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
		}
		int derivedStartPeriod = Integer.parseInt(appendMonthYear);

		int derivedEndPeriod = Integer.parseInt(appendMonthYear1);

		List<String> monthList = request.getMonth();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String GSTIN = null;
		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

					}
				}
			}
		}

		String taxperiod = request.getTaxPeriod();

		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildHeader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			buildQuery.append(" WHERE RECEPIENT_GSTIN IN :gstinList");
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND TAX_PERIOD = :taxperiod ");
		}
		buildQuery.append(
				" AND DERIVED_RET_PERIOD BETWEEN :derivedStartPeriod AND :derivedEndPeriod");
		if (monthList != null && monthList.size() > 0) {
			buildQuery
					.append(" AND SUBSTRING(TAX_PERIOD,1,2) IN (:monthList) ");
		}

		String queryStr = createApiProcessedQueryString(buildQuery.toString(),
				buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			q.setParameter("taxperiod", taxperiod);
		}

		q.setParameter("derivedStartPeriod", derivedStartPeriod);
		q.setParameter("derivedEndPeriod", derivedEndPeriod);
		if (monthList != null && monthList.size() > 0) {
			q.setParameter("monthList", monthList);
		}
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr6aReportsDownloadRespDto convertProcessed(Object[] arr) {
		Gstr6aReportsDownloadRespDto obj = new Gstr6aReportsDownloadRespDto();

		obj.setCounterPartyReturnStatus(
				arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setRecipentGSTIN(arr[2] != null ? arr[2].toString() : null);
		if (arr[3].toString().equalsIgnoreCase("C")) {
			obj.setDocumentType("CR");
		} else if (arr[3].toString().equalsIgnoreCase("D")) {
			obj.setDocumentType("DR");
		} else {
			obj.setDocumentType(arr[3] != null ? arr[3].toString() : null);
		}
		obj.setTransactionType(arr[4] != null ? arr[4].toString() : null);
		obj.setDlinkFlag(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentNumber(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentDate(arr[7] != null ? arr[7].toString() : null);
		obj.setLineNumber(arr[8] != null ? arr[8].toString() : null);
		obj.setSupplierGstin(arr[9] != null ? arr[9].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[10] != null ? arr[10].toString() : null);
		obj.setOriginalDocumentDate(
				arr[11] != null ? arr[11].toString() : null);
		obj.setOriginalInvoiceNo(arr[12] != null ? arr[12].toString() : null);
		obj.setOriginalInvoiceDate(arr[13] != null ? arr[13].toString() : null);
		obj.setCrdrPreGST(arr[14] != null ? arr[14].toString() : null);
		obj.setPos(arr[15] != null ? arr[15].toString() : null);
		obj.setTaxableValue(arr[16] != null ? arr[16].toString() : null);
		obj.setTaxRate(arr[17] != null
				? appendDecimalDigit((BigDecimal) arr[17]) : null);
		if (arr[18] != null) {
			obj.setIntegratedTaxAmount(
					arr[18] != null ? arr[18].toString() : null);
		} else {
			obj.setIntegratedTaxAmount("0.00");
		}
		if (arr[19] != null) {
			obj.setCentralTaxAmount(
					arr[19] != null ? arr[19].toString() : null);
		} else {
			obj.setCentralTaxAmount("0.00");
		}
		if (arr[20] != null) {
			obj.setStateUTTaxAmount(
					arr[20] != null ? arr[20].toString() : null);
		} else {
			obj.setStateUTTaxAmount("0.00");
		}
		if (arr[20] != null) {
			obj.setCessAmount(arr[21] != null ? arr[21].toString() : null);
		} else {
			obj.setCessAmount("0.00");
		}
		obj.setInvoiceValue(arr[22] != null ? arr[22].toString() : null);
		obj.setDifferentialPercentage(
				arr[23] != null ? arr[23].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[24] != null ? arr[24].toString() : null);
		obj.setReverseCharge(arr[25] != null ? arr[25].toString() : null);
		obj.setSourceTypeIRN(arr[26] != null ? arr[26].toString() : null);
		obj.setIrnNumber(arr[27] != null ? arr[27].toString() : null);
		obj.setIrnGenerationDate(arr[28] != null ? arr[28].toString() : null);
		obj.setCFp(arr[29] != null ? arr[29].toString() : null);
		obj.setLegalName(arr[30] != null ? arr[30].toString() : null);
		obj.setTradeName(arr[31] != null ? arr[31].toString() : null);
		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery,
			String buildHeader) {

		return " select CFS,TAX_PERIOD,RECEPIENT_GSTIN,DOC_TYPE,TRANS_TYPE, D_FLAG, "
				+ "DOC_NUMBER,DOC_DATE,ITM_NO,SUPPLIER_GSTIN,ORG_DOC_NUM,ORG_DOC_DATE,"
				+ "ORG_INV_NO,ORG_INV_DATE, CRDR_PER_GST,POS,TAXABLE_VALUE,TAX_RATE, "
				+ "IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT, DOC_AMT, DIFF_PER,REASON_CRDR, "
				+ "RCHRG,IRN_SOURCE_TYPE,IRN_NUM,IRN_GEN_DATE,CFP,"
				+ " LEGAL_NAME_BS,TRADE_NAME "
				+ " FROM ( SELECT HDR.CFS,TAX_PERIOD,CTIN AS RECEPIENT_GSTIN, 'INV' AS DOC_TYPE,"
				+ " 'B2B' AS TRANS_TYPE,'' as D_FLAG, HDR.DOC_NUMBER, HDR.DOC_DATE,ITM_NO, "
				+ "GSTIN AS SUPPLIER_GSTIN,NULL AS ORG_DOC_NUM,NULL AS ORG_DOC_DATE, "
				+ "NULL AS ORG_INV_NO,NULL AS ORG_INV_DATE, NULL AS CRDR_PER_GST,HDR.POS,"
				+ "ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.CESS_AMT, ITM.DOC_AMT, NULL AS DIFF_PER, NULL AS REASON_CRDR,CFP, "
				+ "REVERSE_CHARGE AS RCHRG,IRN_SOURCE_TYPE,IRN_NUM,IRN_GEN_DATE,HDR.DERIVED_RET_PERIOD"
				+ " from  GETGSTR6A_B2B_HEADER HDR INNER JOIN  "
				+ "GETGSTR6A_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND IS_DELETE = FALSE "
				+ " UNION SELECT HDR.CFS,TAX_PERIOD,CTIN AS RECEPIENT_GSTIN, 'RNV' AS DOC_TYPE,"
				+ " 'B2BA' AS TRANS_TYPE,'' as D_FLAG, HDR.DOC_NUMBER, HDR.DOC_DATE,ITM_NO, "
				+ "GSTIN AS SUPPLIER_GSTIN, HDR. ORG_DOC_NUMBER,HDR.ORG_DOC_DATE, "
				+ "NULL AS ORG_INV_NO,NULL AS ORG_INV_DATE, NULL AS CRDR_PER_GST,HDR.POS,"
				+ "ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.CESS_AMT, ITM.DOC_AMT, NULL AS DIFF_PER, NULL AS REASON_CRDR,CFP, "
				+ "REVERSE_CHARGE AS RCHRG,NULL AS IRN_SOURCE_TYPE,NULL AS IRN_NUM,NULL AS IRN_GEN_DATE,HDR.DERIVED_RET_PERIOD"
				+ " from  GETGSTR6A_B2BA_HEADER HDR INNER JOIN  GETGSTR6A_B2BA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND IS_DELETE = FALSE"
				+ " UNION ALL SELECT HDR.CFS,TAX_PERIOD,CTIN AS RECEPIENT_GSTIN, "
				+ "HDR.NOTE_TYPE,'CDN' AS TRANS_TYPE, D_FLAG,"
				+ " HDR.NOTE_NUM AS DOC_NUMBER, "
				+ " HDR.NOTE_DATE AS  DOC_DATE,ITM_NO, "
				+ "GSTIN AS SUPPLIER_GSTIN, null as ORG_DOC_NUMBER,null as ORG_DOC_DATE, "
				+ "NULL AS ORG_INV_NO,NULL AS ORG_INV_DATE, "
				+ "NULL AS CRDR_PER_GST,HDR.POS,ITM.TAXABLE_VALUE,ITM.TAX_RATE, "
				+ "ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT,"
				+ "ITM.INV_VALUE as  DOC_AMT, NULL AS DIFF_PER, NULL AS REASON_CRDR,CFP, "
				+ "NULL AS RCHRG,IRN_SOURCE_TYPE,IRN_NUM,IRN_GEN_DATE,HDR.DERIVED_RET_PERIOD "
				+ " from  GETGSTR6A_CDN_HEADER HDR INNER JOIN  GETGSTR6A_CDN_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND IS_DELETE = FALSE"
				+ " UNION SELECT HDR.CFS,TAX_PERIOD,CTIN AS RECEPIENT_GSTIN, "
				+ "HDR.NOTE_TYPE,'CDNA' AS TRANS_TYPE, D_FLAG,"
				+ " HDR.NOTE_NUM AS DOC_NUMBER, "
				+ " HDR.NOTE_DATE AS  DOC_DATE,ITM_NO, "
				+ "GSTIN AS SUPPLIER_GSTIN, ORG_NOTE_NUM  as ORG_DOC_NUMBER, "
				+ "ORG_NOTE_DATE as ORG_DOC_DATE,NULL AS ORG_INV_NO,"
				+ "NULL AS ORG_INV_DATE, NULL AS CRDR_PER_GST, HDR.POS,"
				+ "ITM.TAXABLE_VALUE,ITM.TAX_RATE, ITM.IGST_AMT,ITM.CGST_AMT,"
				+ "ITM.SGST_AMT,ITM.CESS_AMT, ITM.INV_VALUE as  DOC_AMT, "
				+ "NULL AS DIFF_PER, NULL AS REASON_CRDR,CFP,"
				+ "NULL AS RCHRG,NULL AS IRN_SOURCE_TYPE,NULL AS IRN_NUM,NULL AS IRN_GEN_DATE,HDR.DERIVED_RET_PERIOD "
				+ " from  GETGSTR6A_CDNA_HEADER HDR INNER JOIN  "
				+ " GETGSTR6A_CDNA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND IS_DELETE = FALSE) " 
				+ " HDR LEFT JOIN (SELECT DISTINCT VENDOR_GSTIN AS GSTIN, "
				+ " LEGAL_NAME AS LEGAL_NAME_BS,TRADE_NAME FROM "
				+ " TBL_VENDOR_MASTER_CONFIG WHERE GSTIN_STATUS "
				+ " IN ('Active','Suspended','Cancelled') AND "
				+ " IS_ACTIVE = TRUE AND IS_FETCHED = TRUE ) "
				+ " VD ON HDR.SUPPLIER_GSTIN = VD.GSTIN"
				+ buildQuery;
	}

	private String appendDecimalDigit(BigDecimal b) {

		String val = b.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

		String[] s = val.split("\\.");
		if (s.length == 2) {
			if (s[1].length() == 1)
				return "'" + (s[0] + "." + s[1] + "0");
			else {
				return "'" + val;
			}
		} else
			return "'" + (val + ".00");

	}
}

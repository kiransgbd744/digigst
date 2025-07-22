package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

import com.ey.advisory.app.data.views.client.Gstr2ProcessedDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

@Component("Gstr2ProcessedReportsDaoImpl")
public class Gstr2ProcessedReportsDaoImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr2ProcessedReportsDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	public List<Object> getErrorReports(Gstr2ProcessedRecordsReqDto criteria) {

		LocalDate docRecvFrom = criteria.getDocRecvFrom();
		LocalDate docRecvTo = criteria.getDocRecvTo();
		String taxPeriodFrom = criteria.getTaxPeriodFrom();
		String taxPeriodTo = criteria.getTaxPeriodTo();
		List<String> tableType = criteria.getTableType();
		List<String> tableTypeUpperCase = tableType.stream().map(String::toUpperCase).collect(Collectors.toList());

		List<String> docType = criteria.getDocType();
		List<String> docTypeUpperCase = docType.stream().map(String::toUpperCase).collect(Collectors.toList());

		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		String cgstin = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					cgstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN).size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

					}
				}
			}
		}
		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildHeader = new StringBuilder();

		if (cgstin != null && !cgstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HDR.CUST_GSTIN IN :gstinList");
				buildHeader.append(" PR_RECIPIENT_GSTIN  IN :gstinList");
			}
		}
		if (CollectionUtils.isEmpty(tableTypeUpperCase)) {
			if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
				buildQuery.append(" AND  HDR.TAX_DOC_TYPE IN :tableTypeUpperCase");
			}
		}
		if (CollectionUtils.isEmpty(docTypeUpperCase)) {
			if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
				buildQuery.append(" AND  HDR.DOC_TYPE IN :docTypeUpperCase");
			}
		}
		if (docRecvFrom != null && docRecvTo != null) {
			buildQuery.append(" AND  HDR.DOC_DATE BETWEEN :docRecvFrom AND :docRecvTo  ");
		}
		if (StringUtils.isNotBlank(taxPeriodFrom) && StringUtils.isNotBlank(taxPeriodTo)) {
			buildQuery.append(" AND  HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo  ");
			buildHeader.append(" AND PR_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
		}
		String queryStr = creategstnTransQueryString(buildQuery.toString(),buildHeader.toString());

		LOGGER.error("bufferString-------------------------->" + queryStr);
		Query outquery = entityManager.createNativeQuery(queryStr);

		if (cgstin != null && !cgstin.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty() && gstinList.size() > 0) {
				outquery.setParameter("gstinList", gstinList);
			}
		}
		if (docRecvFrom != null && docRecvTo != null) {
			outquery.setParameter("docRecvFrom", docRecvFrom);
			outquery.setParameter("docRecvTo", docRecvTo);
		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			outquery.setParameter("docTypeUpperCase", docTypeUpperCase);
		}
		if (StringUtils.isNotBlank(taxPeriodFrom) && StringUtils.isNotBlank(taxPeriodTo)) {
			outquery.setParameter("taxPeriodFrom", GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
			outquery.setParameter("taxPeriodTo", GenUtil.convertTaxPeriodToInt(taxPeriodTo));
		}

		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			outquery.setParameter("tableTypeUpperCase", tableTypeUpperCase);
		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			outquery.setParameter("docTypeUpperCase", docTypeUpperCase);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = outquery.getResultList();
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr2ProcessedDto convertTransactionalLevel(Object[] arr) {
		Gstr2ProcessedDto obj = new Gstr2ProcessedDto();

		obj.setCategory(arr[0] != null ? arr[0].toString() : null);
		obj.setUserResponse(arr[1] != null ? arr[1].toString() : null);
		obj.setSuggestedResponse(arr[2] != null ? arr[2].toString() : null);
		obj.setReportType(arr[3] != null ? arr[3].toString() : null);
		obj.setMismatchReason(arr[4] != null ? arr[4].toString() : null);
		obj.setResponseTaxPeriod(arr[5] != null ? arr[5].toString() : null);
		obj.setReconTaxPeriod(arr[6] != null ? arr[6].toString() : null);
		obj.setCalendarMonth(arr[7] != null ? arr[7].toString() : null);
		obj.setOrgMonth(arr[8] != null ? arr[8].toString() : null);
		obj.setUserID(arr[9] != null ? arr[9].toString() : null);
		obj.setSourceFileName(arr[10] != null ? arr[10].toString() : null);
		obj.setProfitCentre(arr[11] != null ? arr[11].toString() : null);
		obj.setPlant(arr[12] != null ? arr[12].toString() : null);
		obj.setDivision(arr[13] != null ? arr[13].toString() : null);
		obj.setLocation(arr[14] != null ? arr[14].toString() : null);
		obj.setPurchaseOrganisation(arr[15] != null ? arr[15].toString() : null);
		obj.setUserAccess1(arr[16] != null ? arr[16].toString() : null);
		obj.setUserAccess2(arr[17] != null ? arr[17].toString() : null);
		obj.setUserAccess3(arr[18] != null ? arr[18].toString() : null);
		obj.setUserAccess4(arr[19] != null ? arr[19].toString() : null);
		obj.setUserAccess5(arr[20] != null ? arr[20].toString() : null);
		obj.setUserAccess6(arr[21] != null ? arr[21].toString() : null);
		obj.setGLCodeTaxableValue(arr[22] != null ? arr[22].toString() : null);
		obj.setGLCodeIGST(arr[23] != null ? arr[23].toString() : null);
		obj.setGLCodeCGST(arr[24] != null ? arr[24].toString() : null);
		obj.setGLCodeSGST(arr[25] != null ? arr[25].toString() : null);
		obj.setGLCodeAdvaloremCess(arr[26] != null ? arr[26].toString() : null);
		obj.setGLCodeSpecificCess(arr[27] != null ? arr[27].toString() : null);
		obj.setGLCodeStateCess(arr[28] != null ? arr[28].toString() : null);
		obj.setReturnPeriod(arr[29] != null ? arr[29].toString() : null);
		obj.setRecipientGSTIN(arr[30] != null ? arr[30].toString() : null);
		obj.setDocumentType(arr[31] != null ? arr[31].toString() : null);
		obj.setSupplyType(arr[32] != null ? arr[32].toString() : null);
		obj.setDocumentNumber(arr[33] != null ? arr[33].toString() : null);

		if (arr[34] != null) {
			String strdate = arr[34].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {
			obj.setDocumentDate(null);
		}
		obj.setOriginalDocumentNumber(arr[35] != null ? arr[35].toString() : null);
		if (arr[36] != null) {
			String strdate = arr[36].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setOriginalDocumentDate(newDate);
		} else {
			obj.setOriginalDocumentDate(null);
		}

		obj.setCRDRPreGST(arr[37] != null ? arr[37].toString() : null);
		obj.setLineNumber(arr[38] != null ? arr[38].toString() : null);
		obj.setSupplierGSTIN(arr[39] != null ? arr[39].toString() : null);
		obj.setSupplierType(arr[40] != null ? arr[40].toString() : null);
		obj.setDifferentialFlag(arr[41] != null ? arr[41].toString() : null);
		obj.setSupplierName(arr[43] != null ? arr[43].toString() : null);
		obj.setSupplierCode(arr[44] != null ? arr[44].toString() : null);
		obj.setSupplierAddress1(arr[45] != null ? arr[45].toString() : null);
		obj.setSupplierAddress2(arr[46] != null ? arr[46].toString() : null);
		obj.setSupplierAddress3(arr[47] != null ? arr[47].toString() : null);
		obj.setSupplierAddress4(arr[48] != null ? arr[48].toString() : null);
		obj.setPOS(arr[49] != null ? arr[49].toString() : null);
		obj.setStateApplyingCess(arr[50] != null ? arr[50].toString() : null);
		obj.setPortCode(arr[51] != null ? arr[51].toString() : null);
		obj.setBillOfEntry(arr[52] != null ? arr[52].toString() : null);
		if (arr[53] != null) {
			String strdate = arr[53].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBillOfEntryDate(newDate);
		} else {
			obj.setBillOfEntryDate(null);
		}
		obj.setCIFValue(arr[54] != null ? arr[54].toString() : null);
		obj.setCustomDuty(arr[55] != null ? arr[55].toString() : null);
		obj.setHSNorSAC(arr[56] != null ? arr[56].toString() : null);
		obj.setItemCode(arr[57] != null ? arr[57].toString() : null);
		obj.setItemDescription(arr[58] != null ? arr[58].toString() : null);
		obj.setCategoryOfItem(arr[59] != null ? arr[59].toString() : null);
		obj.setUnitOfMeasurement(arr[60] != null ? arr[60].toString() : null);
		obj.setQuantity(arr[61] != null ? arr[61].toString() : null);
		obj.setSection7ofIGSTFlag(arr[62] != null ? arr[62].toString() : null);
		obj.setTaxableValue(arr[63] != null ? arr[63].toString() : null);
		obj.setIntegratedTaxRate(arr[64] != null ? arr[64].toString() : null);
		obj.setIntegratedTaxAmount(arr[65] != null ? arr[65].toString() : null);
		obj.setCentralTaxRate(arr[66] != null ? arr[66].toString() : null);
		obj.setCentralTaxAmount(arr[67] != null ? arr[67].toString() : null);
		obj.setStateUTTaxRate(arr[68] != null ? arr[68].toString() : null);
		obj.setStateUTTaxAmount(arr[69] != null ? arr[69].toString() : null);
		obj.setAdvaloremCessRate(arr[70] != null ? arr[70].toString() : null);
		obj.setAdvaloremCessAmount(arr[71] != null ? arr[71].toString() : null);
		obj.setSpecificCessRate(arr[72] != null ? arr[72].toString() : null);
		obj.setSpecificCessAmount(arr[73] != null ? arr[73].toString() : null);
		obj.setStateCessRate(arr[74] != null ? arr[74].toString() : null);
		obj.setStateCessAmount(arr[75] != null ? arr[75].toString() : null);
		obj.setOtherValue(arr[76] != null ? arr[76].toString() : null);
		obj.setInvoiceValue(arr[77] != null ? arr[77].toString() : null);
		obj.setClaimRefundFlag(arr[78] != null ? arr[78].toString() : null);
		obj.setAutoPopulateToRefund(arr[79] != null ? arr[79].toString() : null);
		obj.setAdjustmentReferenceNo(arr[80] != null ? arr[80].toString() : null);
		if (arr[81] != null) {
			String strdate = arr[81].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAdjustmentReferenceDate(newDate);
		} else {
			obj.setAdjustmentReferenceDate(null);
		}
		obj.setTaxableValueAdjusted(arr[82] != null ? arr[82].toString() : null);
		obj.setIntegratedTaxAmountAdjusted(arr[83] != null ? arr[83].toString() : null);
		obj.setCentralTaxAmountAdjusted(arr[84] != null ? arr[84].toString() : null);
		obj.setStateUTTaxAmountAdjusted(arr[85] != null ? arr[85].toString() : null);
		obj.setAdvaloremCessAmountAdjusted(arr[86] != null ? arr[86].toString() : null);
		obj.setSpecificCessAmountAdjusted(arr[87] != null ? arr[87].toString() : null);
		obj.setStateCessAmountAdjusted(arr[88] != null ? arr[88].toString() : null);
		obj.setReverseChargeFlag(arr[89] != null ? arr[89].toString() : null);
		obj.setEligibilityIndicator(arr[90] != null ? arr[90].toString() : null);
		obj.setCommonSupplyIndicator(arr[91] != null ? arr[91].toString() : null);
		obj.setAvailableIGST(arr[92] != null ? arr[92].toString() : null);
		obj.setAvailableCGST(arr[93] != null ? arr[93].toString() : null);
		obj.setAvailableSGST(arr[94] != null ? arr[94].toString() : null);
		obj.setAvailableCess(arr[95] != null ? arr[95].toString() : null);
		obj.setITCEntitlement(arr[96] != null ? arr[96].toString() : null);
		obj.setITCReversalIdentifier(arr[97] != null ? arr[97].toString() : null);
		obj.setReasonForCreditDebitNote(arr[98] != null ? arr[98].toString() : null);
		obj.setPurchaseVoucherNumber(arr[99] != null ? arr[99].toString() : null);
		if (arr[100] != null) {
			String strdate = arr[100].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPurchaseVoucherDate(newDate);
		} else {
			obj.setPurchaseVoucherDate(null);
		}
		if (arr[101] != null) {
			String strdate = arr[101].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPostingDate(newDate);
		} else {
			obj.setPostingDate(null);
		}
		obj.setPaymentVoucherNumber(arr[102] != null ? arr[102].toString() : null);
		if (arr[103] != null) {
			String strdate = arr[103].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPaymentDate(newDate);
		} else {
			obj.setPaymentDate(null);
		}
		obj.setContractNumber(arr[104] != null ? arr[104].toString() : null);
		if (arr[105] != null) {
			String strdate = arr[105].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setContractDate(newDate);
		} else {
			obj.setContractDate(null);
		}
		obj.setContractValue(arr[106] != null ? arr[106].toString() : null);
		obj.setUserDefinedField1(arr[107] != null ? arr[107].toString() : null);
		obj.setUserDefinedField2(arr[108] != null ? arr[108].toString() : null);
		obj.setUserDefinedField3(arr[109] != null ? arr[109].toString() : null);
		obj.setUserDefinedField4(arr[110] != null ? arr[110].toString() : null);
		obj.setUserDefinedField5(arr[111] != null ? arr[111].toString() : null);
		obj.setUserDefinedField6(arr[112] != null ? arr[112].toString() : null);
		obj.setUserDefinedField7(arr[113] != null ? arr[113].toString() : null);
		obj.setUserDefinedField8(arr[114] != null ? arr[114].toString() : null);
		obj.setUserDefinedField9(arr[115] != null ? arr[115].toString() : null);
		obj.setUserDefinedField10(arr[116] != null ? arr[116].toString() : null);
		obj.setUserDefinedField11(arr[117] != null ? arr[117].toString() : null);
		obj.setUserDefinedField12(arr[118] != null ? arr[118].toString() : null);
		obj.setUserDefinedField13(arr[119] != null ? arr[119].toString() : null);
		obj.setUserDefinedField14(arr[120] != null ? arr[120].toString() : null);
		obj.setUserDefinedField15(arr[121] != null ? arr[121].toString() : null);

		obj.setEWayBillNumber(arr[122] != null ? arr[122].toString() : null);

		if (arr[123] != null) {
			String strdate = arr[123].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setEWayBillDate(newDate);
		} else {
			obj.setEWayBillDate(null);
		}
		obj.setReturnType(arr[124] != null ? arr[124].toString() : null);
		obj.setDataCategory(arr[125] != null ? arr[125].toString() : null);
		obj.setTableNumber(arr[126] != null ? arr[126].toString() : null);
		obj.setASPInformationID(arr[127] != null ? arr[127].toString() : null);
		obj.setASPInformationDescription(arr[128] != null ? arr[128].toString() : null);
		obj.setInvoiceValueASP(arr[129] != null ? arr[129].toString() : null);
		obj.setUploadSource(arr[130] != null ? arr[130].toString() : null);
		obj.setSourceID(arr[131] != null ? arr[131].toString() : null);
		obj.setFileName(arr[132] != null ? arr[132].toString() : null);
		obj.setASPDateTime(arr[133] != null ? arr[133].toString() : null);
		return obj;

	}

	private String creategstnTransQueryString(String buildQuery,String buildHeader) {

		LOGGER.error("bufferString-------------------------->" + buildQuery);
		return " select '' as CATEGORY, LT.USER_RESPONSE, LT.SUGGESTED_RESPONSE, "
				+ "LT.CURRENT_REPORT_TYPe, LT.CURRENT_MISMATCH_REASON, '' AS RESPONSE_TAX_PERIOD, "
				+ "LT.PR_TAX_PERIOD AS RECON_TAX_PERIOD, MONTHNAME(HDR.DOC_DATE) AS CALENDAR_MONTH, "
				+ " '' AS ORG_MONTH, HDR.USER_ID AS USER_ID, HDR.SOURCE_FILENAME AS SOURCE_FLIENAME, "
				+ "HDR.PROFIT_CENTRE AS PROFIT_CENTRE, HDR.PLANT_CODE AS PLANT, HDR.DIVISION AS DIVISION, "
				+ "HDR.LOCATION AS LOCATION, HDR.PURCHASE_ORGANIZATION AS PURCHASE_ORGANISATION, "
				+ "HDR.USERACCESS1 AS USERACCESS1, HDR.USERACCESS2 AS USERACCESS2, HDR.USERACCESS3 AS USERACCESS3, "
				+ "HDR.USERACCESS4 AS USERACCESS4, HDR.USERACCESS5 AS USERACCESS5, HDR.USERACCESS6 AS USERACCESS6, "
				+ "ITM.GLCODE_TAXABLEVALUE AS GLCODE_TAXABLE_VALUE, ITM.GLCODE_IGST AS GL_CODE_IGST, "
				+ "ITM.GLCODE_CGST AS GL_CODE_CGST, ITM.GLCODE_SGST AS GL_CODE_SGST, "
				+ "ITM.GLCODE_ADV_CESS AS GL_CODE_ADVALOREM_CESS, ITM.GLCODE_SP_CESS AS GL_CODE_SPECIFIC_CESS, "
				+ "ITM.GLCODE_STATE_CESS AS GL_CODESTATE_CESS, HDR.RETURN_PERIOD, HDR.CUST_GSTIN, "
				+ "HDR.DOC_TYPE, HDR.SUPPLY_TYPE AS SUPPLY_TYPE, HDR.DOC_NUM, HDR.DOC_DATE, "
				+ "HDR.ORIGINAL_DOC_NUM, HDR.ORIGINAL_DOC_DATE, HDR.CRDR_PRE_GST AS CRDRPERGST, "
				+ "ITM.ITM_NO, HDR.SUPPLIER_GSTIN, HDR.CUST_SUPP_TYPE AS SUPPLIER_TYPE, "
				+ "HDR.DIFF_PERCENT, HDR.ORIG_SUPPLIER_GSTIN, HDR.CUST_SUPP_NAME AS PR_SUPPLIER_NAME, "
				+ "HDR.CUST_SUPP_CODE AS SUPPLIER_CODE, HDR.CUST_SUPP_ADDRESS1 AS SUPPLIER_ADDERSS1, "
				+ "HDR.CUST_SUPP_ADDRESS2 AS SUPPLIER_ADDERSS2, HDR.CUST_SUPP_ADDRESS3 AS SUPPLIER_ADDERSS3, "
				+ "HDR.CUST_SUPP_ADDRESS4 AS SUPPLIER_ADDERSS4, HDR.POS, HDR.STATE_APPLYING_CESS "
				+ "AS STATE_APPLYING_CESS, HDR.SHIP_PORT_CODE AS PORT_CODE, HDR.BILL_OF_ENTRY AS BILL_OF_ENTRY, "
				+ "HDR.BILL_OF_ENTRY_DATE AS BILL_OF_ENTRY_DATE, ITM.CIF_VALUE AS CIF_VALUE, "
				+ "ITM.CUSTOM_DUTY AS CUSTOM_DUTY, ITM.ITM_HSNSAC AS HSN_SAC, ITM.PRODUCT_CODE "
				+ "AS ITEM_CODE, ITM.ITM_DESCRIPTION AS ITEM_DESCRIPTION, ITM.ITM_TYPE AS CAT_OF_ITEM, "
				+ "ITM.ITM_UQC AS UNIT_OF_MEASURMENT, '' AS QUANTITY, HDR.SECTION7_OF_IGST_FLAG, "
				+ "ITM.TAXABLE_VALUE, ITM.IGST_RATE, ITM.IGST_AMT, ITM.CGST_RATE, ITM.CGST_AMT , "
				+ "ITM.SGST_RATE, ITM.SGST_AMT,ITM.CESS_RATE_ADVALOREM, "
				+ "ITM.CESS_AMT_ADVALOREM, ITM.CESS_RATE_SPECIFIC, "
				+ "ITM.CESS_AMT_SPECIFIC, ITM.STATECESS_RATE, ITM.STATECESS_AMT, "
				+ "ITM.OTHER_VALUES, ITM.LINE_ITEM_AMT, ITM.CLAIM_REFUND_FLAG, "
				+ "ITM.AUTOPOPULATE_TO_REFUND, ITM.ADJ_REF_NO, ITM.ADJ_REF_DATE, "
				+ "ITM.ADJ_TAXABLE_VALUE, ITM.ADJ_IGST_AMT, ITM.ADJ_CGST_AMT, ITM.ADJ_SGST_AMT, "
				+ "ITM.ADJ_CESS_AMT_ADVALOREM, ITM.ADJ_CESS_AMT_SPECIFIC, ITM.ADJ_STATECESS_AMT, "
				+ "ITM.REVERSE_CHARGE, ITM.ELIGIBILITY_INDICATOR, ITM.COMMON_SUP_INDICATOR, "
				+ "ITM.AVAILABLE_IGST, ITM.AVAILABLE_CGST, ITM.AVAILABLE_SGST, ITM.AVAILABLE_CESS, "
				+ "ITM.ITC_ENTITLEMENT, ITM.ITC_REVERSAL_IDENTIFER, ITM.CRDR_REASON, "
				+ "ITM.PURCHASE_VOUCHER_NUM, ITM.PURCHASE_VOUCHER_DATE, ITM.POSTING_DATE, "
				+ "ITM.PAYMENT_VOUCHER_NUM, ITM.PAYMENT_DATE, ITM.CONTRACT_NUMBER, "
				+ "ITM. CONTRACT_DATE, ITM. CONTRACT_VALUE, ITM.USERDEFINED_FIELD1 ,"
				+ "ITM.USERDEFINED_FIELD2 , ITM.USERDEFINED_FIELD3 , ITM.USERDEFINED_FIELD4 , "
				+ "ITM.USERDEFINED_FIELD5 , ITM.USERDEFINED_FIELD6 , "
				+ "ITM.USERDEFINED_FIELD7 , ITM.USERDEFINED_FIELD8 , "
				+ "ITM.USERDEFINED_FIELD9 , ITM.USERDEFINED_FIELD10 , "
				+ "ITM.USERDEFINED_FIELD11 , ITM.USERDEFINED_FIELD12 , "
				+ "ITM.USERDEFINED_FIELD13, ITM.USERDEFINED_FIELD14 , "
				+ "ITM.USERDEFINED_FIELD15 , ITM. EWAY_BILL_NUM, ITM. EWAY_BILL_DATE, "
				+ "HDR.RETURN_TYPE, HDR.TAX_DOC_TYPE as DATA_CATEGORY, "
				+ "HDR.TABLE_SECTION, TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) " + "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "HDR.DOC_AMT, Case when HDR.FILE_ID IS NULL "
				+ "THEN 'API' when HDR.FILE_ID IS NOT NULL THEN 'WEB UPLOAD' END "
				+ "UPLOAD_SOURCE,HDR.CREATED_BY AS SOURCE_ID, FIL.FILE_NAME "
				+ "AS FILE_NAME, HDR.CREATED_ON AS ASP_DATE_TIME " + "FROM ANX_INWARD_DOC_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON HDR.ID= ERRH.DOC_HEADER_ID "
				+ "INNER JOIN ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT OUTER JOIN TF_INWARD_ITEM_ERROR_INFO () ERRI " + "ON ITM.DOC_HEADER_ID= ERRI.DOC_HEADER_ID AND "
				+ "ITM.ITEM_INDEX = ERRI.ITEM_INDEX "
				+ "AND ITM.ITM_NO = ERRI.ITM_NO LEFT OUTER JOIN TBL_LINK_2A_PR LT ON HDR.ID = LT.PR_ID "
				+ "AND ITM.DOC_HEADER_ID = LT.PR_ID "
				+ "AND RECON_REPORT_CONFIG_ID = (SELECT DISTINCT RECON_REPORT_CONFIG_ID FROM TBL_LINK_2A_PR "
				+ "WHERE " +buildHeader
				+ " ORDER BY "
				+ "RECON_REPORT_CONFIG_ID DESC LIMIT 1 OFFSET 0) AND LT.IS_DELETED = FALSE AND LT.IS_ACTIVE = TRUE "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID = FIL.ID where HDR.IS_DELETE = FALSE "
				+ "AND HDR.IS_PROCESSED = TRUE AND "
				+ "HDR.RETURN_TYPE = 'GSTR2' "
				+ buildQuery ;

	}

}

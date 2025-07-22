package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1inwardapiErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx1InwardRawFileErrorReportsDaoImpl")
public class Anx1InwardRawFileErrorReportsDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static String errorType = null;
	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	public List<Anx1inwardapiErrorRecordsDto> getInwardErrorReports(
			Anx1FileStatusReportsReqDto request) {

		Long fileId = request.getFileId();
		errorType = request.getErrorType();

		StringBuffer buildQuery = new StringBuffer();
		StringBuffer buildQuery1 = new StringBuffer();
		if (fileId != null) {
			buildQuery.append(" HDR.FILE_ID= :fileId");
		}

		if (errorType.equalsIgnoreCase(DownloadReportsConstant.ERRORACTIVE)) {
			buildQuery1.append(" AND IS_DELETE = FALSE");
		}
		if (errorType.equalsIgnoreCase(DownloadReportsConstant.ERRORINACTIVE)) {
			buildQuery1.append(" AND IS_DELETE = TRUE");
		}

		String queryStr = createErrorQueryString(buildQuery.toString(),
				buildQuery1.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();
		return list.stream().map(o -> convertError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1inwardapiErrorRecordsDto convertError(Object[] arr) {
		Anx1inwardapiErrorRecordsDto obj = new Anx1inwardapiErrorRecordsDto();

		obj.setUserID(arr[1] != null ? arr[1].toString() : null);
		obj.setSourceFileName(arr[2] != null ? arr[2].toString() : null);
		obj.setProfitCentre(arr[3] != null ? arr[3].toString() : null);
		obj.setPlant(arr[4] != null ? arr[4].toString() : null);
		obj.setDivision(arr[5] != null ? arr[5].toString() : null);
		obj.setLocation(arr[6] != null ? arr[6].toString() : null);
		obj.setBillOfEntry(arr[7] != null ? arr[7].toString() : null);
		obj.setITCEntitlement(arr[8] != null ? arr[8].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[9] != null) {
				String strdate = arr[9].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setBillOfEntryDate(newDate);
			} else {
				obj.setBillOfEntryDate(null);
			}
		} else {
			obj.setBillOfEntryDate(arr[9] != null ? arr[9].toString() : null);
		}
		obj.setPurchaseOrganisation(
				arr[10] != null ? arr[10].toString() : null);
		obj.setUserAccess1(arr[11] != null ? arr[11].toString() : null);
		obj.setUserAccess2(arr[12] != null ? arr[12].toString() : null);
		obj.setUserAccess3(arr[13] != null ? arr[13].toString() : null);
		obj.setUserAccess4(arr[14] != null ? arr[14].toString() : null);
		obj.setUserAccess5(arr[15] != null ? arr[15].toString() : null);
		obj.setUserAccess6(arr[16] != null ? arr[16].toString() : null);
		obj.setITCReversalIdentifier(
				arr[17] != null ? arr[17].toString() : null);
		obj.setReturnPeriod(arr[18] != null ? arr[18].toString() : null);
		obj.setSupplierGSTIN(arr[19] != null ? arr[19].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[20] != null) {
				String strdate = arr[20].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setPostingDate(newDate);
			} else {
				obj.setPostingDate(null);
			}
		} else {
			obj.setPostingDate(arr[20] != null ? arr[20].toString() : null);
		}
		obj.setDocumentType(arr[21] != null ? arr[21].toString() : null);
		obj.setSupplyType(arr[122] != null ? arr[122].toString() : null);
		obj.setDocumentNumber(arr[23] != null ? arr[23].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[24] != null) {
				String strdate = arr[24].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setDocumentDate(newDate);
			} else {
				obj.setDocumentDate(null);
			}
		} else {
			obj.setDocumentDate(arr[24] != null ? arr[24].toString() : null);
		}
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[124] != null) {
				String strdate = arr[124].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setOriginalDocumentDate(newDate);
			} else {
				obj.setOriginalDocumentDate(null);
			}
		} else {
			obj.setOriginalDocumentDate(
					arr[124] != null ? arr[124].toString() : null);
		}
		obj.setOriginalDocumentNumber(
				arr[123] != null ? arr[123].toString() : null);
		obj.setCRDRPreGST(arr[27] != null ? arr[27].toString() : null);
		obj.setRecipientGSTIN(arr[28] != null ? arr[28].toString() : null);
		obj.setSupplierType(arr[29] != null ? arr[29].toString() : null);
		obj.setDifferentialFlag(arr[30] != null ? arr[30].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[31] != null ? arr[31].toString() : null);
		obj.setSupplierName(arr[32] != null ? arr[32].toString() : null);
		obj.setSupplierCode(arr[33] != null ? arr[33].toString() : null);
		obj.setSupplierAddress1(arr[34] != null ? arr[34].toString() : null);
		obj.setSupplierAddress2(arr[35] != null ? arr[35].toString() : null);
		obj.setSupplierAddress3(arr[36] != null ? arr[36].toString() : null);
		obj.setSupplierAddress4(arr[37] != null ? arr[37].toString() : null);
		obj.setPOS(arr[38] != null ? arr[38].toString() : null);
		obj.setStateApplyingCess(arr[39] != null ? arr[39].toString() : null);
		obj.setPortCode(arr[40] != null ? arr[40].toString() : null);
		obj.setSection7ofIGSTFlag(arr[41] != null ? arr[41].toString() : null);
		obj.setInvoiceValue(arr[125] != null ? arr[125].toString() : null);
		obj.setReverseChargeFlag(arr[43] != null ? arr[43].toString() : null);
		obj.setClaimRefundFlag(arr[44] != null ? arr[44].toString() : null);
		obj.setAutoPopulateToRefund(
				arr[45] != null ? arr[45].toString() : null);
		obj.setEWayBillNumber(arr[46] != null ? arr[46].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[47] != null) {
				String strdate = arr[47].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setEWayBillDate(newDate);
			} else {
				obj.setEWayBillDate(null);
			}
		} else {
			obj.setEWayBillDate(arr[47] != null ? arr[47].toString() : null);
		}
		obj.setGLCodeTaxableValue(arr[51] != null ? arr[51].toString() : null);
		obj.setGLCodeIGST(arr[52] != null ? arr[52].toString() : null);
		obj.setGLCodeCGST(arr[53] != null ? arr[53].toString() : null);
		obj.setGLCodeSGST(arr[54] != null ? arr[54].toString() : null);
		obj.setGLCodeAdvaloremCess(arr[55] != null ? arr[55].toString() : null);
		obj.setGLCodeSpecificCess(arr[56] != null ? arr[56].toString() : null);
		obj.setGLCodeStateCess(arr[57] != null ? arr[57].toString() : null);
		obj.setLineNumber(arr[58] != null ? arr[58].toString() : null);
		obj.setHSNorSAC(arr[59] != null ? arr[59].toString() : null);
		obj.setItemCode(arr[60] != null ? arr[60].toString() : null);
		obj.setItemDescription(arr[61] != null ? arr[61].toString() : null);
		obj.setCategoryOfItem(arr[62] != null ? arr[62].toString() : null);
		obj.setUnitOfMeasurement(arr[63] != null ? arr[63].toString() : null);
		obj.setQuantity(arr[64] != null ? arr[64].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[65] != null ? arr[65].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[66] != null ? arr[66].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[67] != null) {
				String strdate = arr[67].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setPurchaseVoucherDate(newDate);
			} else {
				obj.setPurchaseVoucherDate(null);
			}
		} else {
			obj.setPurchaseVoucherDate(
					arr[67] != null ? arr[67].toString() : null);
		}
		obj.setPurchaseVoucherNumber(
				arr[68] != null ? arr[68].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[69] != null) {
				String strdate = arr[69].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setPaymentDate(newDate);
			} else {
				obj.setPaymentDate(null);
			}
		} else {
			obj.setPaymentDate(arr[69] != null ? arr[69].toString() : null);
		}
		obj.setPaymentVoucherNumber(
				arr[70] != null ? arr[70].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[71] != null) {
				String strdate = arr[71].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setContractDate(newDate);
			} else {
				obj.setContractDate(null);
			}
		} else {
			obj.setContractDate(arr[71] != null ? arr[71].toString() : null);
		}
		obj.setContractValue(arr[72] != null ? arr[72].toString() : null);
		obj.setAvailableCGST(arr[73] != null ? arr[73].toString() : null);
		obj.setAvailableSGST(arr[74] != null ? arr[74].toString() : null);
		obj.setCIFValue(arr[75] != null ? arr[75].toString() : null);
		obj.setCustomDuty(arr[76] != null ? arr[76].toString() : null);
		obj.setUserDefinedField1(arr[77] != null ? arr[77].toString() : null);
		obj.setUserDefinedField2(arr[78] != null ? arr[78].toString() : null);
		obj.setUserDefinedField3(arr[79] != null ? arr[79].toString() : null);
		obj.setUserDefinedField4(arr[80] != null ? arr[80].toString() : null);
		obj.setUserDefinedField5(arr[81] != null ? arr[81].toString() : null);
		obj.setUserDefinedField6(arr[82] != null ? arr[82].toString() : null);
		obj.setUserDefinedField7(arr[83] != null ? arr[83].toString() : null);
		obj.setUserDefinedField8(arr[84] != null ? arr[84].toString() : null);
		obj.setUserDefinedField9(arr[85] != null ? arr[85].toString() : null);
		obj.setUserDefinedField10(arr[86] != null ? arr[86].toString() : null);
		obj.setUserDefinedField11(arr[87] != null ? arr[87].toString() : null);
		obj.setUserDefinedField12(arr[88] != null ? arr[88].toString() : null);
		obj.setUserDefinedField13(arr[89] != null ? arr[89].toString() : null);
		obj.setUserDefinedField14(arr[90] != null ? arr[90].toString() : null);
		obj.setUserDefinedField15(arr[91] != null ? arr[91].toString() : null);
		obj.setOtherValue(arr[92] != null ? arr[92].toString() : null);
		obj.setAdjustmentReferenceNo(
				arr[93] != null ? arr[93].toString() : null);
		if ((DownloadReportsConstant.ERRORACTIVE).equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			if (arr[94] != null) {
				String strdate = arr[94].toString();
				DateTimeFormatter f = new DateTimeFormatterBuilder()
						.appendPattern(OLDFARMATTER).toFormatter();
				LocalDate parsedDate = LocalDate.parse(strdate, f);
				DateTimeFormatter f2 = DateTimeFormatter
						.ofPattern(NEWFARMATTER);
				String newDate = parsedDate.format(f2);
				obj.setAdjustmentReferenceDate(newDate);
			} else {
				obj.setAdjustmentReferenceDate(null);
			}
		} else {
			obj.setAdjustmentReferenceDate(
					arr[94] != null ? arr[94].toString() : null);
		}
		obj.setTaxableValue(arr[95] != null ? arr[95].toString() : null);
		obj.setIntegratedTaxRate(arr[96] != null ? arr[96].toString() : null);
		obj.setIntegratedTaxAmount(arr[97] != null ? arr[97].toString() : null);
		obj.setCentralTaxRate(arr[98] != null ? arr[98].toString() : null);
		obj.setCentralTaxAmount(arr[99] != null ? arr[99].toString() : null);
		obj.setStateUTTaxRate(arr[100] != null ? arr[100].toString() : null);
		obj.setStateUTTaxAmount(arr[101] != null ? arr[101].toString() : null);
		obj.setSpecificCessRate(arr[102] != null ? arr[102].toString() : null);
		obj.setSpecificCessAmount(
				arr[103] != null ? arr[103].toString() : null);
		obj.setContractNumber(arr[104] != null ? arr[104].toString() : null);
		obj.setAdvaloremCessRate(arr[105] != null ? arr[105].toString() : null);
		obj.setAvailableIGST(arr[106] != null ? arr[106].toString() : null);
		obj.setAdvaloremCessAmount(
				arr[107] != null ? arr[107].toString() : null);
		obj.setStateCessRate(arr[108] != null ? arr[108].toString() : null);
		obj.setAvailableCess(arr[109] != null ? arr[109].toString() : null);
		obj.setStateCessAmount(arr[110] != null ? arr[110].toString() : null);
		obj.setTaxableValueAdjusted(
				arr[111] != null ? arr[111].toString() : null);
		obj.setIntegratedTaxAmountAdjusted(
				arr[112] != null ? arr[112].toString() : null);
		obj.setCentralTaxAmountAdjusted(
				arr[113] != null ? arr[113].toString() : null);
		obj.setStateUTTaxAmountAdjusted(
				arr[114] != null ? arr[114].toString() : null);
		obj.setAdvaloremCessAmountAdjusted(
				arr[115] != null ? arr[115].toString() : null);
		obj.setSpecificCessAmountAdjusted(
				arr[116] != null ? arr[116].toString() : null);
		obj.setStateCessAmountAdjusted(
				arr[117] != null ? arr[117].toString() : null);
		obj.setASPErrorCode(arr[118] != null ? arr[118].toString() : null);
		obj.setASPErrorDescription(
				arr[119] != null ? arr[119].toString() : null);
		obj.setASPInformationID(arr[120] != null ? arr[120].toString() : null);
		obj.setASPInformationDescription(
				arr[121] != null ? arr[121].toString() : null);
		obj.setEligibilityIndicator(
				arr[126] != null ? arr[126].toString() : null);

		return obj;
	}

	private String createErrorQueryString(String buildQuery,
			String buildQuery1) {
		String queryStr = "";
		if ((DownloadReportsConstant.ERRORTOTAL).equalsIgnoreCase(errorType)) {

			queryStr = "(SELECT HDR.ID AS HID,"
					+ "TO_CHAR(ITM.USER_ID) AS USER_ID,"
					+ "TO_CHAR(ITM.SOURCE_FILENAME) AS SOURCE_FILENAME,"
					+ "TO_CHAR(ITM.PROFIT_CENTRE) AS PROFIT_CENTRE,"
					+ "TO_CHAR(ITM.PLANT_CODE) AS PLANT_CODE,"
					+ "TO_CHAR(ITM.DIVISION)  AS DIVISION ,"
					+ "TO_CHAR(ITM.LOCATION) AS LOCATION,"
					+ "TO_CHAR(ITM.BILL_OF_ENTRY) AS BILL_OF_ENTRY,"
					+ "TO_CHAR(ITM.ITC_ENTITLEMENT) AS ITC_ENTITLEMENT,"
					+ "TO_CHAR(ITM.BILL_OF_ENTRY_DATE) AS BILL_OF_ENTRY_DATE,"
					+ "TO_CHAR(ITM.PURCHASE_ORGANIZATION) AS PURCHASE_ORGANIZATION,"
					+ "TO_CHAR(ITM.USERACCESS1) AS USERACCESS1,"
					+ "TO_CHAR(ITM.USERACCESS2) AS USERACCESS2,"
					+ "TO_CHAR(ITM.USERACCESS3) AS USERACCESS3,"
					+ "TO_CHAR(ITM.USERACCESS4) AS USERACCESS4,"
					+ "TO_CHAR(ITM.USERACCESS5) AS USERACCESS5,"
					+ "TO_CHAR(ITM.USERACCESS6) AS USERACCESS6,"
					+ "TO_CHAR(HDR.ITC_REVERSAL_IDENTIFER) AS ITC_REVERSAL_IDENTIFER,"
					+ "TO_CHAR(ITM.RETURN_PERIOD) AS RETURN_PERIOD,"
					+ "TO_CHAR(HDR.SUPPLIER_GSTIN) AS SUPPLIER_GSTIN,"
					+ "TO_CHAR(ITM.POSTING_DATE) AS POSTING_DATE,"
					+ "TO_CHAR(HDR.DOC_TYPE) AS DOC_TYPE,"
					+ "TO_CHAR(HDR.SUPPLY_TYPE) AS SUPPLY_TYPE,"
					+ "TO_CHAR(HDR.DOC_NUM) AS DOC_NUM, "
					+ "TO_CHAR(ITM.DOC_DATE) AS DOC_DATE,"
					+ "TO_CHAR(HDR.ORIGINAL_DOC_DATE) AS ORIGINAL_DOC_DATE,"
					+ "TO_CHAR(HDR.ORIGINAL_DOC_NUM) AS ORIGINAL_DOC_NUM,"
					+ "TO_CHAR(ITM.CRDR_PRE_GST) AS CRDR_PRE_GST,"
					+ "TO_CHAR(HDR.CUST_GSTIN) AS CUST_GSTIN,"
					+ "TO_CHAR(ITM.CUST_SUPP_TYPE) AS CUST_SUPP_TYPE,"
					+ "TO_CHAR(ITM.DIFF_PERCENT) AS DIFF_PERCENT,"
					+ "TO_CHAR(ITM.ORIG_SUPPLIER_GSTIN) AS ORIG_SUPPLIER_GSTIN,"
					+ "TO_CHAR(ITM.CUST_SUPP_NAME) AS CUST_SUPP_NAME,"
					+ "TO_CHAR(ITM.CUST_SUPP_CODE) AS CUST_SUPP_CODE,"
					+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS1) AS CUST_SUPP_ADDRESS1,"
					+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS2) AS CUST_SUPP_ADDRESS2,"
					+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS3) AS CUST_SUPP_ADDRESS3,"
					+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS4) AS CUST_SUPP_ADDRESS4,"
					+ "TO_CHAR(ITM.POS) AS POS, "
					+ "TO_CHAR(ITM.STATE_APPLYING_CESS) AS STATE_APPLYING_CESS, "
					+ "TO_CHAR(ITM.SHIP_PORT_CODE) AS SHIP_PORT_CODE,"
					+ "TO_CHAR(ITM.SECTION7_OF_IGST_FLAG) AS SECTION7_OF_IGST_FLAG,"
					+ "TO_CHAR(HDR.DOC_AMT) AS INV_VALUE,"
					+ "TO_CHAR(ITM.REVERSE_CHARGE) AS REVERSE_CHARGE,"
					+ "TO_CHAR(ITM.CLAIM_REFUND_FLAG) AS CLAIM_REFUND_FLAG,"
					+ "TO_CHAR(ITM.AUTOPOPULATE_TO_REFUND) AS AUTOPOPULATE_TO_REFUND,"
					+ "TO_CHAR(ITM.EWAY_BILL_NUM) AS EWAY_BILL_NUM,"
					+ "TO_CHAR(ITM.EWAY_BILL_DATE) AS EWAY_BILL_DATE, HDR.FILE_ID,"
					+ "FIL.ID, ITM.DOC_HEADER_ID, TO_CHAR(ITM.GLCODE_TAXABLEVALUE) "
					+ "AS GLCODE_TAXABLEVALUE, TO_CHAR(ITM.GLCODE_IGST) AS GLCODE_IGST,"
					+ "TO_CHAR(ITM.GLCODE_CGST) AS GLCODE_CGST, TO_CHAR(ITM.GLCODE_SGST) "
					+ "AS GLCODE_SGST, TO_CHAR(ITM.GLCODE_ADV_CESS) AS GLCODE_ADV_CESS, "
					+ "TO_CHAR(ITM.GLCODE_SP_CESS) AS GLCODE_SP_CESS,"
					+ "TO_CHAR(ITM.GLCODE_STATE_CESS) AS GLCODE_STATE_CESS,"
					+ "TO_CHAR(ITM.ITM_NO) AS ITM_NO , TO_CHAR(ITM.ITM_HSNSAC) AS ITM_HSNSAC,"
					+ "TO_CHAR(ITM.PRODUCT_CODE) AS PRODUCT_CODE,"
					+ "TO_CHAR(ITM.ITM_DESCRIPTION) AS ITM_DESCRIPTION,"
					+ "TO_CHAR(ITM.ITM_TYPE) AS ITM_TYPE,"
					+ "TO_CHAR(ITM.ITM_UQC) AS ITM_UQC,"
					+ "TO_CHAR(ITM.ITM_QTY) AS ITM_QTY,"
					+ "TO_CHAR(ITM.CRDR_REASON) AS CRDR_REASON,"
					+ "TO_CHAR(ITM.COMMON_SUP_INDICATOR) AS COMMON_SUP_INDICATOR,"
					+ "TO_CHAR(ITM.PURCHASE_VOUCHER_DATE) AS PURCHASE_VOUCHER_DATE,"
					+ "TO_CHAR(ITM.PURCHASE_VOUCHER_NUM) AS PURCHASE_VOUCHER_NUM,"
					+ "TO_CHAR(ITM.PAYMENT_DATE) AS PAYMENT_DATE,"
					+ "TO_CHAR(ITM.PAYMENT_VOUCHER_NUM) AS PAYMENT_VOUCHER_NUM,"
					+ "TO_CHAR(ITM.CONTRACT_DATE) AS CONTRACT_DATE,"
					+ "TO_CHAR(ITM.CONTRACT_VALUE) AS CONTRACT_VALUE,"
					+ "TO_CHAR(ITM.AVAILABLE_CGST) AS AVAILABLE_CGST,"
					+ "TO_CHAR(ITM.AVAILABLE_SGST) AS AVAILABLE_SGST,"
					+ "TO_CHAR(ITM.CIF_VALUE) AS CIF_VALUE,"
					+ "TO_CHAR(ITM.CUSTOM_DUTY) AS CUSTOM_DUTY,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD1) AS USERDEFINED_FIELD1,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD2) AS USERDEFINED_FIELD2,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD3) AS USERDEFINED_FIELD3,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD4) AS USERDEFINED_FIELD4,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD5) AS USERDEFINED_FIELD5,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD6) AS USERDEFINED_FIELD6,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD7) AS USERDEFINED_FIELD7,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD8) AS USERDEFINED_FIELD8,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD9) AS USERDEFINED_FIELD9,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD10) AS USERDEFINED_FIELD10,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD11) AS USERDEFINED_FIELD11,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD12) AS USERDEFINED_FIELD12,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD13) AS USERDEFINED_FIELD13,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD14) AS USERDEFINED_FIELD14,"
					+ "TO_CHAR(ITM.USERDEFINED_FIELD15) AS USERDEFINED_FIELD15,"
					+ "TO_CHAR(ITM.OTHER_VALUES) AS OTHER_VALUES,"
					+ "TO_CHAR(ITM.ADJ_REF_NO) AS ADJ_REF_NO,"
					+ "TO_CHAR(ITM.ADJ_REF_DATE) AS ADJ_REF_DATE,"
					+ "TO_CHAR(ITM.TAXABLE_VALUE) AS TAXABLE_VALUE,"
					+ "TO_CHAR(ITM.IGST_RATE) AS IGST_RATE, "
					+ "TO_CHAR(ITM.IGST_AMT) AS IGST_AMT,"
					+ "TO_CHAR(ITM.CGST_RATE) AS CGST_RATE,"
					+ "TO_CHAR(ITM.CGST_AMT) AS CGST_AMT,"
					+ "TO_CHAR(ITM.SGST_RATE) AS SGST_RATE,"
					+ "TO_CHAR(ITM.SGST_AMT) AS SGST_AMT,"
					+ "TO_CHAR(ITM.CESS_RATE_SPECIFIC) AS CESS_RATE_SPECIFIC,"
					+ "TO_CHAR(ITM.CESS_AMT_SPECIFIC) AS CESS_AMT_SPECIFIC,"
					+ "TO_CHAR(ITM.CONTRACT_NUMBER) AS CONTRACT_NUMBER,"
					+ "TO_CHAR(ITM.CESS_RATE_ADVALOREM) AS CESS_RATE_ADVALOREM,"
					+ "TO_CHAR(ITM.AVAILABLE_IGST) AS AVAILABLE_IGST,"
					+ "TO_CHAR(ITM.CESS_AMT_ADVALOREM) AS CESS_AMT_ADVALOREM,"
					+ "TO_CHAR(ITM.STATECESS_RATE) AS STATECESS_RATE,"
					+ "TO_CHAR(ITM.AVAILABLE_CESS) AS AVAILABLE_CESS,"
					+ "TO_CHAR(ITM.STATECESS_AMT) AS STATECESS_AMT,"
					+ "TO_CHAR(ITM.ADJ_TAXABLE_VALUE) AS ADJ_TAXABLE_VALUE,"
					+ "TO_CHAR(ITM.ADJ_IGST_AMT) AS ADJ_IGST_AMT,"
					+ "TO_CHAR(ITM.ADJ_CGST_AMT) AS ADJ_CGST_AMT,"
					+ "TO_CHAR(ITM.ADJ_SGST_AMT) AS ADJ_SGST_AMT,"
					+ "TO_CHAR(ITM.ADJ_CESS_AMT_ADVALOREM) AS ADJ_CESS_AMT_ADVALOREM,"
					+ "TO_CHAR(ITM.ADJ_CESS_AMT_SPECIFIC) AS ADJ_CESS_AMT_SPECIFIC,"
					+ "TO_CHAR(ITM.ADJ_STATECESS_AMT) AS ADJ_STATECESS_AMT,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP,"
					+ "TO_CHAR(ITM.SUPPLY_TYPE) AS ITM_SUPPLY_TYPE,"
					+ "TO_CHAR(ITM.ORIGINAL_DOC_NUM) AS ITM_ORIGINAL_DOC_NUM,"
					+ "TO_CHAR(ITM.ORIGINAL_DOC_DATE) AS ITM_ORIGINAL_DOC_DATE,"
					+ "TO_CHAR(ITM.LINE_ITEM_AMT) AS ITM_INV_VALUE,"
					+ "TO_CHAR(ITM.ELIGIBILITY_INDICATOR) "
					+ "AS ITM_ELIGIBILITY_INDICATOR" + " FROM "
					+ "ANX_INWARD_DOC_HEADER HDR LEFT OUTER JOIN "
					+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON HDR.ID="
					+ "ERRH.DOC_HEADER_ID INNER JOIN ANX_INWARD_DOC_ITEM ITM ON "
					+ "HDR.ID=ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD "
					+ "= ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN "
					+ "TF_INWARD_ITEM_ERROR_INFO () ERRI ON "
					+ "ITM.DOC_HEADER_ID=ERRI.DOC_HEADER_ID AND "
					+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) "
					+ "AND "
					+ "IFNULL(ITM.ITM_NO,'0') = IFNULL(ERRI.ITM_NO,'0') "
					+ "INNER JOIN FILE_STATUS FIL ON "
					+ " HDR.FILE_ID=FIL.ID WHERE IS_ERROR=TRUE "
					+ " AND IS_DELETE = FALSE AND ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID FROM ANX_INWARD_DOC_ERROR "
					+ "WHERE VAL_TYPE = 'BV' ) AND " + buildQuery
					+ " AND IS_DELETE = FALSE  " + " ORDER BY HDR.ID) "
					+ " UNION ALL "
					+ "(SELECT HDR.ID AS HID,ITM.USER_ID,ITM.SOURCE_FILENAME,"
					+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,ITM.DIVISION,ITM.LOCATION,"
					+ "ITM.BILL_OF_ENTRY,ITM.ITC_ENTITLEMENT,"
					+ "ITM.BILL_OF_ENTRY_DATE,ITM.PURCHASE_ORGANIZATION,"
					+ "ITM.USERACCESS1,ITM.USERACCESS2,ITM.USERACCESS3,"
					+ "ITM.USERACCESS4,ITM.USERACCESS5,ITM.USERACCESS6,"
					+ "HDR.ITC_REVERSAL_IDENTIFER,ITM.RETURN_PERIOD,"
					+ "HDR.SUPPLIER_GSTIN,ITM.POSTING_DATE,HDR.DOC_TYPE,"
					+ "HDR.SUPPLY_TYPE,HDR.DOC_NUM,ITM.DOC_DATE,"
					+ "HDR.ORIGINAL_DOC_DATE,HDR.ORIGINAL_DOC_NUM,ITM.CRDR_PRE_GST,"
					+ "HDR.CUST_GSTIN,ITM.CUST_SUPP_TYPE,ITM.DIFF_PERCENT,"
					+ "ITM.ORIG_SUPPLIER_GSTIN,ITM.CUST_SUPP_NAME,ITM.CUST_SUPP_CODE,"
					+ "ITM.CUST_SUPP_ADDRESS1,ITM.CUST_SUPP_ADDRESS2,"
					+ "ITM.CUST_SUPP_ADDRESS3,ITM.CUST_SUPP_ADDRESS4,ITM.POS, "
					+ "ITM.STATE_APPLYING_CESS,ITM.SHIP_PORT_CODE,"
					+ "ITM.SECTION7_OF_IGST_FLAG,HDR.DOC_AMT,ITM.REVERSE_CHARGE,"
					+ "ITM.CLAIM_REFUND_FLAG,ITM.AUTOPOPULATE_TO_REFUND,"
					+ "ITM.EWAY_BILL_NUM,ITM.EWAY_BILL_DATE, HDR.FILE_ID,FIL.ID,"
					+ "ITM.DOC_HEADER_ID,ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,"
					+ "ITM.GLCODE_CGST, ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS, "
					+ "ITM.GLCODE_SP_CESS,ITM.GLCODE_STATE_CESS,ITM.ITM_NO, "
					+ "ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,ITM.ITM_DESCRIPTION,"
					+ "ITM.ITM_TYPE,ITM.ITM_UQC,ITM.ITM_QTY,"
					+ "ITM.CRDR_REASON,"
					+ "ITM.COMMON_SUP_INDICATOR,ITM.PURCHASE_VOUCHER_DATE,"
					+ "ITM.PURCHASE_VOUCHER_NUM,ITM.PAYMENT_DATE,"
					+ "ITM.PAYMENT_VOUCHER_NUM,ITM.CONTRACT_DATE,"
					+ "ITM.CONTRACT_VALUE," + "ITM.AVAILABLE_CGST,"
					+ "ITM.AVAILABLE_SGST," + "ITM.CIF_VALUE,"
					+ "ITM.CUSTOM_DUTY,"
					+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
					+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
					+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
					+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
					+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
					+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,"
					+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
					+ "ITM.USERDEFINED_FIELD15," + "ITM.OTHER_VALUES,"
					+ "ITM.ADJ_REF_NO,"
					+ "ITM.ADJ_REF_DATE,ITM.TAXABLE_VALUE,ITM.IGST_RATE,"
					+ "ITM.IGST_AMT," + "ITM.CGST_RATE," + "ITM.CGST_AMT,"
					+ "ITM.SGST_RATE," + "ITM.SGST_AMT,"
					+ "ITM.CESS_RATE_SPECIFIC," + "ITM.CESS_AMT_SPECIFIC,"
					+ "ITM.CONTRACT_NUMBER," + "ITM.CESS_RATE_ADVALOREM,"
					+ "ITM.AVAILABLE_IGST," + "ITM.CESS_AMT_ADVALOREM,"
					+ "ITM.STATECESS_RATE," + "ITM.AVAILABLE_CESS,"
					+ "ITM.STATECESS_AMT," + "ITM.ADJ_TAXABLE_VALUE,"
					+ "ITM.ADJ_IGST_AMT," + "ITM.ADJ_CGST_AMT,"
					+ "ITM.ADJ_SGST_AMT," + "ITM.ADJ_CESS_AMT_ADVALOREM,"
					+ "ITM.ADJ_CESS_AMT_SPECIFIC," + "ITM.ADJ_STATECESS_AMT,"
					+ "TRIM(',' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
					+ "TRIM(',' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP,"
					+ "TRIM(',' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(',' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP," + "ITM.SUPPLY_TYPE,"
					+ "ITM.ORIGINAL_DOC_NUM," + "ITM.ORIGINAL_DOC_DATE,"
					+ "ITM.LINE_ITEM_AMT," + "ITM.ELIGIBILITY_INDICATOR "
					+ " FROM " + "ANX_INWARD_ERROR_HEADER HDR LEFT OUTER JOIN "
					+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON HDR.ID="
					+ "ERRH.DOC_HEADER_ID INNER JOIN ANX_INWARD_ERROR_ITEM ITM "
					+ "ON HDR.ID=ITM.DOC_HEADER_ID LEFT OUTER JOIN "
					+ "TF_INWARD_ITEM_ERROR_INFO () ERRI ON ITM.DOC_HEADER_ID="
					+ "ERRI.DOC_HEADER_ID AND "
					+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) AND "
					+ "IFNULL(ITM.ITM_NO,'0') = IFNULL(ERRI.ITM_NO,'0') "
					+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
					+ "WHERE IS_ERROR='true'  AND ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID FROM "
					+ "ANX_INWARD_DOC_ERROR WHERE VAL_TYPE = 'SV' ) " + " AND "
					+ buildQuery 
					+ " ORDER BY HDR.ID )";

		} else if ((DownloadReportsConstant.ERRORACTIVE)
				.equalsIgnoreCase(errorType)
				|| (DownloadReportsConstant.ERRORINACTIVE)
						.equalsIgnoreCase(errorType)) {
			queryStr = "SELECT HDR.ID AS HID,ITM.USER_ID "
					+ "AS USER_ID,ITM.SOURCE_FILENAME  AS SOURCE_FILENAME,"
					+ "ITM.PROFIT_CENTRE  AS PROFIT_CENTRE,ITM.PLANT_CODE  "
					+ "AS PLANT_CODE,ITM.DIVISION  AS DIVISION,ITM.LOCATION  "
					+ "AS LOCATION,ITM.BILL_OF_ENTRY  AS BILL_OF_ENTRY,"
					+ "ITM.ITC_ENTITLEMENT  AS ITC_ENTITLEMENT,"
					+ "ITM.BILL_OF_ENTRY_DATE  AS BILL_OF_ENTRY_DATE,"
					+ "ITM.PURCHASE_ORGANIZATION  AS PURCHASE_ORGANIZATION,"
					+ "ITM.USERACCESS1  AS USERACCESS1,ITM.USERACCESS2  "
					+ "AS USERACCESS2,ITM.USERACCESS3  AS USERACCESS3,"
					+ "ITM.USERACCESS4  AS USERACCESS4,ITM.USERACCESS5 "
					+ "AS USERACCESS5,ITM.USERACCESS6  AS USERACCESS6,"
					+ "HDR.ITC_REVERSAL_IDENTIFER  AS ITC_REVERSAL_IDENTIFER,"
					+ "ITM.RETURN_PERIOD  AS RETURN_PERIOD,"
					+ "HDR.SUPPLIER_GSTIN  AS SUPPLIER_GSTIN,"
					+ "ITM.POSTING_DATE  AS POSTING_DATE,"
					+ "HDR.DOC_TYPE  AS DOC_TYPE,HDR.SUPPLY_TYPE  AS SUPPLY_TYPE,"
					+ "HDR.DOC_NUM  AS DOC_NUM, ITM.DOC_DATE  AS DOC_DATE,"
					+ "HDR.ORIGINAL_DOC_DATE  AS ORIGINAL_DOC_DATE,"
					+ "HDR.ORIGINAL_DOC_NUM  AS ORIGINAL_DOC_NUM,"
					+ "ITM.CRDR_PRE_GST  AS CRDR_PRE_GST,"
					+ "HDR.CUST_GSTIN  AS CUST_GSTIN,ITM.CUST_SUPP_TYPE "
					+ "AS CUST_SUPP_TYPE,ITM.DIFF_PERCENT  AS DIFF_PERCENT,"
					+ "ITM.ORIG_SUPPLIER_GSTIN  AS ORIG_SUPPLIER_GSTIN,"
					+ "ITM.CUST_SUPP_NAME  AS CUST_SUPP_NAME,"
					+ "ITM.CUST_SUPP_CODE  AS CUST_SUPP_CODE,"
					+ "ITM.CUST_SUPP_ADDRESS1  AS CUST_SUPP_ADDRESS1,"
					+ "ITM.CUST_SUPP_ADDRESS2  AS CUST_SUPP_ADDRESS2,"
					+ "ITM.CUST_SUPP_ADDRESS3  AS CUST_SUPP_ADDRESS3,"
					+ "ITM.CUST_SUPP_ADDRESS4  AS CUST_SUPP_ADDRESS4,"
					+ "ITM.POS  AS POS,ITM.STATE_APPLYING_CESS  AS "
					+ "STATE_APPLYING_CESS,ITM.SHIP_PORT_CODE  AS SHIP_PORT_CODE,"
					+ "ITM.SECTION7_OF_IGST_FLAG  AS SECTION7_OF_IGST_FLAG,"
					+ "HDR.DOC_AMT  AS INV_VALUE,ITM.REVERSE_CHARGE "
					+ "AS REVERSE_CHARGE,ITM.CLAIM_REFUND_FLAG  AS CLAIM_REFUND_FLAG,"
					+ "ITM.AUTOPOPULATE_TO_REFUND  AS AUTOPOPULATE_TO_REFUND,"
					+ "ITM.EWAY_BILL_NUM  AS EWAY_BILL_NUM,ITM.EWAY_BILL_DATE "
					+ "AS EWAY_BILL_DATE, HDR.FILE_ID,FIL.ID, ITM.DOC_HEADER_ID,"
					+ "ITM.GLCODE_TAXABLEVALUE AS GLCODE_TAXABLEVALUE, "
					+ "ITM.GLCODE_IGST  AS GLCODE_IGST,ITM.GLCODE_CGST AS "
					+ "GLCODE_CGST, ITM.GLCODE_SGST AS GLCODE_SGST,"
					+ "ITM.GLCODE_ADV_CESS  AS GLCODE_ADV_CESS,"
					+ "ITM.GLCODE_SP_CESS  AS GLCODE_SP_CESS,"
					+ "ITM.GLCODE_STATE_CESS  AS GLCODE_STATE_CESS,"
					+ "ITM.ITM_NO  AS ITM_NO , ITM.ITM_HSNSAC  AS ITM_HSNSAC,"
					+ "ITM.PRODUCT_CODE  AS PRODUCT_CODE,ITM.ITM_DESCRIPTION "
					+ "AS ITM_DESCRIPTION,ITM.ITM_TYPE  AS ITM_TYPE,"
					+ "ITM.ITM_UQC  AS ITM_UQC," + "ITM.ITM_QTY  AS ITM_QTY,"
					+ "ITM.CRDR_REASON  AS CRDR_REASON,ITM.COMMON_SUP_INDICATOR "
					+ "AS COMMON_SUP_INDICATOR,ITM.PURCHASE_VOUCHER_DATE "
					+ "AS PURCHASE_VOUCHER_DATE,ITM.PURCHASE_VOUCHER_NUM "
					+ "AS PURCHASE_VOUCHER_NUM,ITM.PAYMENT_DATE  AS PAYMENT_DATE,"
					+ "ITM.PAYMENT_VOUCHER_NUM  AS PAYMENT_VOUCHER_NUM,"
					+ "ITM.CONTRACT_DATE  AS CONTRACT_DATE,"
					+ "ITM.CONTRACT_VALUE AS CONTRACT_VALUE,"
					+ "ITM.AVAILABLE_CGST AS AVAILABLE_CGST,"
					+ "ITM.AVAILABLE_SGST AS AVAILABLE_SGST,"
					+ "ITM.CIF_VALUE AS CIF_VALUE,"
					+ "ITM.CUSTOM_DUTY AS CUSTOM_DUTY,"
					+ "ITM.USERDEFINED_FIELD1  AS USERDEFINED_FIELD1,"
					+ "ITM.USERDEFINED_FIELD2  AS USERDEFINED_FIELD2,"
					+ "ITM.USERDEFINED_FIELD3  AS USERDEFINED_FIELD3,"
					+ "ITM.USERDEFINED_FIELD4  AS USERDEFINED_FIELD4,"
					+ "ITM.USERDEFINED_FIELD5  AS USERDEFINED_FIELD5,"
					+ "ITM.USERDEFINED_FIELD6  AS USERDEFINED_FIELD6,"
					+ "ITM.USERDEFINED_FIELD7  AS USERDEFINED_FIELD7,"
					+ "ITM.USERDEFINED_FIELD8  AS USERDEFINED_FIELD8,"
					+ "ITM.USERDEFINED_FIELD9  AS USERDEFINED_FIELD9,"
					+ "ITM.USERDEFINED_FIELD10  AS USERDEFINED_FIELD10,"
					+ "ITM.USERDEFINED_FIELD11  AS USERDEFINED_FIELD11,"
					+ "ITM.USERDEFINED_FIELD12  AS USERDEFINED_FIELD12,"
					+ "ITM.USERDEFINED_FIELD13  AS USERDEFINED_FIELD13,"
					+ "ITM.USERDEFINED_FIELD14  AS USERDEFINED_FIELD14,"
					+ "ITM.USERDEFINED_FIELD15  AS USERDEFINED_FIELD15,"
					+ "ITM.OTHER_VALUES AS OTHER_VALUES," + "ITM.ADJ_REF_NO "
					+ "AS ADJ_REF_NO,ITM.ADJ_REF_DATE  AS ADJ_REF_DATE,"
					+ "ITM.TAXABLE_VALUE  AS TAXABLE_VALUE,ITM.IGST_RATE "
					+ "AS IGST_RATE,ITM.IGST_AMT  AS IGST_AMT,"
					+ "ITM.CGST_RATE AS CGST_RATE,"
					+ "ITM.CGST_AMT AS CGST_AMT,"
					+ "ITM.SGST_RATE AS SGST_RATE,"
					+ "ITM.SGST_AMT AS SGST_AMT,"
					+ "ITM.CESS_RATE_SPECIFIC AS CESS_RATE_SPECIFIC,"
					+ "ITM.CESS_AMT_SPECIFIC AS CESS_AMT_SPECIFIC,"
					+ "ITM.CONTRACT_NUMBER  AS CONTRACT_NUMBER,"
					+ "ITM.CESS_RATE_ADVALOREM AS CESS_RATE_ADVALOREM,"
					+ "ITM.AVAILABLE_IGST AS AVAILABLE_IGST,"
					+ "ITM.CESS_AMT_ADVALOREM AS CESS_AMT_ADVALOREM,"
					+ "ITM.STATECESS_RATE AS STATECESS_RATE,"
					+ "ITM.AVAILABLE_CESS AS AVAILABLE_CESS,"
					+ "ITM.STATECESS_AMT AS STATECESS_AMT,"
					+ "ITM.ADJ_TAXABLE_VALUE AS ADJ_TAXABLE_VALUE,"
					+ "ITM.ADJ_IGST_AMT AS ADJ_IGST_AMT,"
					+ "ITM.ADJ_CGST_AMT AS ADJ_CGST_AMT,"
					+ "ITM.ADJ_SGST_AMT AS ADJ_SGST_AMT,"
					+ "ITM.ADJ_CESS_AMT_ADVALOREM AS ADJ_CESS_AMT_ADVALOREM,"
					+ "ITM.ADJ_CESS_AMT_SPECIFIC AS ADJ_CESS_AMT_SPECIFIC,"
					+ "ITM.ADJ_STATECESS_AMT AS ADJ_STATECESS_AMT,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP," + "ITM.SUPPLY_TYPE "
					+ "AS ITM_SUPPLY_TYPE,ITM.ORIGINAL_DOC_NUM "
					+ "AS ITM_ORIGINAL_DOC_NUM,ITM.ORIGINAL_DOC_DATE "
					+ "AS ITM_ORIGINAL_DOC_DATE," + "ITM.LINE_ITEM_AMT "
					+ "AS ITM_INV_VALUE," + "ITM.ELIGIBILITY_INDICATOR AS "
					+ "ITM_ELIGIBILITY_INDICATOR "
					+ "FROM ANX_INWARD_DOC_HEADER "
					+ "HDR LEFT OUTER JOIN TF_INWARD_HEADER_ERROR_INFO () "
					+ "ERRH ON HDR.ID=ERRH.DOC_HEADER_ID INNER JOIN "
					+ "ANX_INWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID AND "
					+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "LEFT OUTER JOIN TF_INWARD_ITEM_ERROR_INFO () ERRI ON "
					+ "ITM.DOC_HEADER_ID=ERRI.DOC_HEADER_ID AND "
					+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) AND "
					+ "IFNULL(ITM.ITM_NO,'0') = IFNULL(ERRI.ITM_NO,'0') "
					+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
					+ "WHERE IS_ERROR=TRUE AND ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID "
					+ "FROM ANX_INWARD_DOC_ERROR WHERE VAL_TYPE = 'BV' ) "
					+ buildQuery1 + " AND " + buildQuery 
				    + " ORDER BY HDR.ID  ";

		} else if ((DownloadReportsConstant.ERRORSV)
				.equalsIgnoreCase(errorType)) {
			queryStr = "SELECT HDR.ID AS HID,ITM.USER_ID,ITM.SOURCE_FILENAME,"
					+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,ITM.DIVISION,ITM.LOCATION,"
					+ "ITM.BILL_OF_ENTRY,ITM.ITC_ENTITLEMENT,"
					+ "ITM.BILL_OF_ENTRY_DATE,ITM.PURCHASE_ORGANIZATION,"
					+ "ITM.USERACCESS1,ITM.USERACCESS2,ITM.USERACCESS3,"
					+ "ITM.USERACCESS4,ITM.USERACCESS5,ITM.USERACCESS6,"
					+ "HDR.ITC_REVERSAL_IDENTIFER,ITM.RETURN_PERIOD,"
					+ "HDR.SUPPLIER_GSTIN,ITM.POSTING_DATE,HDR.DOC_TYPE,"
					+ "HDR.SUPPLY_TYPE,HDR.DOC_NUM,ITM.DOC_DATE,"
					+ "HDR.ORIGINAL_DOC_DATE,HDR.ORIGINAL_DOC_NUM,ITM.CRDR_PRE_GST,"
					+ "HDR.CUST_GSTIN,ITM.CUST_SUPP_TYPE,ITM.DIFF_PERCENT,"
					+ "ITM.ORIG_SUPPLIER_GSTIN,ITM.CUST_SUPP_NAME,ITM.CUST_SUPP_CODE,"
					+ "ITM.CUST_SUPP_ADDRESS1,ITM.CUST_SUPP_ADDRESS2,"
					+ "ITM.CUST_SUPP_ADDRESS3,ITM.CUST_SUPP_ADDRESS4,ITM.POS, "
					+ "ITM.STATE_APPLYING_CESS,ITM.SHIP_PORT_CODE,"
					+ "ITM.SECTION7_OF_IGST_FLAG,HDR.DOC_AMT,ITM.REVERSE_CHARGE,"
					+ "ITM.CLAIM_REFUND_FLAG,ITM.AUTOPOPULATE_TO_REFUND,"
					+ "ITM.EWAY_BILL_NUM,ITM.EWAY_BILL_DATE, HDR.FILE_ID,FIL.ID,"
					+ "ITM.DOC_HEADER_ID,ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,"
					+ "ITM.GLCODE_CGST, ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,"
					+ "ITM.GLCODE_SP_CESS,ITM.GLCODE_STATE_CESS,ITM.ITM_NO, "
					+ "ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,ITM.ITM_DESCRIPTION,"
					+ "ITM.ITM_TYPE,ITM.ITM_UQC,"
					+ "ITM.ITM_QTY,ITM.CRDR_REASON,"
					+ "ITM.COMMON_SUP_INDICATOR,ITM.PURCHASE_VOUCHER_DATE,"
					+ "ITM.PURCHASE_VOUCHER_NUM,ITM.PAYMENT_DATE,"
					+ "ITM.PAYMENT_VOUCHER_NUM,ITM.CONTRACT_DATE,"
					+ "ITM.CONTRACT_VALUE," + "ITM.AVAILABLE_CGST,"
					+ "ITM.AVAILABLE_SGST," + "ITM.CIF_VALUE,"
					+ "ITM.CUSTOM_DUTY,"
					+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
					+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
					+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
					+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
					+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
					+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,"
					+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
					+ "ITM.USERDEFINED_FIELD15," + "ITM.OTHER_VALUES,"
					+ "ITM.ADJ_REF_NO,"
					+ "ITM.ADJ_REF_DATE,ITM.TAXABLE_VALUE,ITM.IGST_RATE,"
					+ "ITM.IGST_AMT," + "ITM.CGST_RATE," + "ITM.CGST_AMT,"
					+ "ITM.SGST_RATE," + "ITM.SGST_AMT,"
					+ "ITM.CESS_RATE_SPECIFIC," + "ITM.CESS_AMT_SPECIFIC,"
					+ "ITM.CONTRACT_NUMBER," + "ITM.CESS_RATE_ADVALOREM,"
					+ "ITM.AVAILABLE_IGST," + "ITM.CESS_AMT_ADVALOREM,"
					+ "ITM.STATECESS_RATE," + "ITM.AVAILABLE_CESS,"
					+ "ITM.STATECESS_AMT," + "ITM.ADJ_TAXABLE_VALUE,"
					+ "ITM.ADJ_IGST_AMT," + "ITM.ADJ_CGST_AMT,"
					+ "ITM.ADJ_SGST_AMT," + "ITM.ADJ_CESS_AMT_ADVALOREM,"
					+ "ITM.ADJ_CESS_AMT_SPECIFIC," + "ITM.ADJ_STATECESS_AMT,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP,"
					+ "ITM.SUPPLY_TYPE AS ITM_SUPPLY_TYPE,"
					+ "ITM.ORIGINAL_DOC_NUM AS ITM_ORIGINAL_DOC_NUM,"
					+ "ITM.ORIGINAL_DOC_DATE AS ITM_ORIGINAL_DOC_DATE,"
					+ "ITM.LINE_ITEM_AMT AS ITM_INV_VALUE,"
					+ "ITM.ELIGIBILITY_INDICATOR FROM "
					+ "ANX_INWARD_ERROR_HEADER HDR LEFT OUTER JOIN "
					+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON HDR.ID="
					+ "ERRH.DOC_HEADER_ID INNER JOIN ANX_INWARD_ERROR_ITEM ITM "
					+ "ON HDR.ID=ITM.DOC_HEADER_ID LEFT OUTER JOIN "
					+ "TF_INWARD_ITEM_ERROR_INFO () ERRI ON ITM.DOC_HEADER_ID="
					+ "ERRI.DOC_HEADER_ID AND "
					+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) AND "
					+ "IFNULL(ITM.ITM_NO,'0') = IFNULL(ERRI.ITM_NO,'0')  "
					+ "INNER JOIN FILE_STATUS FIL ON "
					+ "HDR.FILE_ID=FIL.ID WHERE IS_ERROR='true'  "
					+ "AND ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID FROM ANX_INWARD_DOC_ERROR "
					+ "WHERE VAL_TYPE = 'SV' ) " + " AND " + buildQuery 
					+ " ORDER BY HDR.ID ";

		}
		return queryStr;
	}
}

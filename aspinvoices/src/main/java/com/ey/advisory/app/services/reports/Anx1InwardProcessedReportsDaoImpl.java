
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

import com.ey.advisory.app.data.views.client.Anx1InwardProcessedRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

@Component("Anx1InwardProcessedReportsDaoImpl")
public class Anx1InwardProcessedReportsDaoImpl
		implements Anx1InwardProcessedReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Anx1InwardProcessedRecordsDto> getInwardProcessedReports(
			Anx1FileStatusReportsReqDto request) {

		Long fileId = request.getFileId();
		String status = request.getStatus();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" AND HDR.FILE_ID= :fileId");
		}
		if (status.equalsIgnoreCase(DownloadReportsConstant.PROCESSEDACTIVE)) {
			buildQuery.append(" AND IS_DELETE = FALSE");
		}
		if (status
				.equalsIgnoreCase(DownloadReportsConstant.PROCESSEDINACTIVE)) {
			buildQuery.append(" AND IS_DELETE = TRUE");
		}
		String queryStr = createInwardProcessedQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();
		return list.stream().map(o -> convertInwardProcessedReports(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1InwardProcessedRecordsDto convertInwardProcessedReports(
			Object[] arr) {
		Anx1InwardProcessedRecordsDto obj = new Anx1InwardProcessedRecordsDto();

		obj.setUserID(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnType(arr[2] != null ? arr[2].toString() : null);
		obj.setDataCategory(arr[3] != null ? arr[3].toString() : null);
		obj.setTableNumber(arr[4] != null ? arr[4].toString() : null);
		obj.setSourceFileName(arr[5] != null ? arr[5].toString() : null);
		obj.setProfitCentre(arr[6] != null ? arr[6].toString() : null);
		obj.setPlant(arr[7] != null ? arr[7].toString() : null);
		obj.setDivision(arr[8] != null ? arr[8].toString() : null);
		obj.setLocation(arr[9] != null ? arr[9].toString() : null);
		obj.setPurchaseOrganisation(
				arr[10] != null ? arr[10].toString() : null);
		obj.setBillOfEntry(arr[11] != null ? arr[11].toString() : null);
		if (arr[12] != null) {
			String strdate = arr[12].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBillOfEntryDate(newDate);
		} else {
			obj.setBillOfEntryDate(null);
		}
		obj.setITCEntitlement(arr[13] != null ? arr[13].toString() : null);
		obj.setUserAccess1(arr[14] != null ? arr[14].toString() : null);
		obj.setUserAccess2(arr[15] != null ? arr[15].toString() : null);
		obj.setUserAccess3(arr[16] != null ? arr[16].toString() : null);
		obj.setUserAccess4(arr[17] != null ? arr[17].toString() : null);
		obj.setUserAccess5(arr[18] != null ? arr[18].toString() : null);
		obj.setUserAccess6(arr[19] != null ? arr[19].toString() : null);
		obj.setReturnPeriod(arr[20] != null ? arr[20].toString() : null);
		obj.setSupplierGSTIN(arr[21] != null ? arr[21].toString() : null);
		obj.setDocumentType(arr[22] != null ? arr[22].toString() : null);
		obj.setSupplyType(arr[23] != null ? arr[23].toString() : null);
		obj.setDocumentNumber(arr[24] != null ? arr[24].toString() : null);
		if (arr[25] != null) {
			String strdate = arr[25].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {
			obj.setDocumentDate(null);
		}
		if (arr[122] != null) {
			String strdate = arr[122].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setOriginalDocumentDate(newDate);
		} else {
			obj.setOriginalDocumentDate(null);
		}
		obj.setOriginalDocumentNumber(
				arr[121] != null ? arr[121].toString() : null);
		obj.setCRDRPreGST(arr[28] != null ? arr[28].toString() : null);
		obj.setRecipientGSTIN(arr[29] != null ? arr[29].toString() : null);
		obj.setSupplierType(arr[30] != null ? arr[30].toString() : null);
		obj.setDifferentialFlag(arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[32] != null ? arr[32].toString() : null);
		obj.setSupplierName(arr[33] != null ? arr[33].toString() : null);
		obj.setSupplierCode(arr[34] != null ? arr[34].toString() : null);
		obj.setSupplierAddress1(arr[35] != null ? arr[35].toString() : null);
		obj.setSupplierAddress2(arr[36] != null ? arr[36].toString() : null);
		obj.setSupplierAddress3(arr[37] != null ? arr[37].toString() : null);
		obj.setSupplierAddress4(arr[38] != null ? arr[38].toString() : null);
		obj.setPOS(arr[39] != null ? arr[39].toString() : null);
		obj.setStateApplyingCess(arr[40] != null ? arr[40].toString() : null);
		obj.setPortCode(arr[41] != null ? arr[41].toString() : null);
		obj.setSection7ofIGSTFlag(arr[42] != null ? arr[42].toString() : null);
		obj.setInvoicevalueasp(arr[43] != null ? arr[43].toString() : null);
		obj.setInvoiceValue(arr[123] != null ? arr[123].toString() : null);
		obj.setReverseChargeFlag(arr[44] != null ? arr[44].toString() : null);
		if (arr[45] != null) {
			String strdate = arr[45].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPostingDate(newDate);
		} else {
			obj.setPostingDate(null);
		}
		obj.setITCReversalIdentifier(
				arr[46] != null ? arr[46].toString() : null);
		obj.setClaimRefundFlag(arr[47] != null ? arr[47].toString() : null);
		obj.setAutoPopulateToRefund(
				arr[48] != null ? arr[48].toString() : null);
		obj.setEWayBillNumber(arr[49] != null ? arr[49].toString() : null);
		if (arr[50] != null) {
			String strdate = arr[50].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setEWayBillDate(newDate);
		} else {
			obj.setEWayBillDate(null);
		}
		obj.setGLCodeTaxableValue(arr[52] != null ? arr[52].toString() : null);
		obj.setGLCodeIGST(arr[53] != null ? arr[53].toString() : null);
		obj.setGLCodeCGST(arr[54] != null ? arr[54].toString() : null);
		obj.setGLCodeSGST(arr[55] != null ? arr[55].toString() : null);
		obj.setGLCodeAdvaloremCess(arr[56] != null ? arr[56].toString() : null);
		obj.setGLCodeSpecificCess(arr[57] != null ? arr[57].toString() : null);
		obj.setGLCodeStateCess(arr[58] != null ? arr[58].toString() : null);
		obj.setLineNumber(arr[59] != null ? arr[59].toString() : null);
		obj.setHSNorSAC(arr[60] != null ? arr[60].toString() : null);
		obj.setItemCode(arr[61] != null ? arr[61].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[62] != null ? arr[62].toString() : null);
		if (arr[63] != null) {
			String strdate = arr[63].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPurchaseVoucherDate(newDate);
		} else {
			obj.setPurchaseVoucherDate(null);
		}
		obj.setPurchaseVoucherNumber(
				arr[64] != null ? arr[64].toString() : null);
		if (arr[65] != null) {
			String strdate = arr[65].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPaymentDate(newDate);
		} else {
			obj.setPaymentDate(null);
		}
		obj.setItemDescription(arr[66] != null ? arr[66].toString() : null);
		obj.setCategoryOfItem(arr[67] != null ? arr[67].toString() : null);
		obj.setUnitOfMeasurement(arr[68] != null ? arr[68].toString() : null);
		obj.setQuantity(arr[69] != null ? arr[69].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[70] != null ? arr[70].toString() : null);
		obj.setUserDefinedField1(arr[71] != null ? arr[71].toString() : null);
		obj.setUserDefinedField2(arr[72] != null ? arr[72].toString() : null);
		obj.setUserDefinedField3(arr[73] != null ? arr[73].toString() : null);
		obj.setUserDefinedField4(arr[74] != null ? arr[74].toString() : null);
		obj.setUserDefinedField5(arr[75] != null ? arr[75].toString() : null);
		obj.setUserDefinedField6(arr[76] != null ? arr[76].toString() : null);
		obj.setUserDefinedField7(arr[77] != null ? arr[77].toString() : null);
		obj.setUserDefinedField8(arr[78] != null ? arr[78].toString() : null);
		obj.setUserDefinedField9(arr[79] != null ? arr[79].toString() : null);
		obj.setUserDefinedField10(arr[80] != null ? arr[80].toString() : null);
		obj.setUserDefinedField11(arr[81] != null ? arr[81].toString() : null);
		obj.setUserDefinedField12(arr[82] != null ? arr[82].toString() : null);
		obj.setUserDefinedField13(arr[83] != null ? arr[83].toString() : null);
		obj.setUserDefinedField14(arr[84] != null ? arr[84].toString() : null);
		obj.setUserDefinedField15(arr[85] != null ? arr[85].toString() : null);
		obj.setOtherValue(arr[86] != null ? arr[86].toString() : null);
		obj.setAdjustmentReferenceNo(
				arr[87] != null ? arr[87].toString() : null);
		if (arr[88] != null) {
			String strdate = arr[88].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAdjustmentReferenceDate(newDate);
		} else {
			obj.setAdjustmentReferenceDate(null);
		}
		obj.setTaxableValue(arr[89] != null ? arr[89].toString() : null);
		obj.setIntegratedTaxRate(arr[90] != null ? arr[90].toString() : null);
		obj.setIntegratedTaxAmount(arr[91] != null ? arr[91].toString() : null);
		obj.setCentralTaxRate(arr[92] != null ? arr[92].toString() : null);
		obj.setPaymentVoucherNumber(
				arr[93] != null ? arr[93].toString() : null);
		obj.setContractNumber(arr[94] != null ? arr[94].toString() : null);
		if (arr[95] != null) {
			String strdate = arr[95].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setContractDate(newDate);
		} else {
			obj.setContractDate(null);
		}
		obj.setContractValue(arr[96] != null ? arr[96].toString() : null);
		obj.setCentralTaxAmount(arr[97] != null ? arr[97].toString() : null);
		obj.setStateUTTaxRate(arr[98] != null ? arr[98].toString() : null);
		obj.setStateUTTaxAmount(arr[99] != null ? arr[99].toString() : null);
		obj.setSpecificCessRate(arr[100] != null ? arr[100].toString() : null);
		obj.setSpecificCessAmount(
				arr[101] != null ? arr[101].toString() : null);
		obj.setAdvaloremCessRate(arr[102] != null ? arr[102].toString() : null);
		obj.setAdvaloremCessAmount(
				arr[103] != null ? arr[103].toString() : null);
		obj.setStateCessRate(arr[104] != null ? arr[104].toString() : null);
		obj.setStateCessAmount(arr[105] != null ? arr[105].toString() : null);
		obj.setTaxableValueAdjusted(
				arr[106] != null ? arr[106].toString() : null);
		obj.setAvailableIGST(arr[107] != null ? arr[107].toString() : null);
		obj.setAvailableCGST(arr[108] != null ? arr[108].toString() : null);
		obj.setAvailableSGST(arr[109] != null ? arr[109].toString() : null);
		obj.setAvailableCess(arr[110] != null ? arr[110].toString() : null);
		obj.setCIFValue(arr[111] != null ? arr[111].toString() : null);
		obj.setCustomDuty(arr[112] != null ? arr[112].toString() : null);
		obj.setIntegratedTaxAmountAdjusted(
				arr[113] != null ? arr[113].toString() : null);
		obj.setCentralTaxAmountAdjusted(
				arr[114] != null ? arr[114].toString() : null);
		obj.setStateUTTaxAmountAdjusted(
				arr[115] != null ? arr[115].toString() : null);
		obj.setAdvaloremCessAmountAdjusted(
				arr[116] != null ? arr[116].toString() : null);
		obj.setSpecificCessAmountAdjusted(
				arr[117] != null ? arr[117].toString() : null);
		obj.setStateCessAmountAdjusted(
				arr[118] != null ? arr[118].toString() : null);
		obj.setASPInformationID(arr[119] != null ? arr[119].toString() : null);
		obj.setASPInformationDescription(
				arr[120] != null ? arr[120].toString() : null);
		obj.setEligibilityIndicator(
				arr[124] != null ? arr[124].toString() : null);

		return obj;
	}

	private String createInwardProcessedQueryString(String buildQuery) {
		return "SELECT HDR.ID AS HID,HDR.USER_ID,"
				+ "HDR.RETURN_TYPE AS RETURN_TYPE,"
				+ "HDR.TAX_DOC_TYPE AS DATA_CATEGORY,"
				+ "HDR.TABLE_SECTION AS TABLE_NUMBER,"
				+ "HDR.SOURCE_FILENAME,"
				+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,HDR.DIVISION,"
				+ "ITM.LOCATION,HDR.PURCHASE_ORGANIZATION,"
				+ "HDR.BILL_OF_ENTRY,HDR.BILL_OF_ENTRY_DATE,"
				+ "HDR.ITC_ENTITLEMENT,HDR.USERACCESS1,HDR.USERACCESS2,"
				+ "HDR.USERACCESS3,HDR.USERACCESS4,HDR.USERACCESS5,"
				+ "HDR.USERACCESS6,HDR.RETURN_PERIOD,"
				+ "HDR.SUPPLIER_GSTIN,HDR.DOC_TYPE,ITM.SUPPLY_TYPE,"
				+ "HDR.DOC_NUM,HDR.DOC_DATE,HDR.ORIGINAL_DOC_DATE,"
				+ "HDR.ORIGINAL_DOC_NUM,HDR.CRDR_PRE_GST,HDR.CUST_GSTIN,"
				+ "HDR.CUST_SUPP_TYPE,HDR.DIFF_PERCENT,"
				+ "HDR.ORIG_SUPPLIER_GSTIN,HDR.CUST_SUPP_NAME,"
				+ "HDR.CUST_SUPP_CODE,HDR.CUST_SUPP_ADDRESS1,"
				+ "HDR.CUST_SUPP_ADDRESS2,HDR.CUST_SUPP_ADDRESS3,"
				+ "HDR.CUST_SUPP_ADDRESS4,HDR.POS,HDR.STATE_APPLYING_CESS,"
				+ "HDR.SHIP_PORT_CODE,HDR.SECTION7_OF_IGST_FLAG,"
				+ "HDR.DOC_AMT AS INV_VALUE,HDR.REVERSE_CHARGE,"
				+ "HDR.POSTING_DATE,HDR.ITC_REVERSAL_IDENTIFER,"
				+ "HDR.CLAIM_REFUND_FLAG,HDR.AUTOPOPULATE_TO_REFUND,"
				+ "HDR.EWAY_BILL_NUM,HDR.EWAY_BILL_DATE,ITM.DOC_HEADER_ID,"
				+ "ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,ITM.GLCODE_CGST,"
				+ "ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS,"
				+ "ITM.GLCODE_STATE_CESS,ITM.ITM_NO,ITM.ITM_HSNSAC,"
				+ "ITM.PRODUCT_CODE,ITM.COMMON_SUP_INDICATOR,"
				+ "ITM.PURCHASE_VOUCHER_DATE,ITM.PURCHASE_VOUCHER_NUM,"
				+ "ITM.PAYMENT_DATE,ITM.ITM_DESCRIPTION,ITM.ITM_TYPE,"
				+ "ITM.ITM_UQC," + "ITM.ITM_QTY," + "ITM.CRDR_REASON,"
				+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
				+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
				+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
				+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
				+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
				+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,"
				+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
				+ "ITM.USERDEFINED_FIELD15," + "ITM.OTHER_VALUES,"
				+ "ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE,ITM.TAXABLE_VALUE,"
				+ "ITM.IGST_RATE,ITM.IGST_AMT," + "ITM.CGST_RATE,"
				+ "ITM.PAYMENT_VOUCHER_NUM,ITM.CONTRACT_NUMBER,"
				+ "ITM.CONTRACT_DATE," + "ITM.CONTRACT_VALUE," + "ITM.CGST_AMT,"
				+ "ITM.SGST_RATE," + "ITM.SGST_AMT," + "ITM.CESS_RATE_SPECIFIC,"
				+ "ITM.CESS_AMT_SPECIFIC," + "ITM.CESS_RATE_ADVALOREM,"
				+ "ITM.CESS_AMT_ADVALOREM," + "ITM.STATECESS_RATE,"
				+ "ITM.STATECESS_AMT," + "ITM.ADJ_TAXABLE_VALUE,"
				+ "ITM.AVAILABLE_IGST," + "ITM.AVAILABLE_CGST,"
				+ "ITM.AVAILABLE_SGST," + "ITM.AVAILABLE_CESS,"
				+ "ITM.CIF_VALUE," + "ITM.CUSTOM_DUTY," + "ITM.ADJ_IGST_AMT,"
				+ "ITM.ADJ_CGST_AMT," + "ITM.ADJ_SGST_AMT,"
				+ "ITM.ADJ_CESS_AMT_ADVALOREM," + "ITM.ADJ_CESS_AMT_SPECIFIC,"
				+ "ITM.ADJ_STATECESS_AMT,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_NUM) AS ITM_ORIGINAL_DOC_NUM,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_DATE) AS ITM_ORIGINAL_DOC_DATE,"
				+ "TO_CHAR(ITM.LINE_ITEM_AMT) AS ITM_INV_VALUE,"
				+ "TO_CHAR(ITM.ELIGIBILITY_INDICATOR) "
				+ "AS ITM_ELIGIBILITY_INDICATOR, " + "HDR.FILE_ID,FIL.ID FROM "
				+ "ANX_INWARD_DOC_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON HDR.ID= "
				+ "ERRH.DOC_HEADER_ID INNER JOIN ANX_INWARD_DOC_ITEM ITM ON "
				+ "HDR.ID=ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = "
				+ "ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN "
				+ "TF_INWARD_ITEM_ERROR_INFO () ERRI ON ITM.DOC_HEADER_ID= "
				+ "ERRI.DOC_HEADER_ID AND "
				+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) "
				+ "AND " + "IFNULL(ITM.ITM_NO,'0' ) = IFNULL(ERRI.ITM_NO,'0' ) "
				+ "INNER JOIN " + "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
				+ "WHERE IS_ERROR=FALSE " + buildQuery
				+ " ORDER BY HDR.ID ";

	}
}

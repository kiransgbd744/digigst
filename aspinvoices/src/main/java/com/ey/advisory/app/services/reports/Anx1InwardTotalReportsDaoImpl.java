package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1InwardTotalRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

@Component("Anx1InwardTotalReportsDaoImpl")
public class Anx1InwardTotalReportsDaoImpl
		implements Anx1InwardTotalReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx1InwardTotalRecordsDto> getInwardTotalReports(
			Anx1FileStatusReportsReqDto request) {

		Long fileId = request.getFileId();
		String status = request.getStatus();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" HDR.FILE_ID= :fileId");
		}
		String queryStr = createInwardTotalQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();
		return list.stream().map(o -> convertInwardTotalReports(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1InwardTotalRecordsDto convertInwardTotalReports(Object[] arr) {
		Anx1InwardTotalRecordsDto obj = new Anx1InwardTotalRecordsDto();
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
		obj.setBillOfEntryDate(arr[12] != null ? arr[12].toString() : null);
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
		obj.setSupplyType(arr[125] != null ? arr[125].toString() : null);
		obj.setDocumentNumber(arr[24] != null ? arr[24].toString() : null);
		obj.setDocumentDate(arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalDocumentDate(
				arr[127] != null ? arr[127].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[126] != null ? arr[126].toString() : null);
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
		obj.setInvoiceValue(arr[128] != null ? arr[128].toString() : null);
		obj.setReverseChargeFlag(arr[44] != null ? arr[44].toString() : null);
		obj.setPostingDate(arr[45] != null ? arr[45].toString() : null);
		obj.setITCReversalIdentifier(
				arr[46] != null ? arr[46].toString() : null);
		obj.setClaimRefundFlag(arr[47] != null ? arr[47].toString() : null);
		obj.setAutoPopulateToRefund(
				arr[48] != null ? arr[48].toString() : null);
		obj.setEWayBillNumber(arr[49] != null ? arr[49].toString() : null);
		obj.setEWayBillDate(arr[50] != null ? arr[50].toString() : null);
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
		obj.setPurchaseVoucherDate(arr[63] != null ? arr[63].toString() : null);
		obj.setPurchaseVoucherNumber(
				arr[64] != null ? arr[64].toString() : null);
		obj.setPaymentDate(arr[65] != null ? arr[65].toString() : null);
		obj.setItemDescription(arr[66] != null ? arr[66].toString() : null);
		obj.setCategoryOfItem(arr[67] != null ? arr[67].toString() : null);
		obj.setUnitOfMeasurement(arr[68] != null ? arr[68].toString() : null);
		obj.setQuantity(arr[69] != null ? arr[69].toString() : null);
		obj.setPaymentVoucherNumber(
				arr[70] != null ? arr[70].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[71] != null ? arr[71].toString() : null);
		obj.setUserDefinedField1(arr[72] != null ? arr[72].toString() : null);
		obj.setUserDefinedField2(arr[73] != null ? arr[73].toString() : null);
		obj.setUserDefinedField3(arr[74] != null ? arr[74].toString() : null);
		obj.setUserDefinedField4(arr[75] != null ? arr[75].toString() : null);
		obj.setUserDefinedField5(arr[76] != null ? arr[76].toString() : null);
		obj.setUserDefinedField6(arr[77] != null ? arr[77].toString() : null);
		obj.setUserDefinedField7(arr[78] != null ? arr[78].toString() : null);
		obj.setUserDefinedField8(arr[79] != null ? arr[79].toString() : null);
		obj.setUserDefinedField9(arr[80] != null ? arr[80].toString() : null);
		obj.setUserDefinedField10(arr[81] != null ? arr[81].toString() : null);
		obj.setUserDefinedField11(arr[82] != null ? arr[82].toString() : null);
		obj.setUserDefinedField12(arr[83] != null ? arr[83].toString() : null);
		obj.setUserDefinedField13(arr[84] != null ? arr[84].toString() : null);
		obj.setUserDefinedField14(arr[85] != null ? arr[85].toString() : null);
		obj.setUserDefinedField15(arr[86] != null ? arr[86].toString() : null);
		obj.setOtherValue(arr[87] != null ? arr[87].toString() : null);
		obj.setAdjustmentReferenceNo(
				arr[88] != null ? arr[88].toString() : null);
		obj.setAdjustmentReferenceDate(
				arr[89] != null ? arr[89].toString() : null);
		obj.setTaxableValue(arr[90] != null ? arr[90].toString() : null);
		obj.setIntegratedTaxRate(arr[91] != null ? arr[91].toString() : null);
		obj.setIntegratedTaxAmount(arr[92] != null ? arr[92].toString() : null);
		obj.setCentralTaxRate(arr[93] != null ? arr[93].toString() : null);
		obj.setContractNumber(arr[94] != null ? arr[94].toString() : null);
		obj.setContractDate(arr[95] != null ? arr[95].toString() : null);
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
		obj.setASPErrorCode(arr[120] != null ? arr[120].toString() : null);
		obj.setASPErrorDescription(
				arr[121] != null ? arr[121].toString() : null);
		obj.setASPInformationID(arr[122] != null ? arr[122].toString() : null);
		obj.setASPInformationDescription(
				arr[123] != null ? arr[123].toString() : null);
		obj.setRecordStatus(arr[124] != null ? arr[124].toString() : null);
		obj.setEligibilityIndicator(
				arr[129] != null ? arr[129].toString() : null);
		return obj;
	}

	private String createInwardTotalQueryString(String buildQuery) {
		return "(SELECT HDR.ID AS HID,"
				+ "TO_CHAR(ITM.USER_ID) AS USER_ID,TO_CHAR(HDR.RETURN_TYPE) "
				+ "AS RETURN_TYPE,TO_CHAR(HDR.TAX_DOC_TYPE) AS DATA_CATEGORY,"
				+ "TO_CHAR(HDR.TABLE_SECTION) AS TABLE_NUMBER,"
				+ "TO_CHAR(ITM.SOURCE_FILENAME) AS SOURCE_FILENAME,"
				+ "TO_CHAR(ITM.PROFIT_CENTRE) AS PROFIT_CENTRE,"
				+ "TO_CHAR(ITM.PLANT_CODE) AS PLANT_CODE,"
				+ "TO_CHAR(ITM.DIVISION) AS DIVISION,TO_CHAR(ITM.LOCATION) AS LOCATION,"
				+ "TO_CHAR(ITM.PURCHASE_ORGANIZATION) AS PURCHASE_ORGANIZATION,"
				+ "TO_CHAR(ITM.BILL_OF_ENTRY) AS BILL_OF_ENTRY,"
				+ "TO_CHAR(ITM.BILL_OF_ENTRY_DATE) AS BILL_OF_ENTRY_DATE,"
				+ "TO_CHAR(ITM.ITC_ENTITLEMENT) AS ITC_ENTITLEMENT,"
				+ "TO_CHAR(ITM.USERACCESS1) AS USERACCESS1,"
				+ "TO_CHAR(ITM.USERACCESS2) AS USERACCESS2,"
				+ "TO_CHAR(ITM.USERACCESS3) AS USERACCESS3,"
				+ "TO_CHAR(ITM.USERACCESS4) AS USERACCESS4,"
				+ "TO_CHAR(ITM.USERACCESS5) AS USERACCESS5,"
				+ "TO_CHAR(ITM.USERACCESS6) AS USERACCESS6,"
				+ "TO_CHAR(ITM.RETURN_PERIOD) AS RETURN_PERIOD,"
				+ "TO_CHAR(HDR.SUPPLIER_GSTIN) AS SUPPLIER_GSTIN,"
				+ "TO_CHAR(HDR.DOC_TYPE) AS DOC_TYPE,"
				+ "TO_CHAR(HDR.SUPPLY_TYPE) AS SUPPLY_TYPE,"
				+ "TO_CHAR(HDR.DOC_NUM) AS DOC_NUM,TO_CHAR(ITM.DOC_DATE) AS DOC_DATE,"
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
				+ "TO_CHAR(ITM.POS) AS POS,TO_CHAR(ITM.STATE_APPLYING_CESS) "
				+ "AS STATE_APPLYING_CESS,TO_CHAR(ITM.SHIP_PORT_CODE) AS SHIP_PORT_CODE,"
				+ "TO_CHAR(ITM.SECTION7_OF_IGST_FLAG) AS SECTION7_OF_IGST_FLAG,"
				+ "TO_CHAR(HDR.DOC_AMT) AS INV_VALUE,"
				+ "TO_CHAR(ITM.REVERSE_CHARGE) AS REVERSE_CHARGE,"
				+ "TO_CHAR(ITM.POSTING_DATE) AS POSTING_DATE,"
				+ "TO_CHAR(HDR.ITC_REVERSAL_IDENTIFER) AS ITC_REVERSAL_IDENTIFER,"
				+ "TO_CHAR(ITM.CLAIM_REFUND_FLAG) AS CLAIM_REFUND_FLAG,"
				+ "TO_CHAR(ITM.AUTOPOPULATE_TO_REFUND) AS AUTOPOPULATE_TO_REFUND,"
				+ "TO_CHAR(ITM.EWAY_BILL_NUM) AS EWAY_BILL_NUM,"
				+ "TO_CHAR(ITM.EWAY_BILL_DATE) AS EWAY_BILL_DATE,ITM.DOC_HEADER_ID,"
				+ "TO_CHAR(ITM.GLCODE_TAXABLEVALUE) AS GLCODE_TAXABLEVALUE,"
				+ "TO_CHAR(ITM.GLCODE_IGST) AS GLCODE_IGST,"
				+ "TO_CHAR(ITM.GLCODE_CGST) AS GLCODE_CGST,"
				+ "TO_CHAR(ITM.GLCODE_SGST) AS GLCODE_SGST,"
				+ "TO_CHAR(ITM.GLCODE_ADV_CESS) AS GLCODE_ADV_CESS,"
				+ "TO_CHAR(ITM.GLCODE_SP_CESS) AS GLCODE_SP_CESS,"
				+ "TO_CHAR(ITM.GLCODE_STATE_CESS) AS GLCODE_STATE_CESS,"
				+ "TO_CHAR(ITM.ITM_NO) AS ITM_NO,TO_CHAR(ITM.ITM_HSNSAC) AS "
				+ "ITM_HSNSAC,TO_CHAR(ITM.PRODUCT_CODE) AS PRODUCT_CODE,"
				+ "TO_CHAR(ITM.COMMON_SUP_INDICATOR) AS COMMON_SUP_INDICATOR,"
				+ "TO_CHAR(ITM.PURCHASE_VOUCHER_DATE) AS PURCHASE_VOUCHER_DATE,"
				+ "TO_CHAR(ITM.PURCHASE_VOUCHER_NUM) AS PURCHASE_VOUCHER_NUM,"
				+ "TO_CHAR(ITM.PAYMENT_DATE) AS PAYMENT_DATE,"
				+ "TO_CHAR(ITM.ITM_DESCRIPTION) AS ITM_DESCRIPTION,"
				+ "TO_CHAR(ITM.ITM_TYPE) AS ITM_TYPE,TO_CHAR(ITM.ITM_UQC) AS ITM_UQC,"
				+ "TO_CHAR(ITM.ITM_QTY) AS ITM_QTY,"
				+ "TO_CHAR(ITM.PAYMENT_VOUCHER_NUM) "
				+ "AS PAYMENT_VOUCHER_NUM,"
				+ "TO_CHAR(ITM.CRDR_REASON) AS CRDR_REASON,"
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
				+ "TO_CHAR(ITM.IGST_RATE) AS IGST_RATE,TO_CHAR(ITM.IGST_AMT) "
				+ "AS IGST_AMT," + "TO_CHAR(ITM.CGST_RATE) AS CGST_RATE,"
				+ "TO_CHAR(ITM.CONTRACT_NUMBER) "
				+ "AS CONTRACT_NUMBER,TO_CHAR(ITM.CONTRACT_DATE) AS CONTRACT_DATE,"
				+ "TO_CHAR(ITM.CONTRACT_VALUE) AS CONTRACT_VALUE,"
				+ "TO_CHAR(ITM.CGST_AMT) AS CGST_AMT,"
				+ "TO_CHAR(ITM.SGST_RATE) AS SGST_RATE,"
				+ "TO_CHAR(ITM.SGST_AMT) AS SGST_AMT,"
				+ "TO_CHAR(ITM.CESS_RATE_SPECIFIC) " + "AS CESS_RATE_SPECIFIC,"
				+ "TO_CHAR(ITM.CESS_AMT_SPECIFIC) " + "AS CESS_AMT_SPECIFIC,"
				+ "TO_CHAR(ITM.CESS_RATE_ADVALOREM) "
				+ "AS CESS_RATE_ADVALOREM," + "TO_CHAR(ITM.CESS_AMT_ADVALOREM) "
				+ "AS CESS_AMT_ADVALOREM," + "TO_CHAR(ITM.STATECESS_RATE) "
				+ "AS STATECESS_RATE," + "TO_CHAR(ITM.STATECESS_AMT) "
				+ "AS STATECESS_AMT," + "TO_CHAR(ITM.ADJ_TAXABLE_VALUE) "
				+ "AS ADJ_TAXABLE_VALUE," + "TO_CHAR(ITM.AVAILABLE_IGST) "
				+ "AS AVAILABLE_IGST," + "TO_CHAR(ITM.AVAILABLE_CGST) "
				+ "AS AVAILABLE_CGST," + "TO_CHAR(ITM.AVAILABLE_SGST) "
				+ "AS AVAILABLE_SGST," + "TO_CHAR(ITM.AVAILABLE_CESS) "
				+ "AS AVAILABLE_CESS," + "TO_CHAR(ITM.CIF_VALUE) AS CIF_VALUE,"
				+ "TO_CHAR(ITM.CUSTOM_DUTY) AS CUSTOM_DUTY,"
				+ "TO_CHAR(ITM.ADJ_IGST_AMT) AS ADJ_IGST_AMT,"
				+ "TO_CHAR(ITM.ADJ_CGST_AMT) AS ADJ_CGST_AMT,"
				+ "TO_CHAR(ITM.ADJ_SGST_AMT) AS ADJ_SGST_AMT,"
				+ "TO_CHAR(ITM.ADJ_CESS_AMT_ADVALOREM) "
				+ "AS ADJ_CESS_AMT_ADVALOREM,"
				+ "TO_CHAR(ITM.ADJ_CESS_AMT_SPECIFIC) "
				+ "AS ADJ_CESS_AMT_SPECIFIC,"
				+ "TO_CHAR(ITM.ADJ_STATECESS_AMT) " + "AS ADJ_STATECESS_AMT,"
				+ "FIL.ID,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "(CASE WHEN IS_DELETE=FALSE THEN 'ACTIVE' "
				+ "WHEN IS_DELETE=TRUE THEN 'INACTIVE' END) AS RECORDSTATUS,"
				+ "TO_CHAR(ITM.SUPPLY_TYPE) AS ITM_SUPPLY_TYPE,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_NUM) AS ITM_ORIGINAL_DOC_NUM,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_DATE) AS ITM_ORIGINAL_DOC_DATE,"
				+ "TO_CHAR(ITM.LINE_ITEM_AMT) AS ITM_INV_VALUE,"
				+ "TO_CHAR(ITM.ELIGIBILITY_INDICATOR) "
				+ "AS ITM_ELIGIBILITY_INDICATOR," + "HDR.FILE_ID FROM "
				+ "ANX_INWARD_DOC_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON "
				+ "HDR.ID=ERRH.DOC_HEADER_ID INNER JOIN ANX_INWARD_DOC_ITEM ITM "
				+ "ON HDR.ID=ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD "
				+ "= ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN "
				+ "TF_INWARD_ITEM_ERROR_INFO () ERRI "
				+ "ON ITM.DOC_HEADER_ID=ERRI.DOC_HEADER_ID AND "
				+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) AND "
				+ "IFNULL(ITM.ITM_NO,'0') = IFNULL(ERRI.ITM_NO,'0') INNER JOIN  "
				+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID " 
				+ " WHERE " + buildQuery
				+ " ORDER BY HDR.ID) "
				+ " UNION ALL "
				+ "(SELECT HDR.ID,ITM.USER_ID,"
				+ "HDR.AN_RETURN_TYPE AS RETURN_TYPE,"
				+ "HDR.AN_TAX_DOC_TYPE AS DATA_CATEGORY,"
				+ "HDR.AN_TABLE_SECTION AS TABLE_NUMBER,"
				+ "ITM.SOURCE_FILENAME,ITM.PROFIT_CENTRE,"
				+ "ITM.PLANT_CODE,ITM.DIVISION,ITM.LOCATION,"
				+ "ITM.PURCHASE_ORGANIZATION,ITM.BILL_OF_ENTRY,"
				+ "ITM.BILL_OF_ENTRY_DATE,ITM.ITC_ENTITLEMENT,"
				+ "ITM.USERACCESS1,ITM.USERACCESS2,ITM.USERACCESS3,"
				+ "ITM.USERACCESS4,ITM.USERACCESS5,ITM.USERACCESS6,"
				+ "ITM.RETURN_PERIOD,HDR.SUPPLIER_GSTIN,HDR.DOC_TYPE,"
				+ "HDR.SUPPLY_TYPE,HDR.DOC_NUM,ITM.DOC_DATE,"
				+ "HDR.ORIGINAL_DOC_DATE,HDR.ORIGINAL_DOC_NUM,"
				+ "ITM.CRDR_PRE_GST,HDR.CUST_GSTIN,ITM.CUST_SUPP_TYPE,"
				+ "ITM.DIFF_PERCENT,ITM.ORIG_SUPPLIER_GSTIN,"
				+ "ITM.CUST_SUPP_NAME,ITM.CUST_SUPP_CODE,"
				+ "ITM.CUST_SUPP_ADDRESS1,ITM.CUST_SUPP_ADDRESS2,"
				+ "ITM.CUST_SUPP_ADDRESS3,ITM.CUST_SUPP_ADDRESS4,"
				+ "ITM.POS,ITM.STATE_APPLYING_CESS,ITM.SHIP_PORT_CODE,"
				+ "ITM.SECTION7_OF_IGST_FLAG,HDR.DOC_AMT AS INV_VALUE,"
				+ "ITM.REVERSE_CHARGE,ITM.POSTING_DATE,"
				+ "HDR.ITC_REVERSAL_IDENTIFER,ITM.CLAIM_REFUND_FLAG,"
				+ "ITM.AUTOPOPULATE_TO_REFUND,ITM.EWAY_BILL_NUM,"
				+ "ITM.EWAY_BILL_DATE,ITM.DOC_HEADER_ID,"
				+ "ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,"
				+ "ITM.GLCODE_CGST,ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,"
				+ "ITM.GLCODE_SP_CESS,ITM.GLCODE_STATE_CESS,"
				+ "ITM.ITM_NO,ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,"
				+ "ITM.COMMON_SUP_INDICATOR,ITM.PURCHASE_VOUCHER_DATE,"
				+ "ITM.PURCHASE_VOUCHER_NUM,ITM.PAYMENT_DATE,"
				+ "ITM.ITM_DESCRIPTION,ITM.ITM_TYPE,ITM.ITM_UQC,"
				+ "ITM.ITM_QTY," + "ITM.PAYMENT_VOUCHER_NUM,"
				+ "ITM.CRDR_REASON,ITM.USERDEFINED_FIELD1,"
				+ "ITM.USERDEFINED_FIELD2,ITM.USERDEFINED_FIELD3,"
				+ "ITM.USERDEFINED_FIELD4,ITM.USERDEFINED_FIELD5,"
				+ "ITM.USERDEFINED_FIELD6,ITM.USERDEFINED_FIELD7,"
				+ "ITM.USERDEFINED_FIELD8,ITM.USERDEFINED_FIELD9,"
				+ "ITM.USERDEFINED_FIELD10,ITM.USERDEFINED_FIELD11,"
				+ "ITM.USERDEFINED_FIELD12,ITM.USERDEFINED_FIELD13,"
				+ "ITM.USERDEFINED_FIELD14,ITM.USERDEFINED_FIELD15,"
				+ "ITM.OTHER_VALUES," + "ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE,"
				+ "ITM.TAXABLE_VALUE,ITM.IGST_RATE,ITM.IGST_AMT,"
				+ "ITM.CGST_RATE," + "ITM.CONTRACT_NUMBER,ITM.CONTRACT_DATE,"
				+ "ITM.CONTRACT_VALUE," + "ITM.CGST_AMT," + "ITM.SGST_RATE,"
				+ "ITM.SGST_AMT," + "ITM.CESS_RATE_SPECIFIC,"
				+ "ITM.CESS_AMT_SPECIFIC," + "ITM.CESS_RATE_ADVALOREM,"
				+ "ITM.CESS_AMT_ADVALOREM," + "ITM.STATECESS_RATE,"
				+ "ITM.STATECESS_AMT," + "ITM.ADJ_TAXABLE_VALUE,"
				+ "ITM.AVAILABLE_IGST," + "ITM.AVAILABLE_CGST,"
				+ "ITM.AVAILABLE_SGST," + "ITM.AVAILABLE_CESS,"
				+ "ITM.CIF_VALUE," + "ITM.CUSTOM_DUTY," + "ITM.ADJ_IGST_AMT,"
				+ "ITM.ADJ_CGST_AMT," + "ITM.ADJ_SGST_AMT,"
				+ "ITM.ADJ_CESS_AMT_ADVALOREM," + "ITM.ADJ_CESS_AMT_SPECIFIC,"
				+ "ITM.ADJ_STATECESS_AMT," + "FIL.ID,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "(CASE WHEN IS_DELETE='false' THEN 'ACTIVE' "
				+ "WHEN IS_DELETE='true' THEN 'INACTIVE' END) AS RECORDSTATUS,"
				+ "ITM.SUPPLY_TYPE, ITM.ORIGINAL_DOC_NUM,ITM.ORIGINAL_DOC_DATE,"
				+ "ITM.LINE_ITEM_AMT," + "ITM.ELIGIBILITY_INDICATOR,"
				+ "HDR.FILE_ID FROM "
				+ "ANX_INWARD_ERROR_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON "
				+ "HDR.ID=ERRH.DOC_HEADER_ID INNER JOIN "
				+ "ANX_INWARD_ERROR_ITEM  ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "LEFT OUTER JOIN TF_INWARD_ITEM_ERROR_INFO () ERRI ON "
				+ "ITM.DOC_HEADER_ID=ERRI.DOC_HEADER_ID AND "
				+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) AND "
				+ "IFNULL(TO_CHAR(ITM.ITM_NO),0) = IFNULL(TO_CHAR(ERRI.ITM_NO),0) "
				+ "INNER JOIN FILE_STATUS FIL ON "
				+ "HDR.FILE_ID=FIL.ID  WHERE " + buildQuery 
				+ " ORDER BY HDR.ID ) ";

	}
}

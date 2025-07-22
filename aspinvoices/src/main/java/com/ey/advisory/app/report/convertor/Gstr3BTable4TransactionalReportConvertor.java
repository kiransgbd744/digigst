package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.gstr3b.Gstr3bTable4TransactionalReportDto;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ReportConvertor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr3BTable4TransactionalReportConvertor")
public class Gstr3BTable4TransactionalReportConvertor
		implements ReportConvertor {

	@Override
	public Object convert(Object[] arr, String reportType) {

		Gstr3bTable4TransactionalReportDto obj = new Gstr3bTable4TransactionalReportDto();

		obj.setSuggestedResponse((arr[0] != null)
				? (NumberFomatUtil.isNumber(arr[0])
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[0].toString())
						: arr[0].toString())
				: null);
		obj.setUserResponse((arr[1] != null)
				? (NumberFomatUtil.isNumber(arr[1])
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[1].toString())
						: arr[1].toString())
				: null);
		obj.setTaxPeriodforGSTR3B((arr[2] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[2].toString())
				: null);
		obj.setResponseRemarks((arr[3] != null) ? arr[3].toString() : null);
		obj.setMatchingScoreOutof12(
				(arr[4] != null) ? arr[4].toString() : null);
		obj.setMatchReason((arr[5] != null) ? arr[5].toString() : null);
		obj.setMismatchReason((arr[6] != null) ? arr[6].toString() : null);
		obj.setReportCategory((arr[7] != null) ? arr[7].toString() : null);
		obj.setReportType((arr[8] != null) ? arr[8].toString() : null);
		obj.setPreviousReportType2B(
				(arr[9] != null) ? arr[9].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[10] != null) ? arr[10].toString() : null);
		obj.setTaxPeriod2B(
				(arr[11] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[11].toString()) : null);
		obj.setTaxPeriodPR(
				(arr[12] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[12].toString()) : null);
		obj.setItcReversedTaxPeriod(
				(arr[13] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[13].toString()) : null);
		obj.setItcReclaimedTaxPeriod(
				(arr[14] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[14].toString()) : null);
		obj.setCalendarMonth(
				(arr[15] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[15].toString()) : null);
		obj.setRecipientGstin2B((arr[16] != null) ? arr[16].toString() : null);
		obj.setRecipientGstinPR((arr[17] != null) ? arr[17].toString() : null);
		obj.setSupplierGstin2B((arr[18] != null) ? arr[18].toString() : null);
		obj.setSupplierGstinPR((arr[19] != null) ? arr[19].toString() : null);
		obj.setSupplierLegalName2B(
				(arr[20] != null) ? arr[20].toString() : null);
		obj.setSupplierTradeName2B(
				(arr[21] != null) ? arr[21].toString() : null);
		obj.setSupplierNamePR((arr[22] != null) ? arr[22].toString() : null);
		String docType2B = (arr[23] != null) ? arr[23].toString() : null;
		String docTypePR = (arr[24] != null) ? arr[24].toString() : null;
		obj.setDocType2B((arr[23] != null) ? arr[23].toString() : null);
		obj.setDocTypePR((arr[24] != null) ? arr[24].toString() : null);
		obj.setDocumentNumber2B(
				(arr[25] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[25].toString()) : null);
		obj.setDocumentNumberPR(
				(arr[26] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[26].toString()) : null);

		obj.setDocumentDate2B((arr[27] != null) ? arr[27].toString() : null);
		obj.setDocumentDatePR((arr[28] != null) ? arr[28].toString() : null);
		obj.setGSTPercent2B((arr[29] != null) ? arr[29].toString() : null);
		obj.setGSTPercentPR((arr[30] != null) ? arr[30].toString() : null);
		obj.setPos2B((arr[31] != null) ? arr[31].toString() : null);
		obj.setPosPR((arr[32] != null) ? arr[32].toString() : null);
		if (docType2B != null && (docType2B.equalsIgnoreCase("CR")
				|| docType2B.equalsIgnoreCase("C")
				|| docType2B.equalsIgnoreCase("RCR"))) {
			obj.setTaxableValue2B(CheckForNegativeValue(arr[33]));
			obj.setIGST2B(CheckForNegativeValue(arr[35]));
			obj.setCGST2B(CheckForNegativeValue(arr[37]));
			obj.setSGST2B(CheckForNegativeValue(arr[39]));
			obj.setCess2B(CheckForNegativeValue(arr[41]));
			obj.setTotalTax2B(CheckForNegativeValue(arr[43]));
			obj.setInvoiceValue2B(CheckForNegativeValue(arr[45]));
		} else {
			obj.setTaxableValue2B(
					(arr[33] != null) ? arr[33].toString() : null);
			obj.setIGST2B(
					(arr[35] != null) ? arr[35].toString() : null);
			obj.setCGST2B(
					(arr[37] != null) ? arr[37].toString() : null);
			obj.setSGST2B(
					(arr[39] != null) ? arr[39].toString() : null);
			obj.setCess2B(
					(arr[41] != null) ? arr[41].toString() : null);
			obj.setTotalTax2B(
					(arr[43] != null) ? arr[43].toString() : null);
			obj.setInvoiceValue2B(
					(arr[45] != null) ? arr[45].toString() : null);
		}
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setTaxableValuePR(CheckForNegativeValue(arr[34]));
			obj.setIGSTPR(CheckForNegativeValue(arr[36]));
			obj.setCGSTPR(CheckForNegativeValue(arr[38]));
			obj.setSGSTPR(CheckForNegativeValue(arr[40]));
			obj.setCessPR(CheckForNegativeValue(arr[42]));
			obj.setTotalTaxPR(CheckForNegativeValue(arr[44]));
			obj.setInvoiceValuePR(CheckForNegativeValue(arr[46]));
		} else {
			obj.setTaxableValuePR(
					(arr[34] != null) ? arr[34].toString() : null);
			obj.setIGSTPR(
					(arr[36] != null) ? arr[36].toString() : null);
			obj.setCGSTPR(
					(arr[38] != null) ? arr[38].toString() : null);
			obj.setSGSTPR(
					(arr[40] != null) ? arr[40].toString() : null);
			obj.setCessPR(
					(arr[42] != null) ? arr[42].toString() : null);
			obj.setTotalTaxPR(
					(arr[44] != null) ? arr[44].toString() : null);
			obj.setInvoiceValuePR(
					(arr[46] != null) ? arr[46].toString() : null);
		}
		obj.setReverseChargeFlag2B(
				(arr[47] != null) ? arr[47].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[48] != null) ? arr[48].toString() : null);
		obj.setEligibilityIndicator(
				(arr[49] != null) ? arr[49].toString() : null);

		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setAvailableIGST(CheckForNegativeValue(arr[50]));
			obj.setAvailableCGST(CheckForNegativeValue(arr[51]));
			obj.setAvailableSGST(CheckForNegativeValue(arr[52]));
			obj.setAvailableCESS(CheckForNegativeValue(arr[53]));
		} else {
			obj.setAvailableIGST(
					(arr[50] != null) ? arr[50].toString() : null);
			obj.setAvailableCGST(
					(arr[51] != null) ? arr[51].toString() : null);
			obj.setAvailableSGST(
					(arr[52] != null) ? arr[52].toString() : null);
			obj.setAvailableCESS(
					(arr[53] != null) ? arr[53].toString() : null);
		}
		obj.setITCReversalIdentifier(
				(arr[54] != null) ? arr[54].toString() : null);
		obj.setITCAvailability((arr[55] != null) ? arr[55].toString() : null);
		obj.setReasonForITCUnAvailability(
				(arr[56] != null) ? arr[56].toString() : null);
		obj.setGSTR1FilingStatus((arr[57] != null) ? arr[57].toString() : null);
		obj.setGSTR1FilingDate(
				(arr[58] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[58].toString()) : null);
		obj.setGSTR1FilingPeriod(
				(arr[59] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[59].toString()) : null);
		obj.setGSTR3BFilingStatus(
				(arr[60] != null) ? arr[60].toString() : null);
		obj.setGSTR3BFilingDate(
				(arr[61] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[61].toString()) : null);
		obj.setSupplierGSTINCancellationDate(
				(arr[62] != null) ? arr[62].toString() : null);
		obj.setGstr2BGenerationDate(
				(arr[63] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[63].toString()) : null);
		obj.setOrgInvAmendmentPeriod(
				(arr[64] != null) ? arr[64].toString() : null);
		obj.setOrgAmendmentType((arr[65] != null) ? arr[65].toString() : null);
		obj.setCDNDelinkingFlag((arr[66] != null) ? arr[66].toString() : null);
		obj.setDifferentialPercentage2B(
				(arr[67] != null) ? arr[67].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[68] != null) ? arr[68].toString() : null);
		obj.setOrgDocNumber2B((arr[69] != null) ? arr[69].toString() : null);
		obj.setOrgDocNumberPR((arr[70] != null) ? arr[70].toString() : null);
		obj.setOrgDocDate2B((arr[71] != null) ? arr[71].toString() : null);
		obj.setOrgDocDatePR((arr[72] != null) ? arr[72].toString() : null);
		obj.setOrgSupplierGstinPR(
				(arr[73] != null) ? arr[73].toString() : null);
		obj.setOrgSupplierNamePR((arr[74] != null) ? arr[74].toString() : null);
		obj.setCRDRPreGST2B((arr[75] != null) ? arr[75].toString() : null);
		obj.setCRDRPreGSTPR((arr[76] != null) ? arr[76].toString() : null);
		obj.setBoeReferenceDate2B(
				(arr[77] != null) ? arr[77].toString() : null);
		obj.setBillOfEntryCreatedDate2B(
				(arr[78] != null) ? arr[78].toString() : null);
		obj.setPortCode2B((arr[79] != null) ? arr[79].toString() : null);
		obj.setPortCodePR((arr[80] != null) ? arr[80].toString() : null);
		obj.setBillOfEntry2B((arr[81] != null) ? arr[81].toString() : null);
		obj.setBillOfEntryPR((arr[82] != null) ? arr[82].toString() : null);
		obj.setBillOfEntryDate2B((arr[83] != null) ? arr[83].toString() : null);
		obj.setBillOfEntryDatePR((arr[84] != null) ? arr[84].toString() : null);
		obj.setBOEAmended((arr[85] != null) ? arr[85].toString() : null);
		obj.setTableType2B((arr[86] != null) ? arr[86].toString() : null);
		obj.setSupplyType2B((arr[87] != null) ? arr[87].toString() : null);
		obj.setSupplyTypePR((arr[88] != null) ? arr[88].toString() : null);
		obj.setUserID((arr[89] != null) ? arr[89].toString() : null);
		obj.setSourceIdentifier((arr[90] != null) ? arr[90].toString() : null);
		obj.setSourceFileName((arr[91] != null) ? arr[91].toString() : null);
		obj.setProfitCentre1((arr[92] != null) ? arr[92].toString() : null);
		obj.setPlantCode((arr[93] != null) ? arr[93].toString() : null);
		obj.setDivision((arr[94] != null) ? arr[94].toString() : null);
		obj.setLocation((arr[95] != null) ? arr[95].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[96] != null) ? arr[96].toString() : null);
		obj.setProfitCentre2((arr[97] != null) ? arr[97].toString() : null);
		obj.setProfitCentre3((arr[98] != null) ? arr[98].toString() : null);
		obj.setProfitCentre4((arr[99] != null) ? arr[99].toString() : null);
		obj.setProfitCentre5((arr[100] != null) ? arr[100].toString() : null);
		obj.setProfitCentre6((arr[101] != null) ? arr[101].toString() : null);
		obj.setProfitCentre7((arr[102] != null) ? arr[102].toString() : null);
		obj.setGLAssessableValue(
				(arr[103] != null) ? arr[103].toString() : null);
		obj.setGLIGST((arr[104] != null) ? arr[104].toString() : null);
		obj.setGLCGST((arr[105] != null) ? arr[105].toString() : null);
		obj.setGLSGST((arr[106] != null) ? arr[106].toString() : null);
		obj.setGLAdvaloremCess((arr[107] != null) ? arr[107].toString() : null);
		obj.setGLSpecificCess((arr[108] != null) ? arr[108].toString() : null);
		obj.setGLStateCessAdvalorem(
				(arr[109] != null) ? arr[109].toString() : null);
		obj.setSupplierType((arr[110] != null) ? arr[110].toString() : null);
		obj.setSupplierCode((arr[111] != null) ? arr[111].toString() : null);
		obj.setSupplierAddress1(
				(arr[112] != null) ? arr[112].toString() : null);
		obj.setSupplierAddress2(
				(arr[113] != null) ? arr[113].toString() : null);
		obj.setSupplierLocation(
				(arr[114] != null) ? arr[114].toString() : null);
		obj.setSupplierPincode((arr[115] != null) ? arr[115].toString() : null);
		obj.setStateApplyingCess(
				(arr[116] != null) ? arr[116].toString() : null);
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setCIF(CheckForNegativeValue(arr[117]));
			obj.setCustomDuty(CheckForNegativeValue(arr[118]));
		} else {
			obj.setCIF((arr[117] != null) ? arr[117].toString() : null);
			obj.setCustomDuty(
					(arr[118] != null) ? arr[118].toString() : null);
		}
		obj.setHSN((arr[119] != null) ? arr[119].toString() : null);
		obj.setProductCode((arr[120] != null) ? arr[120].toString() : null);
		obj.setProductDescription(
				(arr[121] != null) ? arr[121].toString() : null);
		obj.setCategoryOfProduct(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setUQC((arr[123] != null) ? arr[123].toString() : null);
		obj.setQuantity((arr[124] != null) ? arr[124].toString() : null);
		obj.setCessAdvaloremRate(
				(arr[125] != null) ? arr[125].toString() : null);

		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setCessAdvaloremAmount(CheckForNegativeValue(arr[126]));
			obj.setCessSpecificAmount(CheckForNegativeValue(arr[128]));
			obj.setStateCessAdvaloremAmount(CheckForNegativeValue(arr[130]));
		} else {
			obj.setCessAdvaloremAmount(
					(arr[126] != null) ? arr[126].toString() : null);
			obj.setCessSpecificAmount(
					(arr[128] != null) ? arr[128].toString() : null);
			obj.setStateCessAdvaloremAmount(
					(arr[130] != null) ? arr[130].toString() : null);
		}
		obj.setCessSpecificRate(
				(arr[127] != null) ? arr[127].toString() : null);

		obj.setStateCessAdvaloremRate(
				(arr[129] != null) ? arr[129].toString() : null);
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setItemOtherCharges(CheckForNegativeValue((arr[131])));
		} else {
			obj.setItemOtherCharges(
					(arr[131] != null) ? arr[131].toString() : null);
		}
		obj.setClaimRefundFlag((arr[132] != null) ? arr[132].toString() : null);
		obj.setAutoPopulateToRefund(
				(arr[133] != null) ? arr[133].toString() : null);
		obj.setAdjustementReferenceNo(
				(arr[134] != null) ? arr[134].toString() : null);
		obj.setAdjustementReferenceDate(
				(arr[135] != null) ? arr[135].toString() : null);
		obj.setCommonSupplyIndicator(
				(arr[136] != null) ? arr[136].toString() : null);
		obj.setITCEntitlement((arr[137] != null) ? arr[137].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[138] != null) ? arr[138].toString() : null);
		obj.setAccountingVoucherNumber(
				(arr[139] != null) ? arr[139].toString() : null);
		obj.setAccountingVoucherDate(
				(arr[140] != null) ? arr[140].toString() : null);
		obj.setGLPostingDate((arr[141] != null) ? arr[141].toString() : null);
		obj.setCustomerPOReferenceNumber(
				(arr[142] != null) ? arr[142].toString() : null);
		obj.setCustomerPOReferenceDate(
				(arr[143] != null) ? arr[143].toString() : null);
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setPurchaseOrderValue(CheckForNegativeValue(arr[144]));
		} else {
			obj.setPurchaseOrderValue(
					(arr[144] != null) ? arr[144].toString() : null);
		}
		obj.setVendorTaxPaidPercentage(
				(arr[145] != null) ? arr[145].toString() : null);
		obj.setVendorType((arr[146] != null) ? arr[146].toString() : null);
		obj.setHsnValue((arr[147] != null) ? arr[147].toString() : null);
		obj.setVendorRiskCategory(
				(arr[148] != null) ? arr[148].toString() : null);
		obj.setVendorPaymentTerms(
				(arr[149] != null) ? arr[149].toString() : null);
		obj.setVendorRemarks((arr[150] != null) ? arr[150].toString() : null);
		obj.setSourceTypeOfIRN((arr[151] != null) ? arr[151].toString() : null);
		obj.setIRN2B((arr[152] != null) ? arr[152].toString() : null);
		obj.setIRNPR((arr[153] != null) ? arr[153].toString() : null);
		obj.setIRNDate2B((arr[154] != null) ? arr[154].toString() : null);
		obj.setIRNDatePR((arr[155] != null) ? arr[155].toString() : null);
		obj.setUserDefinedField1(
				(arr[156] != null) ? arr[156].toString() : null);
		obj.setUserDefinedField2(
				(arr[157] != null) ? arr[157].toString() : null);
		obj.setUserDefinedField3(
				(arr[158] != null) ? arr[158].toString() : null);
		obj.setUserDefinedField4(
				(arr[159] != null) ? arr[159].toString() : null);
		obj.setUserDefinedField5(
				(arr[160] != null) ? arr[160].toString() : null);
		obj.setUserDefinedField6(
				(arr[161] != null) ? arr[161].toString() : null);
		obj.setUserDefinedField7(
				(arr[162] != null) ? arr[162].toString() : null);
		obj.setUserDefinedField8(
				(arr[163] != null) ? arr[163].toString() : null);
		obj.setUserDefinedField9(
				(arr[164] != null) ? arr[164].toString() : null);
		obj.setUserDefinedField10(
				(arr[165] != null) ? arr[165].toString() : null);
		obj.setUserDefinedField11(
				(arr[166] != null) ? arr[166].toString() : null);
		obj.setUserDefinedField12(
				(arr[167] != null) ? arr[167].toString() : null);
		obj.setUserDefinedField13(
				(arr[168] != null) ? arr[168].toString() : null);
		obj.setUserDefinedField14(
				(arr[169] != null) ? arr[169].toString() : null);
		obj.setUserDefinedField15(
				(arr[170] != null) ? arr[170].toString() : null);
		obj.setUserDefinedField28(
				(arr[171] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[171].toString()) : null);
		obj.setSystemDefinedField1(
				(arr[172] != null) ? arr[172].toString() : null);
		obj.setSystemDefinedField2(
				(arr[173] != null) ? arr[173].toString() : null);
		obj.setSystemDefinedField3(
				(arr[174] != null) ? arr[174].toString() : null);
		obj.setSystemDefinedField4(
				(arr[175] != null) ? arr[175].toString() : null);
		obj.setSystemDefinedField5(
				(arr[176] != null) ? arr[176].toString() : null);
		obj.setEWBNumber((arr[177] != null) ? arr[177].toString() : null);
		obj.setEWBDate((arr[178] != null) ? arr[178].toString() : null);
		obj.setMatchingID((arr[179] != null) ? arr[179].toString() : null);
		obj.setRequestID(
				(arr[180] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[180].toString()) : null);
		obj.setIDPR((arr[181] != null) ? arr[181].toString() : null);
		obj.setID2B((arr[182] != null) ? arr[182].toString() : null);
		obj.setInvoiceKeyPR((arr[183] != null) ? arr[183].toString() : null);
		obj.setInvoiceKey2B((arr[184] != null) ? arr[184].toString() : null);
		obj.setReferenceIDPR((arr[185] != null) ? arr[185].toString() : null);
		obj.setReferenceID2B((arr[186] != null) ? arr[186].toString() : null);
		obj.setSource((arr[187] != null) ? arr[187].toString() : null);
		obj.setAmendedParameters((arr[188] != null) ? arr[188].toString() : null);
		obj.setGstr3bTableNo((arr[189] != null) ? arr[189].toString() : null);

		obj.setTable4AValueIgst(
				(arr[190] != null) ? arr[190].toString() : null);
		obj.setTable4AValueCgst(
				(arr[191] != null) ? arr[191].toString() : null);
		obj.setTable4AValueSgst(
				(arr[192] != null) ? arr[192].toString() : null);
		obj.setTable4AValueCess(
				(arr[193] != null) ? arr[193].toString() : null);
		obj.setTable4BValueIgst(
				(arr[194] != null) ? arr[194].toString() : null);
		obj.setTable4BValueCgst(
				(arr[195] != null) ? arr[195].toString() : null);
		obj.setTable4BValueSgst(
				(arr[196] != null) ? arr[196].toString() : null);
		obj.setTable4BValueCess(
				(arr[197] != null) ? arr[197].toString() : null);
		obj.setTable4DValueIgst(
				(arr[198] != null) ? arr[198].toString() : null);
		obj.setTable4DValueCgst(
				(arr[199] != null) ? arr[199].toString() : null);
		obj.setTable4DValueSgst(
				(arr[200] != null) ? arr[200].toString() : null);
		obj.setTable4DValueCess(
				(arr[201] != null) ? arr[201].toString() : null);
		// 3b transactional report
		// adding negetive values logic

		return obj;

	}

	private String CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else {
				if (!value.toString().isEmpty()) {
					return "-" + value.toString().replaceFirst("-", "");
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private String addApostrophe(Object o) {
		if (o != null) {
			if (!(o.toString().isEmpty())) {
				return DownloadReportsConstant.CSVCHARACTER
						.concat(o.toString());
			} else
				return null;
		}
		return null;
	}
}

package com.ey.advisory.app.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GSTR2ProcessedInwarddto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ErrorMasterUtil;

import com.google.common.base.Strings;

/**
 * @author Mahesh.Golla
 *
 */

@Component("AsyncInwardInvoicemanagementReportMappingConvertor")
public class AsyncInwardInvoicemanagementReportMappingConvertor
		implements AsyncInvMangmntReportConvertor {

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public Object convert(Object[] arr) {

		GSTR2ProcessedInwarddto obj = new GSTR2ProcessedInwarddto();

		String errDesc = null;
		String infoDesc = null;

		String errCode = (arr[0] != null) ? arr[0].toString() : null;
		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errCodeList.replaceAll(String::trim);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "INWARD");
		}
		obj.setDigigstErrorDesc(errDesc);
		String infoCode = (arr[1] != null) ? arr[1].toString() : null;

		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoCodeList.replaceAll(String::trim);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "INWARD");
		}

		obj.setDigigstInformationDesc(infoDesc);

		obj.setIrn(arr[2] != null ? arr[2].toString() : null);
		obj.setIrnDate(arr[3] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString())
				: null);
		obj.setTaxScheme(arr[4] != null ? arr[4].toString() : null);

		String supplyType = arr[5] != null ? arr[5].toString() : null;
		if (!Strings.isNullOrEmpty(supplyType)) {
			obj.setSupplyType(supplyType.toUpperCase());
		}

		obj.setDocCategory(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentType(arr[7] != null ? arr[7].toString() : null);
		obj.setDocumentNumber(arr[8] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);

		if (arr[9] != null) {
			String strdate = arr[9].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {
			obj.setDocumentDate(null);
		}
		obj.setReverseChargeFlag(arr[10] != null ? arr[10].toString() : null);

		obj.setSupplierGSTIN(arr[11] != null ? arr[11].toString() : null);
		obj.setSupplierTradeName(arr[12] != null ? arr[12].toString() : null);
		obj.setSupplierLegalName(arr[13] != null ? arr[13].toString() : null);
		obj.setSupplierAddress1(arr[14] != null ? arr[14].toString() : null);
		obj.setSupplierAddress2(arr[15] != null ? arr[15].toString() : null);
		obj.setSupplierLocation(
				arr[16] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[16].toString()) : null);
		obj.setSupplierPincode(arr[17] != null ? arr[17].toString() : null);
		obj.setSupplierStateCode(
				arr[18] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[18].toString()) : null);
		obj.setSupplierPhone(arr[19] != null ? arr[19].toString() : null);
		obj.setSupplierEmail(arr[20] != null ? arr[20].toString() : null);

		obj.setCustomerGSTIN(arr[21] != null ? arr[21].toString() : null);
		obj.setCustomerTradeName(arr[22] != null ? arr[22].toString() : null);
		obj.setCustomerLegalName(arr[23] != null ? arr[23].toString() : null);
		obj.setCustomerAddress1(arr[24] != null ? arr[24].toString() : null);
		obj.setCustomerAddress2(arr[25] != null ? arr[25].toString() : null);
		obj.setCustomerLocation(
				arr[26] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[26].toString()) : null);
		obj.setCustomerPincode(arr[27] != null ? arr[27].toString() : null);
		obj.setCustomerStateCode(
				arr[28] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[28].toString()) : null);
		obj.setBillingPOS(arr[29] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[29].toString()) : null);
		obj.setCustomerPhone(arr[30] != null ? arr[30].toString() : null);
		obj.setCustomerEmail(arr[31] != null ? arr[31].toString() : null);

		obj.setDispatcherGSTIN(arr[32] != null ? arr[32].toString() : null);
		obj.setDispatcherTradeName(arr[33] != null ? arr[33].toString() : null);
		obj.setDispatcherAddress1(arr[34] != null ? arr[34].toString() : null);
		obj.setDispatcherAddress2(arr[35] != null ? arr[35].toString() : null);
		obj.setDispatcherLocation(
				arr[36] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[36].toString()) : null);
		obj.setDispatcherPincode(arr[37] != null ? arr[37].toString() : null);
		obj.setDispatcherStateCode(
				arr[38] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[38].toString()) : null);

		obj.setShipToGSTIN(arr[39] != null ? arr[39].toString() : null);
		obj.setShipToTradeName(arr[40] != null ? arr[40].toString() : null);
		obj.setShipToLegalName(arr[41] != null ? arr[41].toString() : null);
		obj.setShipToAddress1(arr[42] != null ? arr[42].toString() : null);
		obj.setShipToAddress2(arr[43] != null ? arr[43].toString() : null);
		obj.setShipToLocation(
				arr[44] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[44].toString()) : null);
		obj.setShipToPincode(arr[45] != null ? arr[45].toString() : null);

		obj.setShipToStateCode(
				arr[46] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[46].toString()) : null);

		obj.setItemSerialNumber(arr[47] != null ? arr[47].toString() : null);
		obj.setProductSerialNumber(
				arr[48] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[48].toString()) : null);

		obj.setProductName(arr[49] != null ? arr[49].toString() : null);
		obj.setProductDescription(arr[50] != null ? arr[50].toString() : null);
		obj.setIsService(arr[51] != null ? arr[51].toString() : null);
		// obj.setHsn(arr[77] != null ? arr[77].toString() : null);
		obj.setHsn(arr[52] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[52].toString()) : null);
		obj.setBarcode(arr[53] != null ? arr[53].toString() : null);
		obj.setBatchName(arr[54] != null ? arr[54].toString() : null);
		if (arr[55] != null) {
			String strdate = arr[55].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBatchExpiryDate(newDate);
		} else {
			obj.setBatchExpiryDate(null);
		}
		if (arr[56] != null) {
			String strdate = arr[56].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setWarrantyDate(newDate);
		} else {
			obj.setWarrantyDate(null);
		}
		obj.setOrderlineReference(arr[57] != null ? arr[57].toString() : null);
		obj.setAttributeName(arr[58] != null ? arr[58].toString() : null);
		obj.setAttributeValue(
				arr[59] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[59].toString()) : null);
		obj.setOriginCountry(arr[60] != null ? arr[60].toString() : null);
		obj.setuQC(arr[61] != null ? arr[61].toString() : null);
		obj.setQuantity(arr[62] != null ? arr[62].toString() : null);

		obj.setFreeQuantity(arr[63] != null ? arr[63].toString() : null);
		obj.setUnitPrice(arr[64] != null ? arr[64].toString() : null);
		obj.setItemAmount(arr[65] != null ? arr[65].toString() : null);
		obj.setItemDiscount(arr[66] != null ? arr[66].toString() : null);
		obj.setPreTaxAmount(arr[67] != null ? arr[67].toString() : null);
		obj.setItemAssessableAmount(
				arr[68] != null ? arr[68].toString() : null);
		obj.setiGSTRate(arr[69] != null ? arr[69].toString() : null);
		obj.setiGSTAmount(arr[70] != null ? arr[70].toString() : null);
		obj.setcGSTRate(arr[71] != null ? arr[71].toString() : null);
		obj.setcGSTAmount(arr[72] != null ? arr[72].toString() : null);
		obj.setsGSTRate(arr[73] != null ? arr[73].toString() : null);
		obj.setsGSTAmount(arr[74] != null ? arr[74].toString() : null);
		obj.setCessAdvaloremRate(arr[75] != null ? arr[75].toString() : null);
		obj.setCessAdvaloremAmount(arr[76] != null ? arr[76].toString() : null);
		obj.setCessSpecificRate(arr[77] != null ? arr[77].toString() : null);
		obj.setCessSpecificAmount(arr[78] != null ? arr[78].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[79] != null ? arr[79].toString() : null);
		obj.setStateCessAdvaloremAmount(
				arr[80] != null ? arr[80].toString() : null);
		obj.setStateCessSpecificRate(
				arr[81] != null ? arr[81].toString() : null);
		obj.setStateCessSpecificAmount(
				arr[82] != null ? arr[82].toString() : null);

		obj.setItemOtherCharges(arr[83] != null ? arr[83].toString() : null);
		obj.setTotalItemAmount(arr[84] != null ? arr[84].toString() : null);
		obj.setInvoiceOtherCharges(arr[85] != null ? arr[85].toString() : null);
		obj.setInvoiceAssessableAmount(
				arr[86] != null ? arr[86].toString() : null);
		obj.setInvoiceIGSTAmount(arr[87] != null ? arr[87].toString() : null);
		obj.setInvoiceCGSTAmount(arr[88] != null ? arr[88].toString() : null);
		obj.setInvoiceSGSTAmount(arr[89] != null ? arr[89].toString() : null);
		obj.setInvoiceCessAdvaloremAmount(
				arr[90] != null ? arr[90].toString() : null);
		obj.setInvoiceCessSpecificAmount(
				arr[91] != null ? arr[91].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmount(
				arr[92] != null ? arr[92].toString() : null);
		obj.setInvoiceStateCessSpecificAmount(
				arr[93] != null ? arr[93].toString() : null);
		obj.setInvoiceValue(arr[94] != null ? arr[94].toString() : null);
		obj.setRoundOff(arr[95] != null ? arr[95].toString() : null);
		obj.setTotalInvoiceValue(arr[96] != null ? arr[96].toString() : null);
		obj.setEligibilityIndicator(
				arr[97] != null ? arr[97].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[98] != null ? arr[98].toString() : null);
		obj.setAvailableIgst(arr[99] != null ? arr[99].toString() : null);
		obj.setAvailableCgst(arr[100] != null ? arr[100].toString() : null);
		obj.setAvailableSgst(arr[101] != null ? arr[101].toString() : null);
		obj.setAvailableCess(arr[102] != null ? arr[102].toString() : null);
		obj.setiTCEntitlement(arr[103] != null ? arr[103].toString() : null);
		obj.setiTCReversalIdentifier(
				arr[104] != null ? arr[104].toString() : null);
		obj.settCSFlagIncomeTax(arr[105] != null ? arr[105].toString() : null);
		obj.settCSRateIncomeTax(arr[106] != null ? arr[106].toString() : null);
		obj.settCSAmountIncomeTax(
				arr[107] != null ? arr[107].toString() : null);
		obj.setCurrencyCode(arr[108] != null ? arr[108].toString() : null);
		obj.setCountryCode(arr[109] != null ? arr[109].toString() : null);
		obj.setInvoiceValueFC(arr[110] != null ? arr[110].toString() : null);
		obj.setPortCode(arr[111] != null ? arr[111].toString() : null);
		obj.setBillofEntry(arr[112] != null ? arr[112].toString() : null);

		if (arr[113] != null) {
			String strdate = arr[113].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBillofEntryDate(newDate);
		} else {
			obj.setBillofEntryDate(null);
		}

		obj.setInvoiceRemarks(arr[114] != null ? arr[114].toString() : null);
		if (arr[115] != null) {
			String strdate = arr[115].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodStartDate(newDate);
		} else {
			obj.setInvoicePeriodStartDate(null);
		}

		if (arr[116] != null) {
			String strdate = arr[116].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodEndDate(newDate);
		} else {
			obj.setInvoicePeriodEndDate(null);
		}

		obj.setPreceedingInvoiceNumber(
				arr[117] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[117].toString()) : null);
		if (arr[118] != null) {
			String strdate = arr[118].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPreceedingInvoiceDate(newDate);
		} else {
			obj.setPreceedingInvoiceDate(null);
		}

		obj.setOtherReference(
				arr[119] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[119].toString()) : null);
		obj.setReceiptAdviceReference(
				arr[120] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[120].toString()) : null);
		obj.setReceiptAdviceDate(arr[121] != null ? arr[121].toString() : null);
		obj.setTenderReference(
				arr[122] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[122].toString()) : null);
		obj.setContractReference(
				arr[123] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[123].toString()) : null);
		obj.setExternalReference(
				arr[124] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[124].toString()) : null);
		obj.setProjectReference(
				arr[125] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[125].toString()) : null);
		obj.setCustomerPOReferenceNumber(
				arr[126] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[126].toString()) : null);
		obj.setCustomerPOReferenceDate(
				arr[127] != null ? arr[127].toString() : null);

		obj.setPayeeName(arr[128] != null ? arr[128].toString() : null);
		obj.setModeOfPayment(arr[129] != null ? arr[129].toString() : null);
		obj.setBranchOrIFSCCode(arr[130] != null ? arr[130].toString() : null);
		obj.setPaymentTerms(arr[131] != null ? arr[131].toString() : null);
		obj.setPaymentInstruction(
				arr[132] != null ? arr[132].toString() : null);
		obj.setCreditTransfer(arr[133] != null ? arr[133].toString() : null);
		obj.setDirectDebit(arr[134] != null ? arr[134].toString() : null);
		obj.setCreditDays(arr[135] != null ? arr[135].toString() : null);
		obj.setPaidAmount(arr[136] != null ? arr[136].toString() : null);
		obj.setBalanceAmount(arr[137] != null ? arr[137].toString() : null);
		if (arr[138] != null) {
			String strdate = arr[138].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPaymentDueDate(newDate);
		} else {
			obj.setPaymentDueDate(null);
		}

		obj.setAccountDetail(
				arr[139] != null ? "'" + arr[139].toString() : null);
		// Object accountdetail =
		// CommonUtility.exponentialAndZeroCheck(arr[157]);
		// obj.setAccountDetail(
		// accountdetail != null ? accountdetail.toString() : null);
		obj.setEcomGSTIN(arr[140] != null ? arr[140].toString() : null);
		obj.setSupportingDocURL(arr[141] != null ? arr[141].toString() : null);
		obj.setSupportingDocument(
				arr[142] != null ? arr[142].toString() : null);
		obj.setAdditionalInformation(
				arr[143] != null ? arr[143].toString() : null);
		obj.setTransactionType(arr[144] != null ? arr[144].toString() : null);
		obj.setSubSupplyType(arr[145] != null ? arr[145].toString() : null);
		obj.setOtherSupplyTypeDescription(
				arr[146] != null ? arr[146].toString() : null);
		obj.setTransporterID(arr[147] != null ? arr[147].toString() : null);
		obj.setTransporterName(arr[148] != null ? arr[148].toString() : null);
		obj.setTransportMode(arr[149] != null ? arr[149].toString() : null);
		obj.setTransportDocNo(arr[150] != null ? arr[150].toString() : null);
		if (arr[151] != null) {
			String strdate = arr[151].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setTransportDocDate(newDate);
		} else {
			obj.setTransportDocDate(null);
		}
		obj.setDistance(arr[152] != null ? arr[152].toString() : null);
		obj.setVehicleNo(arr[153] != null ? arr[153].toString() : null);
		obj.setVehicleType(arr[154] != null ? arr[154].toString() : null);
		obj.setReturnPeriod(
				arr[155] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[155].toString()) : null);
		obj.setOriginalDocumentType(
				arr[156] != null ? arr[156].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[157] != null ? arr[157].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[158] != null ? arr[158].toString() : null);
		obj.setSec7ofIGSTFlag(arr[159] != null ? arr[159].toString() : null);
		obj.setClaimRefndFlag(arr[160] != null ? arr[160].toString() : null);
		obj.setAutoPopltToRefund(arr[161] != null ? arr[161].toString() : null);
		obj.setcRDRPreGST(arr[162] != null ? arr[162].toString() : null);
		obj.setSupplierType(arr[163] != null ? arr[163].toString() : null);
		obj.setSupplierCode(arr[164] != null ? arr[164].toString() : null);
		obj.setProductCode(arr[165] != null ? arr[165].toString() : null);
		obj.setCategoryOfProduct(arr[166] != null ? arr[166].toString() : null);
		obj.setStateApplyingCess(arr[167] != null ? arr[167].toString() : null);
		obj.setCif(arr[168] != null ? arr[168].toString() : null);
		obj.setCustomDuty(arr[169] != null ? arr[169].toString() : null);
		obj.setExchangeRate(arr[170] != null ? arr[170].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[171] != null ? arr[171].toString() : null);
		obj.settCSFlagGST(arr[172] != null ? arr[172].toString() : null);
		obj.settCSIGSTAmount(arr[173] != null ? arr[173].toString() : null);
		obj.settCSCGSTAmount(arr[174] != null ? arr[174].toString() : null);
		obj.settCSSGSTAmount(arr[175] != null ? arr[175].toString() : null);
		obj.settDSFlagGST(arr[176] != null ? arr[176].toString() : null);
		obj.settDSIGSTAmount(arr[177] != null ? arr[177].toString() : null);
		obj.settDSCGSTAmount(arr[178] != null ? arr[178].toString() : null);
		obj.settDSSGSTAmount(arr[179] != null ? arr[179].toString() : null);
		obj.setUserID(arr[180] != null ? arr[180].toString() : null);
		obj.setCompanyCode(arr[181] != null ? arr[181].toString() : null);
		obj.setSourceIdentifier(arr[182] != null ? arr[182].toString() : null);
		obj.setSourceFileName(arr[183] != null ? arr[183].toString() : null);
		obj.setPlantCode(arr[184] != null ? arr[184].toString() : null);
		obj.setDivision(arr[185] != null ? arr[185].toString() : null);
		obj.setSubDivision(arr[186] != null ? arr[186].toString() : null);
		obj.setLocation(arr[187] != null ? arr[187].toString() : null);
		obj.setPurchaseOrganisation(
				arr[188] != null ? arr[188].toString() : null);
		obj.setProfitCentre1(arr[189] != null ? arr[189].toString() : null);
		obj.setProfitCentre2(arr[190] != null ? arr[190].toString() : null);
		obj.setProfitCentre3(arr[191] != null ? arr[191].toString() : null);
		obj.setProfitCentre4(arr[192] != null ? arr[192].toString() : null);
		obj.setProfitCentre5(arr[193] != null ? arr[193].toString() : null);
		obj.setProfitCentre6(arr[194] != null ? arr[194].toString() : null);
		obj.setProfitCentre7(arr[195] != null ? arr[195].toString() : null);
		obj.setProfitCentre8(arr[196] != null ? arr[196].toString() : null);
		obj.setGlAssessableValue(arr[197] != null ? arr[197].toString() : null);
		obj.setGlIGST(arr[198] != null ? arr[198].toString() : null);
		obj.setGlCGST(arr[199] != null ? arr[199].toString() : null);
		obj.setGlSGST(arr[200] != null ? arr[200].toString() : null);
		obj.setGlAdvaloremCess(arr[201] != null ? arr[201].toString() : null);
		obj.setGlSpecificCess(arr[202] != null ? arr[202].toString() : null);
		obj.setgLStateCessAdvalorem(
				arr[203] != null ? arr[203].toString() : null);
		obj.setgLStateCessSpecific(
				arr[204] != null ? arr[204].toString() : null);
		if (arr[205] != null) {
			String strdate = arr[205].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setGlPostingDate(newDate);
		} else {
			obj.setGlPostingDate(null);
		}
		obj.setPurchaseOrderValue(
				arr[206] != null ? arr[206].toString() : null);
		obj.seteWBNumber(arr[207] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[207].toString()) : null);
		obj.seteWBDate(arr[208] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[208].toString()) : null);
		obj.setAccountingVoucherNumber(
				arr[209] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[209].toString()) : null);
		if (arr[210] != null) {
			String strdate = arr[210].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAccountingVoucherDate(newDate);
		} else {
			obj.setAccountingVoucherDate(null);
		}
		obj.setDocumentReferenceNumber(
				arr[211] != null ? arr[211].toString() : null);
		obj.setUserDefField1(arr[212] != null ? arr[212].toString() : null);
		obj.setUserDefField2(arr[213] != null ? arr[213].toString() : null);
		obj.setUserDefField3(arr[214] != null ? arr[214].toString() : null);
		obj.setUserDefField4(arr[215] != null ? arr[215].toString() : null);
		obj.setUserDefField5(arr[216] != null ? arr[216].toString() : null);
		obj.setUserDefField6(arr[217] != null ? arr[217].toString() : null);
		obj.setUserDefField7(arr[218] != null ? arr[218].toString() : null);
		obj.setUserDefField8(arr[219] != null ? arr[219].toString() : null);
		obj.setUserDefField9(arr[220] != null ? arr[220].toString() : null);
		obj.setUserDefField10(arr[221] != null ? arr[221].toString() : null);
		obj.setUserDefField11(arr[222] != null ? arr[222].toString() : null);
		obj.setUserDefField12(arr[223] != null ? arr[223].toString() : null);
		obj.setUserDefField13(arr[224] != null ? arr[224].toString() : null);
		obj.setUserDefField14(arr[225] != null ? arr[225].toString() : null);
		obj.setUserDefField15(arr[226] != null ? arr[226].toString() : null);
		obj.setUserDefField16(arr[227] != null ? arr[227].toString() : null);
		obj.setUserDefField17(arr[228] != null ? arr[228].toString() : null);
		obj.setUserDefField18(arr[229] != null ? arr[229].toString() : null);
		obj.setUserDefField19(arr[230] != null ? arr[230].toString() : null);
		obj.setUserDefField20(arr[231] != null ? arr[231].toString() : null);
		obj.setUserDefField21(arr[232] != null ? arr[232].toString() : null);
		obj.setUserDefField22(arr[233] != null ? arr[233].toString() : null);
		obj.setUserDefField23(arr[234] != null ? arr[234].toString() : null);
		obj.setUserDefField24(arr[235] != null ? arr[235].toString() : null);
		obj.setUserDefField25(arr[236] != null ? arr[236].toString() : null);
		obj.setUserDefField26(arr[237] != null ? arr[237].toString() : null);
		obj.setUserDefField27(arr[238] != null ? arr[238].toString() : null);
		obj.setUserDefField28(arr[239] != null ? arr[239].toString() : null);
		obj.setUserDefField29(arr[240] != null ? arr[240].toString() : null);
		obj.setUserDefField30(arr[241] != null ? arr[241].toString() : null);
		obj.setSource(arr[242] != null ? arr[242].toString() : null);
		obj.setSourceID(arr[243] != null ? arr[243].toString() : null);
		obj.setFileName(arr[244] != null ? arr[244].toString() : null);
		obj.setDigiGSTDateTime(arr[245] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[245].toString()) : null);
		obj.setReturnType(arr[246] != null ? arr[246].toString() : null);
		obj.setDataCategory(arr[247] != null ? arr[247].toString() : null);
		obj.setTableNumber(arr[248] != null ? arr[248].toString() : null);
		obj.setInvoiceValueDigigst(
				arr[249] != null ? arr[249].toString() : null);
		return obj;
	}
}
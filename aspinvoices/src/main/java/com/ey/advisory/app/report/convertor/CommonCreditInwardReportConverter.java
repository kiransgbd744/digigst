package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ReversalInwardDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ReportConvertor;

import lombok.extern.slf4j.Slf4j;

@Component("CommonCreditInwardReportConverter")
@Slf4j
public class CommonCreditInwardReportConverter implements ReportConvertor {

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public Object convert(Object[] arr, String reportType) {

		ReversalInwardDto obj = new ReversalInwardDto();

		obj.setCategory(arr[0] != null ? arr[0].toString() : null);
		obj.setIrn(arr[1] != null ? arr[1].toString() : null);
		obj.setIrnDate(arr[2] != null ? arr[2].toString() : null);
		obj.setTaxScheme(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplyType(arr[4] != null ? arr[4].toString() : null);
		obj.setDocCategory(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[7] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		if (arr[8] != null) {
			String strdate = arr[8].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setDocumentDate(null);
		}
		obj.setReverseChargeFlag(arr[9] != null ? arr[9].toString() : null);
		obj.setSupplierGSTIN(arr[10] != null ? arr[10].toString() : null);
		obj.setSupplierTradeName(arr[11] != null ? arr[11].toString() : null);
		obj.setSupplierLegalName(arr[12] != null ? arr[12].toString() : null);
		obj.setSupplierAddress1(arr[13] != null ? arr[13].toString() : null);
		obj.setSupplierAddress2(arr[14] != null ? arr[14].toString() : null);
		obj.setSupplierLocation(arr[15] != null ? arr[15].toString() : null);
		obj.setSupplierPincode(arr[16] != null ? arr[16].toString() : null);
		obj.setSupplierStateCode(
				arr[17] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[17].toString()) : null);
		obj.setSupplierPhone(arr[18] != null ? arr[18].toString() : null);
		obj.setSupplierEmail(arr[19] != null ? arr[19].toString() : null);
		obj.setCustomerGSTIN(arr[20] != null ? arr[20].toString() : null);
		obj.setCustomerTradeName(arr[21] != null ? arr[21].toString() : null);
		obj.setCustomerLegalName(arr[22] != null ? arr[22].toString() : null);
		obj.setCustomerAddress1(arr[23] != null ? arr[23].toString() : null);
		obj.setCustomerAddress2(arr[24] != null ? arr[24].toString() : null);
		obj.setCustomerLocation(arr[25] != null ? arr[25].toString() : null);
		obj.setCustomerPincode(arr[26] != null ? arr[26].toString() : null);
		obj.setCustomerStateCode(
				arr[27] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[27].toString()) : null);
		obj.setBillingPOS(arr[28] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[28].toString()) : null);
		obj.setCustomerPhone(arr[29] != null ? arr[29].toString() : null);
		obj.setCustomerEmail(arr[30] != null ? arr[30].toString() : null);
		obj.setDispatcherGSTIN(arr[31] != null ? arr[31].toString() : null);
		obj.setDispatcherTradeName(arr[32] != null ? arr[32].toString() : null);
		obj.setDispatcherAddress1(arr[33] != null ? arr[33].toString() : null);
		obj.setDispatcherAddress2(arr[34] != null ? arr[34].toString() : null);
		obj.setDispatcherLocation(arr[35] != null ? arr[35].toString() : null);
		obj.setDispatcherPincode(arr[36] != null ? arr[36].toString() : null);
		obj.setDispatcherStateCode(
				arr[37] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[37].toString()) : null);
		obj.setShipToGSTIN(arr[38] != null ? arr[38].toString() : null);
		obj.setShipToTradeName(arr[39] != null ? arr[39].toString() : null);
		obj.setShipToLegalName(arr[40] != null ? arr[40].toString() : null);
		obj.setShipToAddress1(arr[41] != null ? arr[41].toString() : null);
		obj.setShipToAddress2(arr[42] != null ? arr[42].toString() : null);
		obj.setShipToLocation(arr[43] != null ? arr[43].toString() : null);
		obj.setShipToPincode(arr[44] != null ? arr[44].toString() : null);
		obj.setShipToStateCode(
				arr[45] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[45].toString()) : null);
		obj.setItemSerialNumber(arr[46] != null ? arr[46].toString() : null);
		obj.setProductSerialNumber(arr[47] != null ? arr[47].toString() : null);
		obj.setProductName(arr[48] != null ? arr[48].toString() : null);
		obj.setProductDescription(arr[49] != null ? arr[49].toString() : null);
		obj.setIsService(arr[50] != null ? arr[50].toString() : null);
		obj.setHsn(arr[51] != null ? arr[51].toString() : null);
		obj.setBarcode(arr[52] != null ? arr[52].toString() : null);
		obj.setBatchName(arr[53] != null ? arr[53].toString() : null);
		if (arr[54] != null) {
			String strdate = arr[54].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBatchExpiryDate(newDate);
		} else {
			obj.setBatchExpiryDate(null);
		}
		if (arr[55] != null) {
			String strdate = arr[55].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setWarrantyDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setWarrantyDate(null);
		}
		obj.setOrderlineReference(arr[56] != null ? arr[56].toString() : null);
		obj.setAttributeName(arr[57] != null ? arr[57].toString() : null);
		obj.setAttributeValue(arr[58] != null ? arr[58].toString() : null);
		obj.setOriginCountry(arr[59] != null ? arr[59].toString() : null);
		obj.setuQC(arr[60] != null ? arr[60].toString() : null);
		obj.setQuantity(arr[61] != null ? arr[61].toString() : null);
		obj.setFreeQuantity(arr[62] != null ? arr[62].toString() : null);
		obj.setUnitPrice(arr[63] != null ? arr[63].toString() : null);
		obj.setItemAmount(arr[64] != null ? arr[64].toString() : null);
		obj.setItemDiscount(arr[65] != null ? arr[65].toString() : null);
		obj.setPreTaxAmount(arr[66] != null ? arr[66].toString() : null);
		obj.setItemAssessableAmount(
				arr[67] != null ? arr[67].toString() : null);
		obj.setiGSTRate(arr[68] != null ? arr[68].toString() : null);
		obj.setiGSTAmount(arr[69] != null ? arr[69].toString() : null);
		obj.setcGSTRate(arr[70] != null ? arr[70].toString() : null);
		obj.setcGSTAmount(arr[71] != null ? arr[71].toString() : null);
		obj.setsGSTRate(arr[72] != null ? arr[72].toString() : null);
		obj.setsGSTAmount(arr[73] != null ? arr[73].toString() : null);
		obj.setCessAdvaloremRate(arr[74] != null ? arr[74].toString() : null);
		obj.setCessAdvaloremAmount(arr[75] != null ? arr[75].toString() : null);
		obj.setCessSpecificRate(arr[76] != null ? arr[76].toString() : null);
		obj.setCessSpecificAmount(arr[77] != null ? arr[77].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[78] != null ? arr[78].toString() : null);
		obj.setStateCessAdvaloremAmount(
				arr[79] != null ? arr[79].toString() : null);
		obj.setStateCessSpecificRate(
				arr[80] != null ? arr[80].toString() : null);
		obj.setStateCessSpecificAmount(
				arr[81] != null ? arr[81].toString() : null);
		obj.setItemOtherCharges(arr[82] != null ? arr[82].toString() : null);
		obj.setTotalItemAmount(arr[83] != null ? arr[83].toString() : null);
		obj.setInvoiceOtherCharges(arr[84] != null ? arr[84].toString() : null);
		obj.setInvoiceAssessableAmount(
				arr[85] != null ? arr[85].toString() : null);
		obj.setInvoiceIGSTAmount(arr[86] != null ? arr[86].toString() : null);
		obj.setInvoiceCGSTAmount(arr[87] != null ? arr[87].toString() : null);
		obj.setInvoiceSGSTAmount(arr[88] != null ? arr[88].toString() : null);
		obj.setInvoiceCessAdvaloremAmount(
				arr[89] != null ? arr[89].toString() : null);
		obj.setInvoiceCessSpecificAmount(
				arr[90] != null ? arr[90].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmount(
				arr[91] != null ? arr[91].toString() : null);
		obj.setInvoiceStateCessSpecificAmount(
				arr[92] != null ? arr[92].toString() : null);
		obj.setInvoiceValue(arr[93] != null ? arr[93].toString() : null);
		obj.setRoundOff(arr[94] != null ? arr[94].toString() : null);
		obj.setTotalInvoiceValue(arr[95] != null ? arr[95].toString() : null);
		obj.setEligibilityIndicator(
				arr[96] != null ? arr[96].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[97] != null ? arr[97].toString() : null);
		obj.setAvailableIgst(arr[98] != null ? arr[98].toString() : null);
		obj.setAvailableCgst(arr[99] != null ? arr[99].toString() : null);
		obj.setAvailableSgst(arr[100] != null ? arr[100].toString() : null);
		obj.setAvailableCess(arr[101] != null ? arr[101].toString() : null);
		obj.setiTCEntitlement(arr[102] != null ? arr[102].toString() : null);
		obj.setiTCReversalIdentifier(
				arr[103] != null ? arr[103].toString() : null);
		obj.settCSFlagIncomeTax(arr[104] != null ? arr[104].toString() : null);
		obj.settCSRateIncomeTax(arr[105] != null ? arr[105].toString() : null);
		obj.settCSAmountIncomeTax(
				arr[106] != null ? arr[106].toString() : null);
		obj.setCurrencyCode(arr[107] != null ? arr[107].toString() : null);
		obj.setCountryCode(arr[108] != null ? arr[108].toString() : null);
		obj.setInvoiceValueFC(arr[109] != null ? arr[109].toString() : null);
		obj.setPortCode(arr[110] != null ? arr[110].toString() : null);
		obj.setBillofEntry(
				arr[111] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[111].toString()) : null);
		if (arr[112] != null) {
			String strdate = arr[112].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBillofEntryDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setBillofEntryDate(null);
		}

		obj.setInvoiceRemarks(arr[113] != null ? arr[113].toString() : null);
		if (arr[114] != null) {
			String strdate = arr[114].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodStartDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setInvoicePeriodStartDate(null);
		}
		if (arr[115] != null) {
			String strdate = arr[115].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodEndDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setInvoicePeriodEndDate(null);
		}
		obj.setPreceedingInvoiceNumber(
				arr[116] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[116].toString()) : null);
		if (arr[117] != null) {
			String strdate = arr[117].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPreceedingInvoiceDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setPreceedingInvoiceDate(null);
		}
		obj.setOtherReference(arr[118] != null ? arr[118].toString() : null);
		obj.setReceiptAdviceReference(
				arr[119] != null ? arr[119].toString() : null);
		obj.setReceiptAdviceDate(arr[120] != null ? arr[120].toString() : null);
		obj.setTenderReference(arr[121] != null ? arr[121].toString() : null);
		obj.setContractReference(arr[122] != null ? arr[122].toString() : null);
		obj.setExternalReference(arr[123] != null ? arr[123].toString() : null);
		obj.setProjectReference(arr[124] != null ? arr[124].toString() : null);
		obj.setCustomerPOReferenceNumber(
				arr[125] != null ? arr[125].toString() : null);
		obj.setCustomerPOReferenceDate(
				arr[126] != null ? arr[126].toString() : null);
		obj.setPayeeName(arr[127] != null ? arr[127].toString() : null);
		obj.setModeOfPayment(arr[128] != null ? arr[128].toString() : null);
		obj.setBranchOrIFSCCode(arr[129] != null ? arr[129].toString() : null);
		obj.setPaymentTerms(arr[130] != null ? arr[130].toString() : null);
		obj.setPaymentInstruction(
				arr[131] != null ? arr[131].toString() : null);
		obj.setCreditTransfer(arr[132] != null ? arr[132].toString() : null);
		obj.setDirectDebit(arr[133] != null ? arr[133].toString() : null);
		obj.setCreditDays(arr[134] != null ? arr[134].toString() : null);
		obj.setPaidAmount(arr[135] != null ? arr[135].toString() : null);
		obj.setBalanceAmount(arr[136] != null ? arr[136].toString() : null);
		if (arr[137] != null) {
			String strdate = arr[137].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPaymentDueDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setPaymentDueDate(null);
		}
		obj.setAccountDetail(arr[138] != null ? arr[138].toString() : null);
		obj.setEcomGSTIN(arr[139] != null ? arr[139].toString() : null);
		obj.setSupportingDocURL(arr[140] != null ? arr[140].toString() : null);
		obj.setSupportingDocument(
				arr[141] != null ? arr[141].toString() : null);
		obj.setAdditionalInformation(
				arr[142] != null ? arr[142].toString() : null);
		obj.setTransactionType(arr[143] != null ? arr[143].toString() : null);
		obj.setSubSupplyType(arr[144] != null ? arr[144].toString() : null);
		obj.setOtherSupplyTypeDescription(
				arr[145] != null ? arr[145].toString() : null);
		obj.setTransporterID(arr[146] != null ? arr[146].toString() : null);
		obj.setTransporterName(arr[147] != null ? arr[147].toString() : null);
		obj.setTransportMode(arr[148] != null ? arr[148].toString() : null);
		obj.setTransportDocNo(arr[149] != null ? arr[149].toString() : null);
		if (arr[150] != null) {
			String strdate = arr[150].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setTransportDocDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setTransportDocDate(null);
		}
		obj.setDistance(arr[151] != null ? arr[151].toString() : null);
		obj.setVehicleNo(arr[152] != null ? arr[152].toString() : null);
		obj.setVehicleType(arr[153] != null ? arr[153].toString() : null);
		obj.setReturnPeriod(
				arr[154] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[154].toString()) : null);
		obj.setOriginalDocumentType(
				arr[155] != null ? arr[155].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[156] != null ? arr[156].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[157] != null ? arr[157].toString() : null);
		obj.setSec7ofIGSTFlag(arr[158] != null ? arr[158].toString() : null);
		obj.setClaimRefndFlag(arr[159] != null ? arr[159].toString() : null);
		obj.setAutoPopltToRefund(arr[160] != null ? arr[160].toString() : null);
		obj.setcRDRPreGST(arr[161] != null ? arr[161].toString() : null);
		obj.setSupplierType(arr[162] != null ? arr[162].toString() : null);
		obj.setSupplierCode(arr[163] != null ? arr[163].toString() : null);
		obj.setProductCode(arr[164] != null ? arr[164].toString() : null);
		obj.setCategoryOfProduct(arr[165] != null ? arr[165].toString() : null);
		obj.setStateApplyingCess(arr[166] != null ? arr[166].toString() : null);
		obj.setCif(arr[167] != null ? arr[167].toString() : null);
		obj.setCustomDuty(arr[168] != null ? arr[168].toString() : null);
		obj.setExchangeRate(arr[169] != null ? arr[169].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[170] != null ? arr[170].toString() : null);
		obj.settCSFlagGST(arr[171] != null ? arr[171].toString() : null);
		obj.settCSIGSTAmount(arr[172] != null ? arr[172].toString() : null);
		obj.settCSCGSTAmount(arr[173] != null ? arr[173].toString() : null);
		obj.settCSSGSTAmount(arr[174] != null ? arr[174].toString() : null);
		obj.settDSFlagGST(arr[175] != null ? arr[175].toString() : null);
		obj.settDSIGSTAmount(arr[176] != null ? arr[176].toString() : null);
		obj.settDSCGSTAmount(arr[177] != null ? arr[177].toString() : null);
		obj.settDSSGSTAmount(arr[178] != null ? arr[178].toString() : null);
		obj.setUserID(arr[179] != null ? arr[179].toString() : null);
		obj.setCompanyCode(arr[180] != null ? arr[180].toString() : null);
		obj.setSourceIdentifier(arr[181] != null ? arr[181].toString() : null);
		obj.setSourceFileName(arr[182] != null ? arr[182].toString() : null);
		obj.setPlantCode(arr[183] != null ? arr[183].toString() : null);
		obj.setDivision(arr[184] != null ? arr[184].toString() : null);
		obj.setSubDivision(arr[185] != null ? arr[185].toString() : null);
		obj.setLocation(arr[186] != null ? arr[186].toString() : null);
		obj.setPurchaseOrganisation(
				arr[187] != null ? arr[187].toString() : null);
		obj.setProfitCentre1(arr[188] != null ? arr[188].toString() : null);
		obj.setProfitCentre2(arr[189] != null ? arr[189].toString() : null);
		obj.setProfitCentre3(arr[190] != null ? arr[190].toString() : null);
		obj.setProfitCentre4(arr[191] != null ? arr[191].toString() : null);
		obj.setProfitCentre5(arr[192] != null ? arr[192].toString() : null);
		obj.setProfitCentre6(arr[193] != null ? arr[193].toString() : null);
		obj.setProfitCentre7(arr[194] != null ? arr[194].toString() : null);
		obj.setProfitCentre8(arr[195] != null ? arr[195].toString() : null);
		obj.setGlAssessableValue(arr[196] != null ? arr[196].toString() : null);
		obj.setGlIGST(arr[197] != null ? arr[197].toString() : null);
		obj.setGlCGST(arr[198] != null ? arr[198].toString() : null);
		obj.setGlSGST(arr[199] != null ? arr[199].toString() : null);
		obj.setGlAdvaloremCess(arr[200] != null ? arr[200].toString() : null);
		obj.setGlSpecificCess(arr[201] != null ? arr[201].toString() : null);
		obj.setgLStateCessAdvalorem(
				arr[202] != null ? arr[202].toString() : null);
		obj.setgLStateCessSpecific(
				arr[203] != null ? arr[203].toString() : null);
		if (arr[204] != null) {
			String strdate = arr[204].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setGlPostingDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newDate));
		} else {
			obj.setGlPostingDate(null);
		}
		obj.setPurchaseOrderValue(
				arr[205] != null ? arr[205].toString() : null);
		obj.seteWBNumber(arr[206] != null ? arr[206].toString() : null);
		obj.seteWBDate(arr[207] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[207].toString()) : null);
		obj.setAccountingVoucherNumber(
				arr[208] != null ? arr[208].toString() : null);
		obj.setAccountingVoucherDate(
				arr[209] != null ? arr[209].toString() : null);
		obj.setDocumentReferenceNumber(
				arr[210] != null ? arr[210].toString() : null);
		obj.setUserDefField1(arr[211] != null ? arr[211].toString() : null);
		obj.setUserDefField2(arr[212] != null ? arr[212].toString() : null);
		obj.setUserDefField3(arr[213] != null ? arr[213].toString() : null);
		obj.setUserDefField4(arr[214] != null ? arr[214].toString() : null);
		obj.setUserDefField5(arr[215] != null ? arr[215].toString() : null);
		obj.setUserDefField6(arr[216] != null ? arr[216].toString() : null);
		obj.setUserDefField7(arr[217] != null ? arr[217].toString() : null);
		obj.setUserDefField8(arr[218] != null ? arr[218].toString() : null);
		obj.setUserDefField9(arr[219] != null ? arr[219].toString() : null);
		obj.setUserDefField10(arr[220] != null ? arr[220].toString() : null);
		obj.setUserDefField11(arr[221] != null ? arr[221].toString() : null);
		obj.setUserDefField12(arr[222] != null ? arr[222].toString() : null);
		obj.setUserDefField13(arr[223] != null ? arr[223].toString() : null);
		obj.setUserDefField14(arr[224] != null ? arr[224].toString() : null);
		obj.setUserDefField15(arr[225] != null ? arr[225].toString() : null);
		obj.setUserDefField16(arr[226] != null ? arr[226].toString() : null);
		obj.setUserDefField17(arr[227] != null ? arr[227].toString() : null);
		obj.setUserDefField18(arr[228] != null ? arr[228].toString() : null);
		obj.setUserDefField19(arr[229] != null ? arr[229].toString() : null);
		obj.setUserDefField20(arr[230] != null ? arr[230].toString() : null);
		obj.setUserDefField21(arr[231] != null ? arr[231].toString() : null);
		obj.setUserDefField22(arr[232] != null ? arr[232].toString() : null);
		obj.setUserDefField23(arr[233] != null ? arr[233].toString() : null);
		obj.setUserDefField24(arr[234] != null ? arr[234].toString() : null);
		obj.setUserDefField25(arr[235] != null ? arr[235].toString() : null);
		obj.setUserDefField26(arr[236] != null ? arr[236].toString() : null);
		obj.setUserDefField27(arr[237] != null ? arr[237].toString() : null);
		obj.setUserDefField28(arr[238] != null ? arr[238].toString() : null);
		obj.setUserDefField29(arr[239] != null ? arr[239].toString() : null);
		obj.setUserDefField30(arr[240] != null ? arr[240].toString() : null);
		obj.setItcAvailableIGST(arr[241] != null ? arr[241].toString() : null);
		obj.setItcAvailableCGST(arr[242] != null ? arr[242].toString() : null);
		obj.setItcAvailableSGST(arr[243] != null ? arr[243].toString() : null);
		obj.setItcAvailableCess(arr[244] != null ? arr[244].toString() : null);

		BigDecimal bigDecimalRatio1IGST = (BigDecimal) arr[245];
		if (bigDecimalRatio1IGST != null) {
			obj.setRatio1IGST(
					bigDecimalRatio1IGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio1CGST = (BigDecimal) arr[246];
		if (bigDecimalRatio1CGST != null) {
			obj.setRatio1CGST(
					bigDecimalRatio1CGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio1SGST = (BigDecimal) arr[247];
		if (bigDecimalRatio1SGST != null) {
			obj.setRatio1SGST(
					bigDecimalRatio1SGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		BigDecimal bigDecimalRatio1CESS = (BigDecimal) arr[248];
		if (bigDecimalRatio1CESS != null) {
			obj.setRatio1Cess(
					bigDecimalRatio1CESS.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio2IGST = (BigDecimal) arr[249];
		if (bigDecimalRatio2IGST != null) {
			obj.setRatio2IGST(
					bigDecimalRatio2IGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio2CGST = (BigDecimal) arr[250];
		if (bigDecimalRatio2CGST != null) {
			obj.setRatio2CGST(
					bigDecimalRatio2CGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio2SGST = (BigDecimal) arr[251];
		if (bigDecimalRatio2SGST != null) {
			obj.setRatio2SGST(
					bigDecimalRatio2SGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio2CESS = (BigDecimal) arr[252];
		if (bigDecimalRatio2CESS != null) {
			obj.setRatio2Cess(
					bigDecimalRatio2CESS.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio3IGST = (BigDecimal) arr[253];
		if (bigDecimalRatio3IGST != null) {
			obj.setRatio3IGST(
					bigDecimalRatio3IGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio3CGST = (BigDecimal) arr[254];
		if (bigDecimalRatio3CGST != null) {
			obj.setRatio3CGST(
					bigDecimalRatio3CGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		BigDecimal bigDecimalRatio3SGST = (BigDecimal) arr[255];
		if (bigDecimalRatio3SGST != null) {
			obj.setRatio3SGST(
					bigDecimalRatio3SGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio3CESS = (BigDecimal) arr[256];
		if (bigDecimalRatio3CESS != null) {
			obj.setRatio3Cess(
					bigDecimalRatio3CESS.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		
		obj.setCommonSupplyIndicatorUpdated(arr[257] != null ? arr[257].toString() : null);
		obj.setUpdatedCommonSupplyIndicator(arr[258] != null ? arr[258].toString() : null);
		obj.setOriginalCommonSupplyIndicator(arr[259] != null ? arr[259].toString() : null);
		obj.setReturnPeriodUpdated(arr[260] != null ? arr[260].toString() : null);
		obj.setUpdatedReturnPeriod(arr[261] != null ? arr[261].toString() : null);
		obj.setOriginalReturnPeriod(arr[262] != null ? arr[262].toString() : null);

		return obj;
	}
}

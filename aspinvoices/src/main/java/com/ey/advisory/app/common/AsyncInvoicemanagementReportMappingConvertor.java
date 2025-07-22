/**
 * 
 */
package com.ey.advisory.app.common;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.einvoice.EinvoiceMangementResponseDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ErrorMasterUtil;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */

@Slf4j
@Component("AsyncInvoicemanagementReportMappingConvertor")
public class AsyncInvoicemanagementReportMappingConvertor
		implements AsyncInvMangmntReportConvertor {

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";
	private static final String FARMATTER = "yyyy-MM-dd HH:mm:ss";

	@Override
	public Object convert(Object[] arr) {

		EinvoiceMangementResponseDto obj = new EinvoiceMangementResponseDto();

		String errDesc = null;
		String infoDesc = null;

		String errCode = (arr[0] != null) ? arr[0].toString() : null;

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errCodeList.replaceAll(String::trim);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "OUTWARD");
		}

		obj.setAspErrorDesc(errDesc);

		String infoCode = (arr[2] != null) ? arr[2].toString() : null;

		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoCodeList.replaceAll(String::trim);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "OUTWARD");
		}

		obj.setAspInformationDesc(infoDesc);
		obj.setIrnStatus(arr[4] != null ? arr[4].toString() : null);
		obj.setIrnNo(arr[5] != null ? arr[5].toString() : null);
		obj.setIrnAcknowledgmentNo(arr[6] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		if (arr[7] != null) {

			LOGGER.debug("Irn Ack Date :" + arr[7].toString());

			Timestamp timeStamp = (Timestamp) arr[7];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			String[] dateTime = newdate.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];

			obj.setIrnAcknowledgmentDate(date);
			obj.setIrnAcknowledgmentTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}
		obj.setSignedQRCode(arr[8] != null ? arr[8].toString() : null);
		obj.setSignedInvoice(arr[9] != null ? arr[9].toString() : null);
		obj.setIrpErrorCode(arr[10] != null ? arr[10].toString() : null);
		obj.setIrpErrorDescription(arr[11] != null ? arr[11].toString() : null);
		obj.setEwbStatus(arr[12] != null ? arr[12].toString() : null);
		// obj.setEwbValidupto(arr[13] != null ? arr[13].toString() : null);
		if (arr[13] != null) {
			LOGGER.debug("validUpto :" + arr[13].toString());

			Timestamp timeStamp = (Timestamp) arr[13];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			String[] dateTime = newdate.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];

			obj.setEwbValidupto(date);
			obj.setEwbValiduptoTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}
		obj.setEwbNo(arr[296] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[296].toString()) : null);

		if (arr[297] != null) {

			LOGGER.debug("EwbRespDate :" + arr[297].toString());

			Timestamp timeStamp = (Timestamp) arr[297];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			String[] dateTime = newdate.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];
			obj.setEwbRespDate(date);
			obj.setEwbTime(DownloadReportsConstant.CSVCHARACTER.concat(time));

		}
		obj.setEwbErrorCode(arr[16] != null ? arr[16].toString() : null);
		obj.setEwbErrorDescription(arr[17] != null ? arr[17].toString() : null);
		obj.setGstnStatus(arr[18] != null ? arr[18].toString() : null);
		obj.setGstnRefid(arr[19] != null ? arr[19].toString() : null);
		// obj.setGstnRefidDate(arr[20] != null ? arr[20].toString() : null);

		if (arr[20] != null) {

			LOGGER.debug("GstnRefidTime :" + arr[20].toString());

			Timestamp timeStamp = (Timestamp) arr[20];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			String[] dateTime = newdate.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];

			obj.setGstnRefidDate(date);
			obj.setGstnRefidTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}
		// obj.setGstnRefidTime(arr[20] != null ? arr[20].toString() : null);
		obj.setGstnErrorCode(arr[21] != null ? arr[21].toString() : null);
		obj.setGstnErrorDescription(
				arr[22] != null ? arr[22].toString() : null);
		obj.setReturnType(arr[23] != null ? arr[23].toString() : null);
		obj.setTableNumber(arr[24] != null ? arr[24].toString() : null);
		obj.setIrn(arr[25] != null ? arr[25].toString() : null);
		obj.setIrnDate(arr[26] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[26].toString()) : null);
		obj.setTaxScheme(arr[27] != null ? arr[27].toString() : null);
		obj.setCancellationReason(arr[28] != null ? arr[28].toString() : null);
		obj.setCancellationRemarks(arr[29] != null ? arr[29].toString() : null);
		String supplyType = arr[30] != null ? arr[30].toString() : null;
		if (!Strings.isNullOrEmpty(supplyType)) {
			obj.setSupplyType(supplyType.toUpperCase());
		}
		obj.setDocCategory(arr[31] != null ? arr[31].toString() : null);
		obj.setDocumentType(arr[32] != null ? arr[32].toString() : null);
		obj.setDocumentNumber(
				arr[33] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[33].toString()) : null);

		if (arr[34] != null) {
			String strdate = arr[34].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {
			obj.setDocumentDate(null);
		}
		obj.setReverseChargeFlag(arr[35] != null ? arr[35].toString() : null);
		obj.setSupplierGSTIN(arr[36] != null ? arr[36].toString() : null);
		obj.setSupplierTradeName(arr[37] != null ? arr[37].toString() : null);
		obj.setSupplierLegalName(arr[38] != null ? arr[38].toString() : null);
		obj.setSupplierAddress1(arr[39] != null ? arr[39].toString() : null);
		obj.setSupplierAddress2(arr[40] != null ? arr[40].toString() : null);
		obj.setSupplierLocation(
				arr[41] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[41].toString()) : null);
		obj.setSupplierPincode(arr[42] != null ? arr[42].toString() : null);
		obj.setSupplierStateCode(
				arr[43] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[43].toString()) : null);
		obj.setSupplierPhone(arr[44] != null ? arr[44].toString() : null);
		obj.setSupplierEmail(arr[45] != null ? arr[45].toString() : null);
		obj.setCustomerGSTIN(arr[46] != null ? arr[46].toString() : null);
		obj.setCustomerTradeName(arr[47] != null ? arr[47].toString() : null);
		obj.setCustomerLegalName(arr[48] != null ? arr[48].toString() : null);
		obj.setCustomerAddress1(arr[49] != null ? arr[49].toString() : null);
		obj.setCustomerAddress2(arr[50] != null ? arr[50].toString() : null);
		obj.setCustomerLocation(
				arr[51] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[51].toString()) : null);
		obj.setCustomerPincode(arr[52] != null ? arr[52].toString() : null);
		obj.setCustomerStateCode(
				arr[53] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[53].toString()) : null);
		obj.setBillingPOS(arr[54] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[54].toString()) : null);
		obj.setCustomerPhone(arr[55] != null ? arr[55].toString() : null);
		obj.setCustomerEmail(arr[56] != null ? arr[56].toString() : null);
		obj.setDispatcherGSTIN(arr[57] != null ? arr[57].toString() : null);
		obj.setDispatcherTradeName(arr[58] != null ? arr[58].toString() : null);
		obj.setDispatcherAddress1(arr[59] != null ? arr[59].toString() : null);
		obj.setDispatcherAddress2(arr[60] != null ? arr[60].toString() : null);
		obj.setDispatcherLocation(
				arr[61] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[61].toString()) : null);
		obj.setDispatcherPincode(arr[62] != null ? arr[62].toString() : null);
		obj.setDispatcherStateCode(
				arr[63] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[63].toString()) : null);
		obj.setShipToGSTIN(arr[64] != null ? arr[64].toString() : null);
		obj.setShipToTradeName(arr[65] != null ? arr[65].toString() : null);
		obj.setShipToLegalName(arr[66] != null ? arr[66].toString() : null);
		obj.setShipToAddress1(arr[67] != null ? arr[67].toString() : null);
		obj.setShipToAddress2(arr[68] != null ? arr[68].toString() : null);
		obj.setShipToLocation(
				arr[69] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[69].toString()) : null);
		obj.setShipToPincode(arr[70] != null ? arr[70].toString() : null);

		obj.setShipToStateCode(
				arr[71] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[71].toString()) : null);

		/*
		 * obj.setShipToStateCode( arr[301] != null ?
		 * DownloadReportsConstant.CSVCHARACTER .concat(arr[301].toString()) :
		 * null);
		 */

		obj.setItemSerialNumber(arr[72] != null ? arr[72].toString() : null);
		obj.setProductSerialNumber(
				arr[73] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[73].toString()) : null);
		obj.setProductName(arr[74] != null ? arr[74].toString() : null);
		obj.setProductDescription(arr[75] != null ? arr[75].toString() : null);
		obj.setIsService(arr[76] != null ? arr[76].toString() : null);
		// obj.setHsn(arr[77] != null ? arr[77].toString() : null);
		obj.setHsn(arr[77] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[77].toString()) : null);
		obj.setBarcode(arr[78] != null ? arr[78].toString() : null);
		obj.setBatchName(arr[79] != null ? arr[79].toString() : null);
		if (arr[80] != null) {
			String strdate = arr[80].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBatchExpiryDate(newDate);
		} else {
			obj.setBatchExpiryDate(null);
		}
		if (arr[81] != null) {
			String strdate = arr[81].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setWarrantyDate(newDate);
		} else {
			obj.setWarrantyDate(null);
		}
		obj.setOrderlineReference(arr[82] != null ? arr[82].toString() : null);
		obj.setAttributeName(arr[83] != null ? arr[83].toString() : null);
		obj.setAttributeValue(
				arr[84] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[84].toString()) : null);
		obj.setOriginCountry(arr[85] != null ? arr[85].toString() : null);
		obj.setFreeQuantity(arr[88] != null ? arr[88].toString() : null);
		obj.setUnitPrice(arr[89] != null ? arr[89].toString() : null);
		obj.setItemAmount(arr[90] != null ? arr[90].toString() : null);
		obj.setItemDiscount(arr[91] != null ? arr[91].toString() : null);
		obj.setPreTaxAmount(arr[92] != null ? arr[92].toString() : null);
		obj.setItemAssessableAmount(
				arr[93] != null ? arr[93].toString() : null);
		obj.setiGSTRate(arr[94] != null ? arr[94].toString() : null);
		obj.setiGSTAmount(arr[95] != null ? arr[95].toString() : null);
		obj.setcGSTRate(arr[96] != null ? arr[96].toString() : null);
		obj.setcGSTAmount(arr[97] != null ? arr[97].toString() : null);
		obj.setsGSTRate(arr[98] != null ? arr[98].toString() : null);
		obj.setsGSTAmount(arr[99] != null ? arr[99].toString() : null);
		obj.setCessAdvaloremRate(arr[100] != null ? arr[100].toString() : null);
		obj.setCessAdvaloremAmount(
				arr[101] != null ? arr[101].toString() : null);
		obj.setCessSpecificRate(arr[102] != null ? arr[102].toString() : null);
		obj.setCessSpecificAmount(
				arr[103] != null ? arr[103].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[104] != null ? arr[104].toString() : null);
		obj.setStateCessAdvaloremAmount(
				arr[105] != null ? arr[105].toString() : null);
		obj.setStateCessSpecificRate(
				arr[106] != null ? arr[106].toString() : null);
		obj.setStateCessSpecificAmount(
				arr[107] != null ? arr[107].toString() : null);
		obj.setItemOtherCharges(arr[108] != null ? arr[108].toString() : null);
		obj.setTotalItemAmount(arr[109] != null ? arr[109].toString() : null);
		obj.setInvoiceOtherCharges(
				arr[110] != null ? arr[110].toString() : null);
		obj.setInvoiceAssessableAmount(
				arr[111] != null ? arr[111].toString() : null);
		obj.setInvoiceIGSTAmount(arr[112] != null ? arr[112].toString() : null);
		obj.setInvoiceCGSTAmount(arr[113] != null ? arr[113].toString() : null);
		obj.setInvoiceSGSTAmount(arr[114] != null ? arr[114].toString() : null);
		obj.setInvoiceCessAdvaloremAmount(
				arr[115] != null ? arr[115].toString() : null);
		obj.setInvoiceCessSpecificAmount(
				arr[116] != null ? arr[116].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmount(
				arr[117] != null ? arr[117].toString() : null);
		obj.setInvoiceStateCessSpecificAmount(
				arr[118] != null ? arr[118].toString() : null);
		obj.setInvoiceValue(arr[119] != null ? arr[119].toString() : null);
		obj.setRoundOff(arr[120] != null ? arr[120].toString() : null);
		obj.setTotalInvoiceValue(arr[121] != null ? arr[121].toString() : null);
		obj.settCSFlagIncomeTax(arr[122] != null ? arr[122].toString() : null);
		obj.settCSRateIncomeTax(arr[123] != null ? arr[123].toString() : null);
		obj.settCSAmountIncomeTax(
				arr[124] != null ? arr[124].toString() : null);
		obj.setCustomerPANOrAadhaar(
				arr[125] != null ? arr[125].toString() : null);
		obj.setCurrencyCode(arr[126] != null ? arr[126].toString() : null);
		obj.setCountryCode(arr[127] != null ? arr[127].toString() : null);
		obj.setInvoiceValueFC(arr[128] != null ? arr[128].toString() : null);
		obj.setPortCode(arr[129] != null ? arr[129].toString() : null);
		obj.setShippingBillNumber(
				arr[130] != null ? arr[130].toString() : null);
		if (arr[131] != null) {
			String strdate = arr[131].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setShippingBillDate(newDate);
		} else {
			obj.setShippingBillDate(null);
		}
		obj.setInvoiceRemarks(arr[132] != null ? arr[132].toString() : null);
		if (arr[133] != null) {
			String strdate = arr[133].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodStartDate(newDate);
		} else {
			obj.setInvoicePeriodStartDate(null);
		}

		if (arr[134] != null) {
			String strdate = arr[134].toString();
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
				arr[135] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[135].toString()) : null);
		if (arr[136] != null) {
			String strdate = arr[136].toString();
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
				arr[137] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[137].toString()) : null);
		obj.setReceiptAdviceReference(
				arr[138] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[138].toString()) : null);
		obj.setReceiptAdviceDate(arr[139] != null ? arr[139].toString() : null);
		obj.setTenderReference(
				arr[140] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[140].toString()) : null);
		obj.setContractReference(
				arr[141] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[141].toString()) : null);
		obj.setExternalReference(
				arr[142] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[142].toString()) : null);
		obj.setProjectReference(
				arr[143] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[143].toString()) : null);
		obj.setCustomerPOReferenceNumber(
				arr[144] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[144].toString()) : null);
		obj.setCustomerPOReferenceDate(
				arr[145] != null ? arr[145].toString() : null);
		obj.setPayeeName(arr[146] != null ? arr[146].toString() : null);
		obj.setModeOfPayment(arr[147] != null ? arr[147].toString() : null);
		obj.setBranchOrIFSCCode(arr[148] != null ? arr[148].toString() : null);
		obj.setPaymentTerms(arr[149] != null ? arr[149].toString() : null);
		obj.setPaymentInstruction(
				arr[150] != null ? arr[150].toString() : null);
		obj.setCreditTransfer(arr[151] != null ? arr[151].toString() : null);
		obj.setDirectDebit(arr[152] != null ? arr[152].toString() : null);
		obj.setCreditDays(arr[153] != null ? arr[153].toString() : null);
		obj.setPaidAmount(arr[154] != null ? arr[154].toString() : null);
		obj.setBalanceAmount(arr[155] != null ? arr[155].toString() : null);
		if (arr[156] != null) {
			String strdate = arr[156].toString();
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
				arr[157] != null ? "'" + arr[157].toString() : null);
		// Object accountdetail =
		// CommonUtility.exponentialAndZeroCheck(arr[157]);
		// obj.setAccountDetail(
		// accountdetail != null ? accountdetail.toString() : null);
		obj.setEcomGSTIN(arr[158] != null ? arr[158].toString() : null);
		obj.setEcomTransactionID(arr[159] != null ? arr[159].toString() : null);
		obj.setSupportingDocURL(arr[160] != null ? arr[160].toString() : null);
		obj.setSupportingDocument(
				arr[161] != null ? arr[161].toString() : null);
		obj.setAdditionalInformation(
				arr[162] != null ? arr[162].toString() : null);
		obj.setTransactionType(arr[163] != null ? arr[163].toString() : null);
		obj.setSubSupplyType(arr[164] != null ? arr[164].toString() : null);
		obj.setOtherSupplyTypeDescription(
				arr[165] != null ? arr[165].toString() : null);
		obj.setTransporterID(arr[166] != null ? arr[166].toString() : null);
		obj.setTransporterName(arr[167] != null ? arr[167].toString() : null);
		obj.setTransportMode(arr[168] != null ? arr[168].toString() : null);
		obj.setTransportDocNo(arr[169] != null ? arr[169].toString() : null);
		if (arr[170] != null) {
			String strdate = arr[170].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setTransportDocDate(newDate);
		} else {
			obj.setTransportDocDate(null);
		}
		obj.setDistance(arr[171] != null ? arr[171].toString() : null);
		obj.setVehicleNo(arr[172] != null ? arr[172].toString() : null);
		obj.setVehicleType(arr[173] != null ? arr[173].toString() : null);
		obj.setReturnPeriod(
				arr[174] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[174].toString()) : null);
		obj.setOriginalDocumentType(
				arr[175] != null ? arr[175].toString() : null);
		obj.setOriginalCustomerGSTIN(
				arr[176] != null ? arr[176].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[177] != null ? arr[177].toString() : null);
		obj.setSec7ofIGSTFlag(arr[178] != null ? arr[178].toString() : null);
		obj.setClaimRefndFlag(arr[179] != null ? arr[179].toString() : null);
		obj.setAutoPopltToRefund(arr[180] != null ? arr[180].toString() : null);
		obj.setcRDRPreGST(arr[181] != null ? arr[181].toString() : null);
		obj.setCustomerType(arr[182] != null ? arr[182].toString() : null);
		obj.setCustomerCode(arr[183] != null ? arr[183].toString() : null);
		obj.setProductCode(arr[184] != null ? arr[184].toString() : null);
		obj.setCategoryOfProduct(arr[185] != null ? arr[185].toString() : null);
		obj.setiTCFlag(arr[186] != null ? arr[186].toString() : null);
		obj.setStateApplyingCess(arr[187] != null ? arr[187].toString() : null);
		obj.setfOB(arr[188] != null ? arr[188].toString() : null);
		obj.setExportDuty(arr[189] != null ? arr[189].toString() : null);
		obj.setExchangeRate(arr[190] != null ? arr[190].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[191] != null ? arr[191].toString() : null);
		obj.settCSFlagGST(arr[192] != null ? arr[192].toString() : null);
		obj.settCSIGSTAmount(arr[193] != null ? arr[193].toString() : null);
		obj.settCSCGSTAmount(arr[194] != null ? arr[194].toString() : null);
		obj.settCSSGSTAmount(arr[195] != null ? arr[195].toString() : null);
		obj.settDSFlagGST(arr[196] != null ? arr[196].toString() : null);
		obj.settDSIGSTAmount(arr[197] != null ? arr[197].toString() : null);
		obj.settDSCGSTAmount(arr[198] != null ? arr[198].toString() : null);
		obj.settDSSGSTAmount(arr[199] != null ? arr[199].toString() : null);
		obj.setUserId(arr[200] != null ? arr[200].toString() : null);
		obj.setCompanyCode(arr[201] != null ? arr[201].toString() : null);
		obj.setSourceIdentifier(arr[202] != null ? arr[202].toString() : null);
		obj.setSourceFileName(arr[203] != null ? arr[203].toString() : null);
		obj.setPlantCode(arr[204] != null ? arr[204].toString() : null);
		obj.setDivision(arr[205] != null ? arr[205].toString() : null);
		obj.setSubDivision(arr[206] != null ? arr[206].toString() : null);
		obj.setLocation(arr[207] != null ? arr[207].toString() : null);
		obj.setSalesOrganisation(arr[208] != null ? arr[208].toString() : null);
		obj.setDistributionChannel(
				arr[209] != null ? arr[209].toString() : null);
		obj.setProfitCentre1(arr[210] != null ? arr[210].toString() : null);
		obj.setProfitCentre2(arr[211] != null ? arr[211].toString() : null);
		obj.setProfitCentre3(arr[212] != null ? arr[212].toString() : null);
		obj.setProfitCentre4(arr[213] != null ? arr[213].toString() : null);
		obj.setProfitCentre5(arr[214] != null ? arr[214].toString() : null);
		obj.setProfitCentre6(arr[215] != null ? arr[215].toString() : null);
		obj.setProfitCentre7(arr[216] != null ? arr[216].toString() : null);
		obj.setProfitCentre8(arr[217] != null ? arr[217].toString() : null);
		obj.setGlAssessableValue(arr[218] != null ? arr[218].toString() : null);
		obj.setGlIGST(arr[219] != null ? arr[219].toString() : null);
		obj.setGlCGST(arr[220] != null ? arr[220].toString() : null);
		obj.setGlSGST(arr[221] != null ? arr[221].toString() : null);
		obj.setGlAdvaloremCess(arr[222] != null ? arr[222].toString() : null);
		obj.setGlSpecificCess(arr[223] != null ? arr[223].toString() : null);
		obj.setgLStateCessAdvalorem(
				arr[224] != null ? arr[224].toString() : null);
		obj.setgLStateCessSpecific(
				arr[225] != null ? arr[225].toString() : null);
		if (arr[226] != null) {
			String strdate = arr[226].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setGlPostingDate(newDate);
		} else {
			obj.setGlPostingDate(null);
		}
		obj.setSalesOrderNumber(arr[227] != null ? arr[227].toString() : null);
		obj.seteWBNumber(arr[14] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[14].toString()) : null);
		obj.setEWBDateStr(arr[15] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[15].toString()) : null);
		obj.setAccountingVoucherNumber(
				arr[230] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[230].toString()) : null);
		if (arr[231] != null) {
			String strdate = arr[231].toString();
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
				arr[232] != null ? arr[232].toString() : null);
		obj.setCustomerTAN(arr[233] != null ? arr[233].toString() : null);
		obj.setUserDefField1(arr[234] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[234].toString()) : null);
		obj.setUserDefField2(arr[235] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[235].toString()) : null);
		obj.setUserDefField3(arr[236] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[236].toString()) : null);
		obj.setUserDefField4(arr[237] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[237].toString()) : null);
		obj.setUserDefField5(arr[238] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[238].toString()) : null);
		obj.setUserDefField6(arr[239] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[239].toString()) : null);
		obj.setUserDefField7(arr[240] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[240].toString()) : null);
		obj.setUserDefField8(arr[241] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[241].toString()) : null);
		obj.setUserDefField9(arr[242] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[242].toString()) : null);
		obj.setUserDefField10(arr[243] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[243].toString()) : null);
		obj.setUserDefField11(arr[244] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[244].toString()) : null);
		obj.setUserDefField12(arr[245] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[245].toString()) : null);
		obj.setUserDefField13(arr[246] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[246].toString()) : null);
		obj.setUserDefField14(arr[247] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[247].toString()) : null);
		obj.setUserDefField15(arr[248] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[248].toString()) : null);
		obj.setUserDefField16(arr[249] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[249].toString()) : null);
		obj.setUserDefField17(arr[250] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[250].toString()) : null);
		obj.setUserDefField18(arr[251] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[251].toString()) : null);
		obj.setUserDefField19(arr[252] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[252].toString()) : null);
		obj.setUserDefField20(arr[253] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[253].toString()) : null);
		obj.setUserDefField21(arr[254] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[254].toString()) : null);
		obj.setUserDefField22(arr[255] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[255].toString()) : null);
		obj.setUserDefField23(arr[256] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[256].toString()) : null);
		obj.setUserDefField24(arr[257] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[257].toString()) : null);
		obj.setUserDefField25(arr[258] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[258].toString()) : null);
		obj.setUserDefField26(arr[259] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[259].toString()) : null);
		obj.setUserDefField27(arr[260] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[260].toString()) : null);
		obj.setUserDefField28(arr[261] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[261].toString()) : null);
		obj.setUserDefField29(arr[262] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[262].toString()) : null);
		obj.setUserDefField30(arr[263] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[263].toString()) : null);
		obj.setSupplyTypeASP(arr[264] != null ? arr[264].toString() : null);
		obj.setApproximateDistanceASP(
				arr[265] != null ? arr[265].toString() : null);
		obj.setDistanceSavedtoEWB(
				arr[266] != null ? arr[266].toString() : null);
		obj.setUserID(arr[267] != null ? arr[267].toString() : null);
		obj.setFileID(arr[268] != null ? arr[268].toString() : null);
		obj.setFileName(arr[269] != null ? arr[269].toString() : null);
		obj.setInvoiceOtherChargesASP(
				arr[270] != null ? arr[270].toString() : null);
		obj.setInvoiceAssessableAmountASP(
				arr[271] != null ? arr[271].toString() : null);
		obj.setInvoiceIGSTAmountASP(
				arr[272] != null ? arr[272].toString() : null);
		obj.setInvoiceCGSTAmountASP(
				arr[273] != null ? arr[273].toString() : null);
		obj.setInvoiceSGSTAmountASP(
				arr[274] != null ? arr[274].toString() : null);
		obj.setInvoiceCessAdvaloremAmountASP(
				arr[275] != null ? arr[275].toString() : null);
		obj.setInvoiceCessSpecificAmountASP(
				arr[276] != null ? arr[276].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmountASP(
				arr[277] != null ? arr[277].toString() : null);
		obj.setInvoiceStateCessSpecificAmountASP(
				arr[278] != null ? arr[278].toString() : null);
		obj.setInvoiceValueASP(arr[279] != null ? arr[279].toString() : null);
		obj.setIntegratedTaxAmountASP(
				arr[280] != null ? arr[280].toString() : null);
		obj.setCentralTaxAmountASP(
				arr[281] != null ? arr[281].toString() : null);
		obj.setStateUTTaxAmountASP(
				arr[282] != null ? arr[282].toString() : null);
		obj.setCessAdvaloremAmountASP(
				arr[283] != null ? arr[283].toString() : null);
		obj.setStateCessAdvaloremAmountASP(
				arr[284] != null ? arr[284].toString() : null);
		obj.setIntegratedTaxAmountRET1Impact(
				arr[285] != null ? arr[285].toString() : null);
		obj.setCentralTaxAmountRET1Impact(
				arr[286] != null ? arr[286].toString() : null);
		obj.setStateUTTaxAmountRET1Impact(
				arr[287] != null ? arr[287].toString() : null);
		obj.setCessAdvaloremAmountDifference(
				arr[288] != null ? arr[288].toString() : null);
		obj.setStateCessAdvaloremAmountDifference(
				arr[289] != null ? arr[289].toString() : null);
		obj.setRecordStatus(arr[290] != null ? arr[290].toString() : null);

		if (arr[291] != null) {

			LOGGER.debug("Irn Cancellation Date :" + arr[291].toString());

			Timestamp timeStamp = (Timestamp) arr[291];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			String[] dateTime = newdate.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];

			obj.setIrnCancellationDate(date);
			obj.setIrnCancellationTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		if (arr[292] != null) {

			LOGGER.debug("EWB Cancellation Date :" + arr[292].toString());

			Timestamp timeStamp = (Timestamp) arr[292];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			String[] dateTime = newdate.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];

			obj.seteWBCancellationDate(date);
			obj.seteWBCancellationTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}
		obj.setDigiGSTStatus(arr[293] != null ? arr[293].toString() : null);
		obj.setInfoErrorCode(arr[294] != null ? arr[294].toString() : null);
		obj.setInfoErrorMsg(arr[295] != null ? arr[295].toString() : null);

		String itemUqc = arr[298] != null ? arr[298].toString() : null; 
		String itemQty = arr[299] != null ? arr[299].toString() : null; 

		String itemUqcUser = arr[86] != null ? arr[86].toString() : null; 
		String itemQtyUser = arr[87] != null ? arr[87].toString() : null; 

		String uqc = itemUqcUser != null ? itemUqcUser : itemUqc;
		String qty = itemQtyUser != null ? itemQtyUser : itemQty;

		obj.setuQC(uqc != null ? uqc : null);
		obj.setQuantity(qty != null ? qty : null);
		
		//To Exclude below column E-Invoice Response Download Report and Invoice Management Records Report
		//As Part of this User Story 80008: Update of Missing Shipping Bill details Report in case details are updated via amendments
		if (arr.length > 313) {
		    obj.setAmendmentStatus(arr[313] != null ? arr[313].toString() : null);
		}
		//To Exclude below column E-Invoice Response Download Report and Invoice Management Records Report
		//As Part of this User Story 80008: Update of Missing Shipping Bill details Report in case details are updated via amendments
		if (arr.length > 314) {
		    obj.setAmendedReturnPeriod(arr[314] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[314].toString()) : null);
		}
		if (arr.length > 315) {
		    obj.setAmendedPortCode(arr[315] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[315].toString()) : null);
		}
		if (arr.length > 316) {
		    obj.setAmendedShipBillNo(arr[316] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[316].toString()) : null);
		}
		if (arr.length > 317) {
		    obj.setAmendedShipBillDate(arr[317] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[317].toString()) : null);
		}

		return obj;
	}
}
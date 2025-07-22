package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.ReportConvertor;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("AspProcessedAsUploadedReportConvertor")
public class AspProcessedAsUploadedReportConvertor implements ReportConvertor {

	static final String OLDFARMATTER = "yyyy-MM-dd";
	static final String NEWFARMATTER = "dd-MM-yyyy";

	static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss a");

	@Override
	public Object convert(Object[] arr, String reportType) {

		DataStatusEinvoiceDto obj = new DataStatusEinvoiceDto();
		String errDesc = null;
		String infoDesc = null;

		String errCode = (arr[0] != null) ? arr[0].toString() : null;

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errCodeList.replaceAll(String::trim);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "OUTWARD");
		}

		// obj.setAspErrorCode(errCode);
		obj.setAspErrorDesc(errDesc);

		String infoCode = (arr[2] != null) ? arr[2].toString() : null;

		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoCodeList.replaceAll(String::trim);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "OUTWARD");
		}

		obj.setAspInformationDesc(infoDesc);
		obj.setIrnStatus(arr[4] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		obj.setIrnNo(arr[5] != null ? arr[5].toString() : null);
		obj.setIrnAcknowledgmentNo(arr[6] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		if (arr[7] != null) {

			LOGGER.debug("Irn Ack Date :" + arr[7].toString());

			String timestamp = arr[7].toString();
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];

			obj.setIrnAcknowledgmentDate(date);
			obj.setIrnAcknowledgmentTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}
		obj.setSignedQRCode(arr[8] != null ? arr[8].toString() : null);
		// obj.setSignedInvoice(arr[9] != null ? arr[9].toString() : null);
		obj.setIrpErrorCode(arr[10] != null ? arr[10].toString() : null);
		obj.setIrpErrorDescription(arr[11] != null ? arr[11].toString() : null);
		obj.setEwbStatus(arr[12] != null ? arr[12].toString() : null);
		obj.setEwbValidupto(arr[13] != null ? arr[13].toString() : null);
		/*
		 * if (arr[13] != null) { LOGGER.debug("validUpto :" +
		 * arr[13].toString());
		 * 
		 * String timestamp = arr[13].toString(); String[] dateTime =
		 * timestamp.split(" ");
		 * 
		 * String date = dateTime[0]; String time = dateTime[1];
		 * 
		 * obj.setEwbValidupto(date); obj.setEwbValiduptoTime(
		 * DownloadReportsConstant.CSVCHARACTER.concat(time)); }
		 */

		String itnStatus = obj.getIrnStatus();

		if ("'GENERATED".equalsIgnoreCase(itnStatus)) {
			String timestamp = arr[7].toString();
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];
			String[] datesplit = date.split("-");
			String[] timesplit = time.split(":");
			LocalDateTime irnDate = LocalDateTime.of(
					Integer.parseInt(datesplit[2]),
					Integer.parseInt(datesplit[1]),
					Integer.parseInt(datesplit[0]),
					Integer.parseInt(timesplit[0]),
					Integer.parseInt(timesplit[1]),
					Integer.parseInt(timesplit[2]));

			if (CommonUtility.deriveEinvStatus(irnDate)) {

				obj.setIrnStatus(DownloadReportsConstant.CSVCHARACTER
						.concat("Generated – Available for Cancellation "));
			}

		}
		obj.setEwbNo(arr[296] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[296].toString()) : null);

		if (arr[297] != null) {

			LOGGER.debug("EwbRespDate :" + arr[297].toString());

			String timestamp = arr[297].toString();
			String[] dateTime = timestamp.split(" ");

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

			String timestamp = arr[20].toString();
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];
			obj.setGstnRefidDate(date);
			obj.setGstnRefidTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));

		}
		/*
		 * obj.setGstnRefidTime( arr[20] != null ? arr[20].toString() : null);
		 */
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
		obj.setSupplyType(arr[30] != null ? arr[30].toString() : null);
		obj.setDocCategory(arr[31] != null ? arr[31].toString() : null);
                String documentType = arr[32] != null ? arr[32].toString() : null;
                obj.setDocumentType(documentType);
                boolean isNegative = "CR".equalsIgnoreCase(documentType)
                                || "RCR".equalsIgnoreCase(documentType)
                                || "CDNR".equalsIgnoreCase(documentType)
                                || "CDNRA".equalsIgnoreCase(documentType)
                                || "CDNUR".equalsIgnoreCase(documentType)
                                || "CDNURA".equalsIgnoreCase(documentType);
		obj.setDocumentNumber(arr[33] != null ? arr[33].toString() : null);
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
		obj.setSupplierLocation(arr[41] != null ? arr[41].toString() : null);
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
		obj.setCustomerLocation(arr[51] != null ? arr[51].toString() : null);
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
		obj.setDispatcherLocation(arr[61] != null ? arr[61].toString() : null);
		obj.setDispatcherPincode(arr[62] != null ? arr[62].toString() : null);
		obj.setDispatcherStateCode(
				arr[63] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[63].toString()) : null);
		obj.setShipToGSTIN(arr[64] != null ? arr[64].toString() : null);
		obj.setShipToTradeName(arr[65] != null ? arr[65].toString() : null);
		obj.setShipToLegalName(arr[66] != null ? arr[66].toString() : null);
		obj.setShipToAddress1(arr[67] != null ? arr[67].toString() : null);
		obj.setShipToAddress2(arr[68] != null ? arr[68].toString() : null);
		obj.setShipToLocation(arr[69] != null ? arr[69].toString() : null);
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
		obj.setProductSerialNumber(arr[73] != null ? arr[73].toString() : null);
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
		obj.setAttributeValue(arr[84] != null ? arr[84].toString() : null);
		obj.setOriginCountry(arr[85] != null ? arr[85].toString() : null);
		/*
		 * obj.setuQC(arr[86] != null ? arr[86].toString() : null);
		 * obj.setQuantity(arr[87] != null ? arr[87].toString() : null);
		 */
		obj.setFreeQuantity(arr[88] != null ? arr[88].toString() : null);
                obj.setUnitPrice(arr[89] != null ? arr[89].toString() : null);
                obj.setItemAmount(convertAmountToNegative(arr[90] != null ? arr[90].toString() : null, isNegative));
                obj.setItemDiscount(arr[91] != null ? arr[91].toString() : null);
                obj.setPreTaxAmount(arr[92] != null ? arr[92].toString() : null);
                obj.setItemAssessableAmount(
                                convertAmountToNegative(arr[93] != null ? arr[93].toString() : null, isNegative));
		obj.setiGSTRate(arr[94] != null ? arr[94].toString() : null);
                obj.setiGSTAmount(convertAmountToNegative(arr[95] != null ? arr[95].toString() : null, isNegative));
		obj.setcGSTRate(arr[96] != null ? arr[96].toString() : null);
                obj.setcGSTAmount(convertAmountToNegative(arr[97] != null ? arr[97].toString() : null, isNegative));
		obj.setsGSTRate(arr[98] != null ? arr[98].toString() : null);
                obj.setsGSTAmount(convertAmountToNegative(arr[99] != null ? arr[99].toString() : null, isNegative));
		obj.setCessAdvaloremRate(arr[100] != null ? arr[100].toString() : null);
                obj.setCessAdvaloremAmount(
                                convertAmountToNegative(arr[101] != null ? arr[101].toString() : null, isNegative));
		obj.setCessSpecificRate(arr[102] != null ? arr[102].toString() : null);
                obj.setCessSpecificAmount(
                                convertAmountToNegative(arr[103] != null ? arr[103].toString() : null, isNegative));
		obj.setStateCessAdvaloremRate(
				arr[104] != null ? arr[104].toString() : null);
                obj.setStateCessAdvaloremAmount(
                                convertAmountToNegative(arr[105] != null ? arr[105].toString() : null, isNegative));
		obj.setStateCessSpecificRate(
				arr[106] != null ? arr[106].toString() : null);
                obj.setStateCessSpecificAmount(
                                convertAmountToNegative(arr[107] != null ? arr[107].toString() : null, isNegative));
		obj.setItemOtherCharges(arr[108] != null ? arr[108].toString() : null);
                obj.setTotalItemAmount(convertAmountToNegative(arr[109] != null ? arr[109].toString() : null, isNegative));
		obj.setInvoiceOtherCharges(
				arr[110] != null ? arr[110].toString() : null);
                obj.setInvoiceAssessableAmount(
                                convertAmountToNegative(arr[111] != null ? arr[111].toString() : null, isNegative));
                obj.setInvoiceIGSTAmount(convertAmountToNegative(arr[112] != null ? arr[112].toString() : null, isNegative));
                obj.setInvoiceCGSTAmount(convertAmountToNegative(arr[113] != null ? arr[113].toString() : null, isNegative));
                obj.setInvoiceSGSTAmount(convertAmountToNegative(arr[114] != null ? arr[114].toString() : null, isNegative));
                obj.setInvoiceCessAdvaloremAmount(
                                convertAmountToNegative(arr[115] != null ? arr[115].toString() : null, isNegative));
                obj.setInvoiceCessSpecificAmount(
                                convertAmountToNegative(arr[116] != null ? arr[116].toString() : null, isNegative));
                obj.setInvoiceStateCessAdvaloremAmount(
                                convertAmountToNegative(arr[117] != null ? arr[117].toString() : null, isNegative));
                obj.setInvoiceStateCessSpecificAmount(
                                convertAmountToNegative(arr[118] != null ? arr[118].toString() : null, isNegative));
                obj.setInvoiceValue(convertAmountToNegative(arr[119] != null ? arr[119].toString() : null, isNegative));
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
		obj.setOtherReference(arr[137] != null ? arr[137].toString() : null);
		obj.setReceiptAdviceReference(
				arr[138] != null ? arr[138].toString() : null);
		obj.setReceiptAdviceDate(arr[139] != null ? arr[139].toString() : null);
		obj.setTenderReference(arr[140] != null ? arr[140].toString() : null);
		obj.setContractReference(arr[141] != null ? arr[141].toString() : null);
		obj.setExternalReference(arr[142] != null ? arr[142].toString() : null);
		obj.setProjectReference(arr[143] != null ? arr[143].toString() : null);
		obj.setCustomerPOReferenceNumber(
				arr[144] != null ? arr[144].toString() : null);
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
		obj.seteWBDate(arr[15] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[15].toString()) : null);
		obj.setAccountingVoucherNumber(
				arr[230] != null ? arr[230].toString() : null);
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
		obj.setUserDefField1(arr[234] != null ? arr[234].toString() : null);
		obj.setUserDefField2(arr[235] != null ? arr[235].toString() : null);
		obj.setUserDefField3(arr[236] != null ? arr[236].toString() : null);
		obj.setUserDefField4(arr[237] != null ? arr[237].toString() : null);
		obj.setUserDefField5(arr[238] != null ? arr[238].toString() : null);
		obj.setUserDefField6(arr[239] != null ? arr[239].toString() : null);
		obj.setUserDefField7(arr[240] != null ? arr[240].toString() : null);
		obj.setUserDefField8(arr[241] != null ? arr[241].toString() : null);
		obj.setUserDefField9(arr[242] != null ? arr[242].toString() : null);
		obj.setUserDefField10(arr[243] != null ? arr[243].toString() : null);
		obj.setUserDefField11(arr[244] != null ? arr[244].toString() : null);
		obj.setUserDefField12(arr[245] != null ? arr[245].toString() : null);
		obj.setUserDefField13(arr[246] != null ? arr[246].toString() : null);
		obj.setUserDefField14(arr[247] != null ? arr[247].toString() : null);
		obj.setUserDefField15(arr[248] != null ? arr[248].toString() : null);
		obj.setUserDefField16(arr[249] != null ? arr[249].toString() : null);
		obj.setUserDefField17(arr[250] != null ? arr[250].toString() : null);
		obj.setUserDefField18(arr[251] != null ? arr[251].toString() : null);
		obj.setUserDefField19(arr[252] != null ? arr[252].toString() : null);
		obj.setUserDefField20(arr[253] != null ? arr[253].toString() : null);
		obj.setUserDefField21(arr[254] != null ? arr[254].toString() : null);
		obj.setUserDefField22(arr[255] != null ? arr[255].toString() : null);
		obj.setUserDefField23(arr[256] != null ? arr[256].toString() : null);
		obj.setUserDefField24(arr[257] != null ? arr[257].toString() : null);
		obj.setUserDefField25(arr[258] != null ? arr[258].toString() : null);
		obj.setUserDefField26(arr[259] != null ? arr[259].toString() : null);
		obj.setUserDefField27(arr[260] != null ? arr[260].toString() : null);
		obj.setUserDefField28(arr[261] != null ? arr[261].toString() : null);
		obj.setUserDefField29(arr[262] != null ? arr[262].toString() : null);
		obj.setUserDefField30(arr[263] != null ? arr[263].toString() : null);
		obj.setSupplyTypeASP(arr[264] != null ? arr[264].toString() : null);
		obj.setApproximateDistanceASP(
				arr[265] != null ? arr[265].toString() : null);
		obj.setDistanceSavedtoEWB(
				arr[266] != null ? arr[266].toString() : null);
		obj.setUserId1(arr[267] != null ? arr[267].toString() : null);
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

			String timestamp = arr[291].toString();
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];

			obj.setIrnCancellationDate(date);
			obj.setIrnCancellationTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		if (arr[292] != null) {

			LOGGER.debug("EWB Cancellation Date :" + arr[292].toString());
			String timestamp = arr[292].toString();
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];

			obj.seteWBCancellationDate(date);
			obj.seteWBCancellationTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}
		obj.setDigiGSTStatus(arr[293] != null ? arr[293].toString() : null);
		obj.setInfoErrorCode(arr[294] != null ? arr[294].toString() : null);
		obj.setInfoErrorMsg(arr[295] != null ? arr[295].toString() : null);

		String itemUqc = arr[310] != null ? arr[310].toString() : null;
		String itemQty = arr[311] != null ? arr[311].toString() : null;

		String itemUqcUser = arr[86] != null ? arr[86].toString() : null;
		String itemQtyUser = arr[87] != null ? arr[87].toString() : null;

		String uqc = itemUqcUser != null ? itemUqcUser : itemUqc;
		String qty = itemQtyUser != null ? itemQtyUser : itemQty;

		obj.setuQC(uqc != null ? uqc : null);
		obj.setQuantity(qty != null ? qty : null);

		return obj;
	}

	@SuppressWarnings("unchecked")
	public Object convert1(Map<String, Object> contextMap, Object[] arr) {

		List<String> selectedFields = new ArrayList<>();
		if (!contextMap.isEmpty() && contextMap.containsKey("selectedList")) {
			selectedFields = (List<String>) contextMap.get("selectedList");
		}
		
		DataStatusEinvoiceDto obj = new DataStatusEinvoiceDto();
		String errDesc = null;
		String infoDesc = null;

		String errCode = extractFieldData(arr, "DigiGSTErrorDescription",
				selectedFields);

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errCodeList.replaceAll(String::trim);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "OUTWARD");
		}

		obj.setAspErrorDesc(errDesc);

		String infoCode = extractFieldData(arr, "DigiGSTInformationDescription",
				selectedFields);

		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoCodeList.replaceAll(String::trim);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "OUTWARD");
		}

		obj.setAspInformationDesc(infoDesc);

		obj.setIrnStatus(
				extractNumFieldData(arr, "EINVStatus", selectedFields));

		obj.setIrnNo(extractFieldData(arr, "IRN(IRP)", selectedFields));

		obj.setIrnAcknowledgmentNo(extractNumFieldData(arr,
				"EINVAcknowledgmentNo", selectedFields));

		if (isDateNull(arr, "EINVAcknowledgmentDate", selectedFields)) {

			String timestamp = extractDateData(arr, "EINVAcknowledgmentDate",
					selectedFields);
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];

			obj.setIrnAcknowledgmentDate(date);
		}

		if (isDateNull(arr, "EINVAcknowledgmentTime", selectedFields)) {

			String timestamp = extractDateData(arr, "EINVAcknowledgmentTime",
					selectedFields);
			String[] dateTime = timestamp.split(" ");

			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];

			obj.setIrnAcknowledgmentTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		obj.setSignedQRCode(
				extractFieldData(arr, "SignedQRCode", selectedFields));

		obj.setIrpErrorCode(
				extractFieldData(arr, "EINVErrorCode", selectedFields));

		obj.setIrpErrorDescription(
				extractFieldData(arr, "EINVErrorDescription", selectedFields));

		obj.setEwbStatus(extractFieldData(arr, "EWBStatus", selectedFields));
		obj.setEwbValidupto(
				extractFieldData(arr, "EwbValidupto", selectedFields));

		String irnStatus = obj.getIrnStatus();

		if ("'GENERATED".equalsIgnoreCase(irnStatus)) {
			String timestamp = extractDateData(arr, "EINVAcknowledgmentDate",
					selectedFields);
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];
			String[] datesplit = date.split("-");
			String[] timesplit = time.split(":");
			LocalDateTime irnDate = LocalDateTime.of(
					Integer.parseInt(datesplit[2]),
					Integer.parseInt(datesplit[1]),
					Integer.parseInt(datesplit[0]),
					Integer.parseInt(timesplit[0]),
					Integer.parseInt(timesplit[1]),
					Integer.parseInt(timesplit[2]));

			if (CommonUtility.deriveEinvStatus(irnDate)) {
				obj.setIrnStatus(DownloadReportsConstant.CSVCHARACTER
						.concat("Generated – Available for Cancellation "));
			}

		}

		// mapping doubt
		obj.setEwbNo(extractNumFieldData(arr, "EwbNo", selectedFields));

		if (isDateNull(arr, "EwbDate", selectedFields)) {

			String timestamp = extractDateData(arr, "EwbDate", selectedFields);
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];

			obj.setEwbRespDate(date);
		}

		if (isDateNull(arr, "EWBTime", selectedFields)) {

			String timestamp = extractDateData(arr, "EWBTime", selectedFields);
			String[] dateTime = timestamp.split(" ");
			int ln = dateTime.length;
			String time = ln>1 ? dateTime[1] : dateTime[0];

			obj.setEwbTime(DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		obj.setEwbErrorCode(
				extractFieldData(arr, "EWBErrorCode", selectedFields));

		obj.setEwbErrorDescription(
				extractFieldData(arr, "EWBErrorDescription", selectedFields));

		obj.setGstnStatus(extractFieldData(arr, "GSTNStatus", selectedFields));

		obj.setGstnRefid(extractFieldData(arr, "GSTNRefid", selectedFields));

		if (isDateNull(arr, "GSTNRefidDate", selectedFields)) {

			LOGGER.debug("GstnRefidTime :"
					+ arr[selectedFields.indexOf("GSTNRefidDate")].toString());

			String timestamp = extractDateData(arr, "GSTNRefidDate",
					selectedFields);
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];
			// String time = dateTime[1];
			obj.setGstnRefidDate(date);

		}

		if (isDateNull(arr, "GSTNRefidTime", selectedFields)) {

			String timestamp = extractDateData(arr, "GSTNRefidTime",
					selectedFields);
			String[] dateTime = timestamp.split(" ");
			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];

			obj.setGstnRefidTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		obj.setGstnErrorCode(
				extractFieldData(arr, "GSTNErrorCode", selectedFields));
		obj.setGstnErrorDescription(
				extractFieldData(arr, "GSTNErrorDescription", selectedFields));

		obj.setReturnType(extractFieldData(arr, "ReturnType", selectedFields));

		obj.setTableNumber(
				extractFieldData(arr, "TableNumber", selectedFields));
		obj.setIrn(extractFieldData(arr, "IRN", selectedFields));

		obj.setIrnDate(extractNumFieldData(arr, "IRNDate", selectedFields));

		obj.setTaxScheme(extractFieldData(arr, "TaxScheme", selectedFields));
		obj.setCancellationReason(
				extractFieldData(arr, "CancellationReason", selectedFields));

		obj.setCancellationRemarks(
				extractFieldData(arr, "CancellationRemarks", selectedFields));

		obj.setSupplyType(extractFieldData(arr, "SupplyType", selectedFields));

		obj.setDocCategory(
				extractFieldData(arr, "DocCategory", selectedFields));

                String documentType = extractFieldData(arr, "DocumentType", selectedFields);
                obj.setDocumentType(documentType);
                boolean isNegative = "CR".equalsIgnoreCase(documentType)
                                || "RCR".equalsIgnoreCase(documentType)
                                || "CDNR".equalsIgnoreCase(documentType)
                                || "CDNRA".equalsIgnoreCase(documentType)
                                || "CDNUR".equalsIgnoreCase(documentType)
                                || "CDNURA".equalsIgnoreCase(documentType);
		obj.setDocumentNumber(
				extractFieldData(arr, "DocumentNumber", selectedFields));
		obj.setDocumentNumber(
				extractFieldData(arr, "DocumentNumber", selectedFields));

		if (isDateNull(arr, "DocumentDate", selectedFields)) {
			String strdate = extractDateData(arr, "DocumentDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {
			obj.setDocumentDate(null);
		}

		obj.setReverseChargeFlag(
				extractFieldData(arr, "ReverseChargeFlag", selectedFields));

		obj.setSupplierGSTIN(
				extractFieldData(arr, "SupplierGSTIN", selectedFields));
		obj.setSupplierTradeName(
				extractFieldData(arr, "SupplierTradeName", selectedFields));
		obj.setSupplierLegalName(
				extractFieldData(arr, "SupplierLegalName", selectedFields));

		obj.setSupplierAddress1(
				extractFieldData(arr, "SupplierAddress1", selectedFields));
		obj.setSupplierAddress2(
				extractFieldData(arr, "SupplierAddress2", selectedFields));

		obj.setSupplierLocation(
				extractFieldData(arr, "SupplierLocation", selectedFields));

		obj.setSupplierPincode(
				extractFieldData(arr, "SupplierPincode", selectedFields));
		obj.setSupplierStateCode(
				extractNumFieldData(arr, "SupplierStateCode", selectedFields));
		obj.setSupplierPhone(
				extractFieldData(arr, "SupplierPhone", selectedFields));

		obj.setSupplierEmail(
				extractFieldData(arr, "SupplierEmail", selectedFields));
		obj.setCustomerGSTIN(
				extractFieldData(arr, "CustomerGSTIN", selectedFields));

		obj.setCustomerTradeName(
				extractFieldData(arr, "CustomerTradeName", selectedFields));
		obj.setCustomerLegalName(
				extractFieldData(arr, "CustomerLegalName", selectedFields));
		obj.setCustomerAddress1(
				extractFieldData(arr, "CustomerAddress1", selectedFields));
		obj.setCustomerAddress2(
				extractFieldData(arr, "CustomerAddress2", selectedFields));
		obj.setCustomerLocation(
				extractFieldData(arr, "CustomerLocation", selectedFields));

		obj.setCustomerPincode(
				extractFieldData(arr, "CustomerPincode", selectedFields));
		obj.setCustomerStateCode(
				extractNumFieldData(arr, "CustomerStateCode", selectedFields));
		obj.setBillingPOS(
				extractNumFieldData(arr, "BillingPOS", selectedFields));

		obj.setCustomerPhone(
				extractFieldData(arr, "CustomerPhone", selectedFields));
		obj.setCustomerEmail(
				extractFieldData(arr, "CustomerEmail", selectedFields));

		obj.setDispatcherGSTIN(
				extractFieldData(arr, "DispatcherGSTIN", selectedFields));
		obj.setDispatcherTradeName(
				extractFieldData(arr, "DispatcherTradeName", selectedFields));

		obj.setDispatcherAddress1(
				extractFieldData(arr, "DispatcherAddress1", selectedFields));
		obj.setDispatcherAddress2(
				extractFieldData(arr, "DispatcherAddress2", selectedFields));

		obj.setDispatcherLocation(
				extractFieldData(arr, "DispatcherLocation", selectedFields));

		obj.setDispatcherPincode(
				extractFieldData(arr, "DispatcherPincode", selectedFields));
		obj.setDispatcherStateCode(extractNumFieldData(arr,
				"DispatcherStateCode", selectedFields));

		obj.setShipToGSTIN(
				extractFieldData(arr, "ShipToGSTIN", selectedFields));

		obj.setShipToTradeName(
				extractFieldData(arr, "ShipToTradeName", selectedFields));
		obj.setShipToLegalName(
				extractFieldData(arr, "ShipToLegalName", selectedFields));
		obj.setShipToAddress1(
				extractFieldData(arr, "ShipToAddress1", selectedFields));
		obj.setShipToAddress2(
				extractFieldData(arr, "ShipToAddress2", selectedFields));

		obj.setShipToLocation(
				extractFieldData(arr, "ShipToLocation", selectedFields));
		obj.setShipToPincode(
				extractFieldData(arr, "ShipToPincode", selectedFields));
		obj.setShipToStateCode(
				extractNumFieldData(arr, "ShipToStateCode", selectedFields));

		obj.setItemSerialNumber(
				extractFieldData(arr, "ItemSerialNumber", selectedFields));
		obj.setProductSerialNumber(
				extractFieldData(arr, "ProductSerialNumber", selectedFields));
		obj.setProductName(
				extractFieldData(arr, "ProductName", selectedFields));

		obj.setProductDescription(
				extractFieldData(arr, "ProductDescription", selectedFields));
		obj.setIsService(extractFieldData(arr, "IS_SERVICE", selectedFields));
		obj.setHsn(extractFieldData(arr, "HSN", selectedFields));

		obj.setBarcode(extractFieldData(arr, "Barcode", selectedFields));
		obj.setBatchName(extractFieldData(arr, "BatchName", selectedFields));

		if (isDateNull(arr, "BatchExpiryDate", selectedFields)) {
			String strdate = extractDateData(arr, "BatchExpiryDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBatchExpiryDate(newDate);
		} else {
			obj.setBatchExpiryDate(null);
		}

		if (isDateNull(arr, "WarrantyDate", selectedFields)) {
			String strdate = extractDateData(arr, "WarrantyDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setWarrantyDate(newDate);
		} else {
			obj.setWarrantyDate(null);
		}

		obj.setOrderlineReference(
				extractFieldData(arr, "OrderLineReference", selectedFields));
		obj.setAttributeName(
				extractFieldData(arr, "AttributeName", selectedFields));
		obj.setAttributeValue(
				extractFieldData(arr, "AttributeValue", selectedFields));

		obj.setOriginCountry(
				extractFieldData(arr, "OriginCountry", selectedFields));

		obj.setFreeQuantity(
				extractFieldData(arr, "FreeQuantity", selectedFields));

		obj.setUnitPrice(extractFieldData(arr, "UnitPrice", selectedFields));
		//obj.setItemAmount(extractFieldData(arr, "ItemAmount", selectedFields));
		obj.setItemAmount(convertAmountToNegative(extractFieldData(arr, "ItemAmount", selectedFields), isNegative));
		
		obj.setItemDiscount(
				extractFieldData(arr, "ItemDiscount", selectedFields));

		obj.setPreTaxAmount(
				extractFieldData(arr, "PreTaxAmount", selectedFields));
		obj.setItemAssessableAmount(convertAmountToNegative(
				extractFieldData(arr, "ItemAssessableAmount", selectedFields), isNegative));
		obj.setiGSTRate(extractFieldData(arr, "IGSTRate", selectedFields));

		obj.setiGSTAmount(convertAmountToNegative(
			    extractFieldData(arr, "IGSTAmount", selectedFields), isNegative));

		obj.setcGSTRate(extractFieldData(arr, "CGSTRate", selectedFields));

		obj.setcGSTAmount(convertAmountToNegative(
			    extractFieldData(arr, "CGSTAmount", selectedFields), isNegative));

		obj.setsGSTRate(extractFieldData(arr, "SGSTRate", selectedFields));


		obj.setsGSTAmount(convertAmountToNegative(
			    extractFieldData(arr, "SGSTAmount", selectedFields), isNegative));

		obj.setCessAdvaloremRate(
				extractFieldData(arr, "CessAdvaloremRate", selectedFields));

		obj.setCessAdvaloremAmount(convertAmountToNegative(
			    extractFieldData(arr, "CessAdvaloremAmount", selectedFields), isNegative));
		obj.setCessSpecificRate(
				extractFieldData(arr, "CessSpecificRate", selectedFields));
		obj.setCessSpecificAmount(convertAmountToNegative(
			    extractFieldData(arr, "CessSpecificAmount", selectedFields), isNegative));

		obj.setStateCessAdvaloremRate(extractFieldData(arr,
				"StateCessAdvaloremRate", selectedFields));
		obj.setStateCessAdvaloremAmount(convertAmountToNegative(
			    extractFieldData(arr, "StateCessAdvaloremAmount", selectedFields), isNegative));

		obj.setStateCessSpecificRate(
				extractFieldData(arr, "StateCessSpecificRate", selectedFields));

		obj.setStateCessSpecificAmount(convertAmountToNegative(
			    extractFieldData(arr, "StateCessSpecificAmount", selectedFields), isNegative));

		obj.setItemOtherCharges(
				extractFieldData(arr, "ItemOtherCharges", selectedFields));
		obj.setTotalItemAmount(convertAmountToNegative(
			    extractFieldData(arr, "TotalItemAmount", selectedFields), isNegative));

		obj.setInvoiceOtherCharges(
				extractFieldData(arr, "InvoiceOtherCharges", selectedFields));

		obj.setInvoiceAssessableAmount(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceAssessableAmount", selectedFields), isNegative));

		obj.setInvoiceIGSTAmount(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceIGSTAmount", selectedFields), isNegative));
		/*
		 * obj.setInvoiceCGSTAmount( extractFieldData(arr, "InvoiceCGSTAmount",
		 * selectedFields)); obj.setInvoiceSGSTAmount( extractFieldData(arr,
		 * "InvoiceSGSTAmount", selectedFields));
		 * 
		 * obj.setInvoiceCessAdvaloremAmount(extractFieldData(arr,
		 * "InvoiceCessAdvaloremAmount", selectedFields));
		 * 
		 * obj.setInvoiceCessSpecificAmount(extractFieldData(arr,
		 * "InvoiceCessSpecificAmount", selectedFields));
		 * obj.setInvoiceStateCessAdvaloremAmount(extractFieldData(arr,
		 * "InvoiceStateCessAdvaloremAmount", selectedFields));
		 * obj.setInvoiceStateCessSpecificAmount(extractFieldData(arr,
		 * "InvoiceStateCessSpecificAmount", selectedFields)); obj.setInvoiceValue(
		 * extractFieldData(arr, "InvoiceValue", selectedFields));
		 */
		
		obj.setInvoiceCGSTAmount(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceCGSTAmount", selectedFields), isNegative));

			obj.setInvoiceSGSTAmount(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceSGSTAmount", selectedFields), isNegative));

			obj.setInvoiceCessAdvaloremAmount(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceCessAdvaloremAmount", selectedFields), isNegative));

			obj.setInvoiceCessSpecificAmount(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceCessSpecificAmount", selectedFields), isNegative));

			obj.setInvoiceStateCessAdvaloremAmount(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceStateCessAdvaloremAmount", selectedFields), isNegative));

			obj.setInvoiceStateCessSpecificAmount(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceStateCessSpecificAmount", selectedFields), isNegative));

			obj.setInvoiceValue(convertAmountToNegative(
			    extractFieldData(arr, "InvoiceValue", selectedFields), isNegative));

		obj.setRoundOff(extractFieldData(arr, "RoundOff", selectedFields));
		obj.setTotalInvoiceValue(extractFieldData(arr,
				"TotalInvoiceValue(InWords)", selectedFields));

		obj.settCSFlagIncomeTax(
				extractFieldData(arr, "TCSFlagIncomeTax", selectedFields));
		obj.settCSRateIncomeTax(
				extractFieldData(arr, "TCSRateIncomeTax", selectedFields));

		obj.settCSAmountIncomeTax(
				extractFieldData(arr, "TCSAmountIncomeTax", selectedFields));
		obj.setCustomerPANOrAadhaar(
				extractFieldData(arr, "CustomerPANOrAadhaar", selectedFields));

		obj.setCurrencyCode(
				extractFieldData(arr, "CurrencyCode", selectedFields));
		obj.setCountryCode(
				extractFieldData(arr, "CountryCode", selectedFields));

		obj.setInvoiceValueFC(
				extractFieldData(arr, "InvoiceValueFC", selectedFields));
		obj.setPortCode(extractFieldData(arr, "PortCode", selectedFields));

		obj.setShippingBillNumber(
				extractFieldData(arr, "ShippingBillNumber", selectedFields));
		if (isDateNull(arr, "ShippingBillDate", selectedFields)) {
			String strdate = extractDateData(arr, "ShippingBillDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setShippingBillDate(newDate);
		} else {
			obj.setShippingBillDate(null);
		}

		obj.setInvoiceRemarks(
				extractFieldData(arr, "InvoiceRemarks", selectedFields));

		if (isDateNull(arr, "InvoicePeriodStartDate", selectedFields)) {
			String strdate = extractDateData(arr, "InvoicePeriodStartDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodStartDate(newDate);
		} else {
			obj.setInvoicePeriodStartDate(null);
		}

		if (isDateNull(arr, "InvoicePeriodEndDate", selectedFields)) {
			String strdate = extractDateData(arr, "InvoicePeriodEndDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodEndDate(newDate);
		} else {
			obj.setInvoicePeriodEndDate(null);
		}

		obj.setPreceedingInvoiceNumber(extractNumFieldData(arr,
				"PreceedingInvoiceNumber", selectedFields));

		if (isDateNull(arr, "PreceedingInvoiceDate", selectedFields)) {
			String strdate = extractDateData(arr, "PreceedingInvoiceDate",
					selectedFields);
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
				extractFieldData(arr, "OtherReference", selectedFields));
		obj.setReceiptAdviceReference(extractFieldData(arr,
				"ReceiptAdviceReference", selectedFields));
		obj.setReceiptAdviceDate(
				extractFieldData(arr, "ReceiptAdviceDate", selectedFields));
		obj.setTenderReference(
				extractFieldData(arr, "TenderReference", selectedFields));
		obj.setContractReference(
				extractFieldData(arr, "ContractReference", selectedFields));
		obj.setExternalReference(
				extractFieldData(arr, "ExternalReference", selectedFields));

		obj.setProjectReference(
				extractFieldData(arr, "ProjectReference", selectedFields));

		obj.setCustomerPOReferenceNumber(extractFieldData(arr,
				"CustomerPOReferenceNumber", selectedFields));
		obj.setCustomerPOReferenceDate(extractFieldData(arr,
				"CustomerPOReferenceDate", selectedFields));

		obj.setPayeeName(extractFieldData(arr, "PayeeName", selectedFields));

		obj.setModeOfPayment(
				extractFieldData(arr, "ModeOfPayment", selectedFields));
		obj.setBranchOrIFSCCode(
				extractFieldData(arr, "BranchOrIFSCCode", selectedFields));
		obj.setPaymentTerms(
				extractFieldData(arr, "PaymentTerms", selectedFields));
		obj.setPaymentInstruction(
				extractFieldData(arr, "PaymentInstruction", selectedFields));

		obj.setCreditTransfer(
				extractFieldData(arr, "CreditTransfer", selectedFields));
		obj.setDirectDebit(
				extractFieldData(arr, "DirectDebit", selectedFields));
		obj.setCreditDays(extractFieldData(arr, "CreditDays", selectedFields));
		obj.setPaidAmount(extractFieldData(arr, "PaidAmount", selectedFields));
		obj.setBalanceAmount(
				extractFieldData(arr, "BalanceAmount", selectedFields));

		if (isDateNull(arr, "PaymentDueDate", selectedFields)) {
			String strdate = extractDateData(arr, "PaymentDueDate",
					selectedFields);
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
				extractNumFieldData(arr, "AccountDetail", selectedFields));

		obj.setEcomGSTIN(extractFieldData(arr, "EcomGSTIN", selectedFields));
		obj.setEcomTransactionID(
				extractFieldData(arr, "EcomTransactionID", selectedFields));
		obj.setSupportingDocURL(
				extractFieldData(arr, "SupportingDocURL", selectedFields));

		obj.setSupportingDocument(
				extractFieldData(arr, "SupportingDocument", selectedFields));
		obj.setAdditionalInformation(
				extractFieldData(arr, "AdditionalInformation", selectedFields));
		obj.setTransactionType(
				extractFieldData(arr, "TransactionType", selectedFields));
		obj.setSubSupplyType(
				extractFieldData(arr, "SubSupplyType", selectedFields));
		obj.setOtherSupplyTypeDescription(extractFieldData(arr,
				"OtherSupplyTypeDescription", selectedFields));

		obj.setTransporterID(
				extractFieldData(arr, "TransporterId", selectedFields));
		obj.setTransporterName(
				extractFieldData(arr, "TransportName", selectedFields));
		obj.setTransportMode(
				extractFieldData(arr, "TransporterMode", selectedFields));

		obj.setTransportDocNo(
				extractFieldData(arr, "TransportDocNo", selectedFields));

		if (isDateNull(arr, "TransportDocDate", selectedFields)) {
			String strdate = extractDateData(arr, "TransportDocDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setTransportDocDate(newDate);
		} else {
			obj.setTransportDocDate(null);
		}

		// mapping doubt
		obj.setDistance(extractFieldData(arr, "Distance", selectedFields));

		obj.setVehicleNo(extractFieldData(arr, "VehicleNo", selectedFields));

		obj.setVehicleType(
				extractFieldData(arr, "VehicleType", selectedFields));
		obj.setReturnPeriod(
				extractNumFieldData(arr, "ReturnPeriod", selectedFields));

		obj.setOriginalDocumentType(
				extractFieldData(arr, "OriginalDocumentType", selectedFields));
		obj.setOriginalCustomerGSTIN(
				extractFieldData(arr, "OriginalCustomerGSTIN", selectedFields));

		obj.setDifferentialPercentageFlag(extractFieldData(arr,
				"DifferentialPercentageFlag", selectedFields));
		obj.setSec7ofIGSTFlag(
				extractFieldData(arr, "Section7OfIGSTFlag", selectedFields));
		obj.setClaimRefndFlag(
				extractFieldData(arr, "ClaimRefundFlag", selectedFields));
		obj.setAutoPopltToRefund(
				extractFieldData(arr, "AutoPopulateToRefund", selectedFields));

		obj.setcRDRPreGST(extractFieldData(arr, "CRDRPreGST", selectedFields));
		obj.setCustomerType(
				extractFieldData(arr, "CustomerType", selectedFields));
		obj.setCustomerCode(
				extractFieldData(arr, "CustomerCode", selectedFields));
		obj.setProductCode(
				extractFieldData(arr, "ProductCode", selectedFields));
		obj.setCategoryOfProduct(
				extractFieldData(arr, "CategoryOfProduct", selectedFields));

		obj.setiTCFlag(extractFieldData(arr, "ITCFlag", selectedFields));

		obj.setStateApplyingCess(
				extractFieldData(arr, "StateApplyingCess", selectedFields));

		obj.setfOB(extractFieldData(arr, "FOB", selectedFields));

		obj.setExportDuty(extractFieldData(arr, "ExportDuty", selectedFields));
		obj.setExchangeRate(
				extractFieldData(arr, "ExchangeRate", selectedFields));
		obj.setReasonForCreditDebitNote(extractFieldData(arr,
				"ReasonForCreditDebitNote", selectedFields));
		obj.settCSFlagGST(extractFieldData(arr, "TCSFlagGST", selectedFields));

		obj.settCSIGSTAmount(
				extractFieldData(arr, "TCSIGSTAmount", selectedFields));
		obj.settCSCGSTAmount(
				extractFieldData(arr, "TCSCGSTAmount", selectedFields));
		obj.settCSSGSTAmount(
				extractFieldData(arr, "TCSSGSTAmount", selectedFields));

		obj.settDSFlagGST(extractFieldData(arr, "TDSFlagGST", selectedFields));

		obj.settDSIGSTAmount(
				extractFieldData(arr, "TDSIGSTAmount", selectedFields));
		obj.settDSCGSTAmount(
				extractFieldData(arr, "TDSCGSTAmount", selectedFields));
		obj.settDSSGSTAmount(
				extractFieldData(arr, "TDSSGSTAmount", selectedFields));
		obj.setUserId(extractFieldData(arr, "UserID", selectedFields));

		obj.setCompanyCode(
				extractFieldData(arr, "CompanyCode", selectedFields));
		obj.setSourceIdentifier(
				extractFieldData(arr, "SourceIdentifier", selectedFields));
		obj.setSourceFileName(
				extractFieldData(arr, "SourceFileName", selectedFields));
		obj.setPlantCode(extractFieldData(arr, "PlantCode", selectedFields));
		obj.setDivision(extractFieldData(arr, "Division", selectedFields));
		obj.setSubDivision(
				extractFieldData(arr, "SubDivision", selectedFields));
		obj.setLocation(extractFieldData(arr, "Location", selectedFields));
		obj.setSalesOrganisation(
				extractFieldData(arr, "SalesOrganisation", selectedFields));

		obj.setDistributionChannel(
				extractFieldData(arr, "DistributionChannel", selectedFields));
		obj.setProfitCentre1(
				extractFieldData(arr, "ProfitCentre1", selectedFields));
		obj.setProfitCentre2(
				extractFieldData(arr, "ProfitCentre2", selectedFields));
		obj.setProfitCentre3(
				extractFieldData(arr, "ProfitCentre3", selectedFields));
		obj.setProfitCentre4(
				extractFieldData(arr, "ProfitCentre4", selectedFields));
		obj.setProfitCentre5(
				extractFieldData(arr, "ProfitCentre5", selectedFields));
		obj.setProfitCentre6(
				extractFieldData(arr, "ProfitCentre6", selectedFields));
		obj.setProfitCentre7(
				extractFieldData(arr, "ProfitCentre7", selectedFields));
		obj.setProfitCentre8(
				extractFieldData(arr, "ProfitCentre8", selectedFields));

		obj.setGlAssessableValue(
				extractFieldData(arr, "GLAssessableValue", selectedFields));
		obj.setGlIGST(extractFieldData(arr, "GLIGST", selectedFields));
		obj.setGlCGST(extractFieldData(arr, "GLCGST", selectedFields));
		obj.setGlSGST(extractFieldData(arr, "GLSGST", selectedFields));
		obj.setGlAdvaloremCess(
				extractFieldData(arr, "GLAdvaloremCess", selectedFields));
		obj.setGlSpecificCess(
				extractFieldData(arr, "GLSpecificCess", selectedFields));
		obj.setgLStateCessAdvalorem(
				extractFieldData(arr, "GLStateCessAdvalorem", selectedFields));

		obj.setgLStateCessSpecific(
				extractFieldData(arr, "GLStateCessSpecific", selectedFields));

		if (isDateNull(arr, "GLPostingDate", selectedFields)) {
			String strdate = extractDateData(arr, "GLPostingDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setGlPostingDate(newDate);
		} else {
			obj.setGlPostingDate(null);
		}

		obj.setSalesOrderNumber(
				extractFieldData(arr, "SalesOrderNumber", selectedFields));

		// Mapping Doubt
		// EWBNo

		// EWBNumber
		obj.seteWBNumber(extractNumFieldData(arr, "EWBNumber", selectedFields));

		// Mapping Doubt
		// EWBDATE
		obj.seteWBDate(extractNumFieldData(arr, "EwbDate", selectedFields));

		obj.setAccountingVoucherNumber(extractFieldData(arr,
				"AccountingVoucherNumber", selectedFields));

		if (isDateNull(arr, "AccountingVoucherDate", selectedFields)) {
			String strdate = extractDateData(arr, "AccountingVoucherDate",
					selectedFields);
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAccountingVoucherDate(newDate);
		} else {
			obj.setAccountingVoucherDate(null);
		}

		obj.setDocumentReferenceNumber(extractFieldData(arr,
				"DocumentReferenceNumber", selectedFields));
		obj.setCustomerTAN(
				extractFieldData(arr, "CustomerTAN", selectedFields));

		obj.setUserDefField1(
				extractFieldData(arr, "UserDefinedField1", selectedFields));

		obj.setUserDefField2(
				extractFieldData(arr, "UserDefinedField2", selectedFields));
		obj.setUserDefField3(
				extractFieldData(arr, "UserDefinedField3", selectedFields));
		obj.setUserDefField4(
				extractFieldData(arr, "UserDefinedField4", selectedFields));
		obj.setUserDefField5(
				extractFieldData(arr, "UserDefinedField5", selectedFields));
		obj.setUserDefField6(
				extractFieldData(arr, "UserDefinedField6", selectedFields));
		obj.setUserDefField7(
				extractFieldData(arr, "UserDefinedField7", selectedFields));
		obj.setUserDefField8(
				extractFieldData(arr, "UserDefinedField8", selectedFields));
		obj.setUserDefField9(
				extractFieldData(arr, "UserDefinedField9", selectedFields));
		obj.setUserDefField10(
				extractFieldData(arr, "UserDefinedField10", selectedFields));
		obj.setUserDefField11(
				extractFieldData(arr, "UserDefinedField11", selectedFields));
		obj.setUserDefField12(
				extractFieldData(arr, "UserDefinedField12", selectedFields));
		obj.setUserDefField13(
				extractFieldData(arr, "UserDefinedField13", selectedFields));
		obj.setUserDefField14(
				extractFieldData(arr, "UserDefinedField14", selectedFields));
		obj.setUserDefField15(
				extractFieldData(arr, "UserDefinedField15", selectedFields));
		obj.setUserDefField16(
				extractFieldData(arr, "UserDefinedField16", selectedFields));
		obj.setUserDefField17(
				extractFieldData(arr, "UserDefinedField17", selectedFields));
		obj.setUserDefField18(
				extractFieldData(arr, "UserDefinedField18", selectedFields));
		obj.setUserDefField19(
				extractFieldData(arr, "UserDefinedField19", selectedFields));
		obj.setUserDefField20(
				extractFieldData(arr, "UserDefinedField20", selectedFields));
		obj.setUserDefField21(
				extractFieldData(arr, "UserDefinedField21", selectedFields));
		obj.setUserDefField22(
				extractFieldData(arr, "UserDefinedField22", selectedFields));
		obj.setUserDefField23(
				extractFieldData(arr, "UserDefinedField23", selectedFields));
		obj.setUserDefField24(
				extractFieldData(arr, "UserDefinedField24", selectedFields));
		obj.setUserDefField25(
				extractFieldData(arr, "UserDefinedField25", selectedFields));
		obj.setUserDefField26(
				extractFieldData(arr, "UserDefinedField26", selectedFields));
		obj.setUserDefField27(
				extractFieldData(arr, "UserDefinedField27", selectedFields));
		obj.setUserDefField28(
				extractFieldData(arr, "UserDefinedField28", selectedFields));
		obj.setUserDefField29(
				extractFieldData(arr, "UserDefinedField29", selectedFields));
		obj.setUserDefField30(
				extractFieldData(arr, "UserDefinedField30", selectedFields));

		obj.setSupplyTypeASP(extractFieldData(arr, "DerivedSupplyType-DigiGST",
				selectedFields));

		obj.setApproximateDistanceASP(extractFieldData(arr,
				"Distance (Computed by DigiGST)", selectedFields));

		obj.setDistanceSavedtoEWB(
				extractFieldData(arr, "Distance (NIC)", selectedFields));

		obj.setUserId1(extractFieldData(arr, "USER_ID", selectedFields));
		obj.setFileID(extractFieldData(arr, "FileID", selectedFields));
		obj.setFileName(extractFieldData(arr, "FileName", selectedFields));

		obj.setInvoiceOtherChargesASP(extractFieldData(arr,
				"InvoiceOtherCharges-DigiGST", selectedFields));
		obj.setInvoiceAssessableAmountASP(extractFieldData(arr,
				"InvoiceAssessableAmount-DigiGST", selectedFields));
		obj.setInvoiceIGSTAmountASP(extractFieldData(arr,
				"InvoiceIGSTAmount-DigiGST", selectedFields));
		obj.setInvoiceCGSTAmountASP(extractFieldData(arr,
				"InvoiceCGSTAmount-DigiGST", selectedFields));
		obj.setInvoiceSGSTAmountASP(extractFieldData(arr,
				"InvoiceSGSTAmount-DigiGST", selectedFields));
		obj.setInvoiceCessAdvaloremAmountASP(extractFieldData(arr,
				"InvoiceCessAdvaloremAmount-DigiGST", selectedFields));
		obj.setInvoiceCessSpecificAmountASP(extractFieldData(arr,
				"InvoiceCessSpecificAmount-DigiGST", selectedFields));

		obj.setInvoiceStateCessAdvaloremAmountASP(extractFieldData(arr,
				"InvoiceStateCessAdvaloremAmount-DigiGST", selectedFields));
		obj.setInvoiceStateCessSpecificAmountASP(extractFieldData(arr,
				"InvoiceStateCessSpecificAmount-DigiGST", selectedFields));
		obj.setInvoiceValueASP(
				extractFieldData(arr, "InvoiceValue-DigiGST", selectedFields));
		obj.setIntegratedTaxAmountASP(
				extractFieldData(arr, "IGSTAmount-DigiGST", selectedFields));
		obj.setCentralTaxAmountASP(
				extractFieldData(arr, "CGSTAmount-DigiGST", selectedFields));
		obj.setStateUTTaxAmountASP(
				extractFieldData(arr, "SGSTAmount-DigiGST", selectedFields));

		obj.setCessAdvaloremAmountASP(extractFieldData(arr,
				"CessAdvaloremAmount-DigiGST", selectedFields));
		obj.setStateCessAdvaloremAmountASP(extractFieldData(arr,
				"StateCessAdvaloremAmount-DigiGST", selectedFields));
		obj.setIntegratedTaxAmountRET1Impact(
				extractFieldData(arr, "IGSTAmount-Difference", selectedFields));
		obj.setCentralTaxAmountRET1Impact(
				extractFieldData(arr, "CGSTAmount-Difference", selectedFields));
		obj.setStateUTTaxAmountRET1Impact(
				extractFieldData(arr, "SGSTAmount-Difference", selectedFields));
		obj.setCessAdvaloremAmountDifference(extractFieldData(arr,
				"CessAdvaloremAmount-Difference", selectedFields));
		obj.setStateCessAdvaloremAmountDifference(extractFieldData(arr,
				"StateCessAdvaloremAmount-Difference", selectedFields));
		obj.setRecordStatus(
				extractFieldData(arr, "RecordStatus", selectedFields));

		if (isDateNull(arr, "EINVCancellationDate", selectedFields)) {
			String timestamp = extractDateData(arr, "EINVCancellationDate",
					selectedFields);
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];

			obj.setIrnCancellationDate(date);
		}

		if (isDateNull(arr, "EINVCancellationTime", selectedFields)) {

			String timestamp = extractDateData(arr, "EINVCancellationTime",
					selectedFields);
			String[] dateTime = timestamp.split(" ");
			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];

			obj.setIrnCancellationTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		if (isDateNull(arr, "EWBCancellationDate", selectedFields)) {
			String timestamp = extractDateData(arr, "EWBCancellationDate",
					selectedFields);
			String[] dateTime = timestamp.split(" ");
			String date = dateTime[0];

			obj.seteWBCancellationDate(date);
		}

		if (isDateNull(arr, "EWBCancellationTime", selectedFields)) {

			String timestamp = extractDateData(arr, "EWBCancellationTime",
					selectedFields);
			String[] dateTime = timestamp.split(" ");
			int ln = dateTime.length;
			String time = ln > 1 ? dateTime[1] : dateTime[0];

			obj.seteWBCancellationTime(
					DownloadReportsConstant.CSVCHARACTER.concat(time));
		}

		obj.setDigiGSTStatus(
				extractFieldData(arr, "DigiGSTStatus", selectedFields));
		obj.setInfoErrorCode(
				extractFieldData(arr, "EWBInformationCode", selectedFields));
		obj.setInfoErrorMsg(extractFieldData(arr, "EWBInformationDescription",
				selectedFields));

		String itemUqc = extractFieldData(arr, "UQC", selectedFields);

		// Doubt
		// String itemQty1 = arr[311] != null ? arr[311].toString() : null;
		// String uqc1 = arr[312] != null ? arr[312].toString() : null;
          
		// Doubt
		// String itemUqcUser = arr[86] != null ? arr[86].toString() : null;

		String itemQtyUser = extractFieldData(arr, "Quantity", selectedFields);

		String uqc = itemUqc;
		String qty = itemQtyUser;

		obj.setuQC(uqc != null ? uqc : null);
		obj.setQuantity(qty != null ? qty : null);//EINVvsGSTR1Reponse-orifinal
		obj.setEINVvsGstr1Reponse(extractFieldData(arr, "EINVvsGstr1Reponse", selectedFields));

		return obj;
	}

	private String extractFieldData(Object[] arr, String fieldName,
			List<String> selectedFields) {
		return (selectedFields.indexOf(fieldName) != -1
				&& arr[selectedFields.indexOf(fieldName)] != null)
						? arr[selectedFields.indexOf(fieldName)].toString()
						: null;
		
						
	}

	private boolean isDateNull(Object[] arr, String fieldName,
			List<String> selectedFields) {

		return selectedFields.indexOf(fieldName) != -1
				&& arr[selectedFields.indexOf(fieldName)] != null;
	}

	private String extractDateData(Object[] arr, String fieldName,
			List<String> selectedFields) {

		return arr[selectedFields.indexOf(fieldName)].toString();
	}

	private String extractNumFieldData(Object[] arr, String fieldName,
			List<String> selectedFields) {

		return (selectedFields.indexOf(fieldName) != -1
				&& arr[selectedFields.indexOf(fieldName)] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[selectedFields.indexOf(fieldName)]
										.toString())
						: null;
	}
	private static String convertAmountToNegative(String value, boolean makeNegative) {
	    try {
	        if (value == null) {
	            return null;
	        }
	        value = value.trim();
	        if (value.isEmpty()) {
	            return null;
	        }
	        BigDecimal amount = new BigDecimal(value);
	        if (makeNegative) {
	            amount = amount.negate();
	        }
	        return amount.toPlainString();
	    } catch (NumberFormatException e) {
	        // If it wasn’t a valid number, just return it unchanged
	        return value;
	    }
	}
}

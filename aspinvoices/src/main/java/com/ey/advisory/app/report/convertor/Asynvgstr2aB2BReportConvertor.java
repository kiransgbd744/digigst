package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ConsolidatedGstr2ADto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ReportConvertor;

@Component("Asynvgstr2aB2BReportConvertor")
public class Asynvgstr2aB2BReportConvertor implements ReportConvertor {

	static final String OLDFARMATTER = "yyyy-MM-dd";
	static final String NEWFARMATTER = "dd-MM-yyyy";

	static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss a");

	@Override
	public Object convert(Object[] arr, String reportType) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();
		
		if (arr[0] == null || arr[0] == "null") {
			obj.setReturnPeriod("");
		} else {
			String returnPeriod = arr[0].toString();
			if (returnPeriod.length() != 6) {
				returnPeriod = "0" + returnPeriod;
			}
			returnPeriod = "'" + returnPeriod;
			obj.setReturnPeriod(returnPeriod);
		}
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[7] != null ? arr[7].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? "'" + arr[8].toString() : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
			obj.setTaxableValue(
					bigDecimalTaxVal.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);

		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			obj.setIgstAmt(
					bigDecimalIGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			obj.setCgstAmt(
					bigDecimalCGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			obj.setSgstAmt(
					bigDecimalSGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			obj.setCessAmt(
					bigDecimalCESS.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
			obj.setTotalTaxAmt(
					bigDecimalTOT.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
			obj.setInvoiceValue(
					bigDecimalINV.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		obj.setPos(arr[18] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[18].toString()) : null);

		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[20].toString()) : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(
				arr[27] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[27].toString()) : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);

		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*if (arr[54] == null || arr[54] == "null") {
			obj.setInitiatedTime("");
		} else {
			Timestamp date = (Timestamp) arr[54];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			String[] split = newdate.split(" ");
			obj.setInitiatedTime(split[1]);
		}*/
		obj.setIrnNum(arr[56] != null ? arr[56].toString() : null);
		obj.setIrnGenDate(arr[57] != null ? arr[57].toString() : null);
		obj.setIrnSourceType(arr[58] != null ? arr[58].toString() : null);

		return obj;
	}

}

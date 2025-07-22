package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ConsolidatedGstr2ADto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConvertor;
import com.ibm.icu.text.SimpleDateFormat;

import lombok.extern.slf4j.Slf4j;

@Component("asynvgstr2aReportConvertor")
@Slf4j
public class asynvgstr2aReportConvertor implements ReportConvertor {

	static final String OLDFARMATTER = "yyyy-MM-dd";
	static final String NEWFARMATTER = "dd-MM-yyyy";

	static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss a");

	@Override
	public Object convert(Object[] arr, String reportType) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();
		String docType = arr[5] != null ? arr[5].toString() : null;
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
		obj.setDocumentType(docType);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? "'" + arr[8].toString() : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR") || docType.equalsIgnoreCase("ISDCN"))) {
			obj.setTaxableValue(CheckForNegativeValue(
						bigDecimalTaxVal.setScale(2, BigDecimal.ROUND_HALF_UP)));	
			}else{
			obj.setTaxableValue(
					bigDecimalTaxVal.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR") || docType.equalsIgnoreCase("ISDCN"))) {
			obj.setIgstAmt(CheckForNegativeValue(
					bigDecimalIGST.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}else{
			obj.setIgstAmt(
					bigDecimalIGST.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR") || docType.equalsIgnoreCase("ISDCN"))) {
				obj.setCgstAmt(CheckForNegativeValue(
						bigDecimalCGST.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}else{
			obj.setCgstAmt(
					bigDecimalCGST.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR") || docType.equalsIgnoreCase("ISDCN"))) {
				obj.setSgstAmt(CheckForNegativeValue(
						bigDecimalSGST.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}else{
			obj.setSgstAmt(
					bigDecimalSGST.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}

		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR") || docType.equalsIgnoreCase("ISDCN"))) {
			obj.setCessAmt(CheckForNegativeValue(
					bigDecimalCESS.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}else{
			obj.setCessAmt(
					bigDecimalCESS.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}

		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
			 if(docType != null && (docType.equalsIgnoreCase("CR")
						|| docType.equalsIgnoreCase("C")
						|| docType.equalsIgnoreCase("RCR") || docType.equalsIgnoreCase("ISDCN"))) {
					obj.setTotalTaxAmt(CheckForNegativeValue(
							bigDecimalTOT.setScale(2, BigDecimal.ROUND_HALF_UP)));				 
			 }else{
					obj.setTotalTaxAmt(
							bigDecimalTOT.setScale(2, BigDecimal.ROUND_HALF_UP));				 
			 }
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
			if(docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR") || docType.equalsIgnoreCase("ISDCN"))) {
				obj.setInvoiceValue(CheckForNegativeValue(
						bigDecimalINV.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}else{
				obj.setInvoiceValue(
						bigDecimalINV.setScale(2, BigDecimal.ROUND_HALF_UP));	
			}
			
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
		if(docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR"))) {
			obj.setOriginalTaxableValue(CheckNegativeValue(
					arr[31]));
			obj.setOriginalIGSTAmount(CheckNegativeValue(arr[32]));
			obj.setOriginalCessAmount(CheckNegativeValue(arr[33]));
		}else{
			obj.setOriginalTaxableValue(
					arr[31] != null ? arr[31].toString() : null);
			obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
			obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		}
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ?DownloadReportsConstant.CSVCHARACTER
						.concat(arr[38].toString()) : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		String gstr1Date = arr[42] != null ? arr[42].toString() : null;
		String newGstr1Format = null;
		if (gstr1Date != null) {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yy");
			Date date;
			try {
				date = format1.parse(gstr1Date);
				newGstr1Format = format2.format(date);
				obj.setgSTR1FilingDate(DownloadReportsConstant.CSVCHARACTER
						.concat(newGstr1Format));
			} catch (Exception e) {
				LOGGER.error("exception oocured while parsing date ", e);
			}

		}else{
			obj.setgSTR1FilingDate(newGstr1Format);
		}
		
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

		if (arr[54] == null || arr[54] == "null") {
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
		}
		obj.setIrnNum(arr[56] != null ? arr[56].toString() : null);
		obj.setIrnGenDate(arr[57] != null ? arr[57].toString() : null);
		obj.setIrnSourceType(arr[58] != null ? arr[58].toString() : null);

		return obj;

	}

	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}

	private String CheckNegativeValue(Object value) {

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
			}

		}
		return null;
	}
}

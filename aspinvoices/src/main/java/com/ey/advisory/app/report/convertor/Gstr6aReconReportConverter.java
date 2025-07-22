package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.gstr1.einv.Gstr6aReportDto;
import com.ey.advisory.common.ReportConvertor;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr6aReconReportConverter")
@Slf4j
public class Gstr6aReconReportConverter implements ReportConvertor {

	@Override
	public Object convert(Object[] arr, String reportType) {

		Gstr6aReportDto obj = new Gstr6aReportDto();
		obj.setReturnPeriod(
				(arr[0] != null) ? "'".concat(arr[0].toString()) : null);
		obj.setRecipentGSTIN(arr[1] != null ? arr[1].toString() : null);
		if (arr[2].toString().equalsIgnoreCase("C")) {
			obj.setDocumentType("CR");
		} else if (arr[2].toString().equalsIgnoreCase("D")) {
			obj.setDocumentType("DR");
		} else {
			obj.setDocumentType(arr[2] != null ? arr[2].toString() : null);
		}
		obj.setTransactionType(arr[3] != null ? arr[3].toString() : null);
		obj.setDlinkFlag(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentNumber(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentDate(arr[6] != null ? arr[6].toString() : null);
		obj.setLineNumber(arr[7] != null ? arr[7].toString() : null);
		obj.setSupplierGstin(arr[8] != null ? arr[8].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[9] != null ? arr[9].toString() : null);
		obj.setOriginalDocumentDate(
				arr[10] != null ? arr[10].toString() : null);
		obj.setOriginalInvoiceNo(arr[11] != null ? arr[11].toString() : null);
		obj.setOriginalInvoiceDate(arr[12] != null ? arr[12].toString() : null);
		obj.setCrdrPreGST(arr[13] != null ? arr[13].toString() : null);
		obj.setPos(arr[14] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[14].toString()) : null);
		if (arr[15] != null) {
			obj.setTaxableValue(
					(arr[15] != null) ? "'".concat(arr[15].toString()) : null);
		} else {
			obj.setTaxableValue("'0.00");
		}
		obj.setTaxRate(arr[16] != null
				? appendDecimalDigit((BigDecimal) arr[16]) : null);
		if (arr[17] != null) {
			obj.setIntegratedTaxAmount(
					(arr[17] != null) ? "'".concat(arr[17].toString()) : null);
		} else {
			obj.setIntegratedTaxAmount("'0.00");
		}
		if (arr[18] != null) {
			obj.setCentralTaxAmount(
					(arr[18] != null) ? "'".concat(arr[18].toString()) : null);
		} else {
			obj.setCentralTaxAmount("'0.00");
		}
		if (arr[19] != null) {
			obj.setStateUTTaxAmount(
					(arr[19] != null) ? "'".concat(arr[19].toString()) : null);
		} else {
			obj.setStateUTTaxAmount("'0.00");
		}
		if (arr[20] != null) {
			obj.setCessAmount(
					(arr[20] != null) ? "'".concat(arr[20].toString()) : null);
		} else {
			obj.setCessAmount("'0.00");
		}
		if (arr[21] != null) {
			obj.setInvoiceValue(
					(arr[21] != null) ? "'".concat(arr[21].toString()) : null);
		} else {
			obj.setInvoiceValue("'0.00");
		}
		obj.setDifferentialPercentage(
				arr[22] != null ? arr[22].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[23] != null ? arr[23].toString() : null);
		obj.setReverseCharge(arr[24] != null ? arr[24].toString() : null);
		obj.setSourceTypeIRN(arr[25] != null ? arr[25].toString() : null);
		obj.setIrnNumber(arr[26] != null ? arr[26].toString() : null);
		obj.setIrnGenerationDate(arr[27] != null ? arr[27].toString() : null);
		obj.setCFp((arr[28] != null) ? "'".concat(arr[28].toString()) : null);
		obj.setCounterPartyReturnStatus(
				arr[29] != null ? arr[29].toString() : null);
		obj.setLegalName(arr[30] != null ? arr[30].toString() : null);
		obj.setTradeName(arr[31] != null ? arr[31].toString() : null);


		return obj;
	}

	private String appendDecimalDigit(BigDecimal b) {

		String val = b.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

		String[] s = val.split("\\.");
		if (s.length == 2) {
			if (s[1].length() == 1)
				return "'" + (s[0] + "." + s[1] + "0");
			else {
				return "'" + val;
			}
		} else
			return "'" + (val + ".00");

	}
}

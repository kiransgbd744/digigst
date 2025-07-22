package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.VendorCommReportGenDto;
import com.google.common.base.Strings;

@Component("VendorCommunicationReportConverterImpl")
public class VendorCommunicationReportConverterImpl
		implements VendorCommReportConverter {

	@Override
	public Object convert(Object[] arr, boolean isJsonConv) {

		VendorCommReportGenDto obj = new VendorCommReportGenDto();
		String docType2A = arr[11] != null ? arr[11].toString() : null;
		String docTypePR = arr[12] != null ? arr[12].toString() : null;
		obj.setMismatchReason(arr[0] != null ? arr[0].toString() : null);
		String reportType = arr[1] != null ? arr[1].toString() : null;
		obj.setReportType(reportType);
		obj.setTaxPeriod2A(arr[2] != null
				? isJsonConv ? arr[2].toString() : "'" + arr[2].toString()
				: null);
		obj.setTaxPeriodPR(arr[3] != null
				? isJsonConv ? arr[3].toString() : "'" + arr[3].toString()
				: null);
		obj.setCalenderMonth(arr[4] != null
				? isJsonConv ? arr[4].toString() : "'" + arr[4].toString()
				: null);
		obj.setSupplierName2A(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplierNamePR(arr[6] != null ? arr[6].toString() : null);
		obj.setSupplierGStin2A(arr[7] != null ? arr[7].toString() : null);
		obj.setSupplierGStin2R(arr[8] != null ? arr[8].toString() : null);
		obj.setRecipientGstin2A(arr[9] != null ? arr[9].toString() : null);
		obj.setRecipientGstinPR(arr[10] != null ? arr[10].toString() : null);
		obj.setDocType2A(arr[11] != null ? arr[11].toString() : null);
		obj.setDocTypePR(arr[12] != null ? arr[12].toString() : null);
		obj.setDocumentNumber2A(arr[13] != null
				? isJsonConv ? arr[13].toString() : "'" + arr[13].toString()
				: null);
		obj.setDocumentNumberPR(arr[14] != null
				? isJsonConv ? arr[14].toString() : "'" + arr[14].toString()
				: null);
		obj.setDate2A(arr[15] != null ? arr[15].toString() : null);
		obj.setDatePR(arr[16] != null ? arr[16].toString() : null);
		String gstrPer2A = arr[17].toString();
		obj.setGSTPercentage2A(
				!Strings.isNullOrEmpty(gstrPer2A) ? arr[17].toString() : null);

		String gstrPerPR = arr[18].toString();
		obj.setGSTPercentagePR(
				!Strings.isNullOrEmpty(gstrPerPR) ? arr[18].toString() : null);

		if (docType2A != null && (docType2A.equalsIgnoreCase("CR")
				|| docType2A.equalsIgnoreCase("C")
				|| docType2A.equalsIgnoreCase("RCR"))) {
			obj.setTaxable2A(CheckForNegativeValue(arr[19]));
			obj.setIgst2A(CheckForNegativeValue(arr[21]));
			obj.setCgst2A(CheckForNegativeValue(arr[23]));
			obj.setSgst2A(CheckForNegativeValue(arr[25]));
			obj.setCess2A(CheckForNegativeValue(arr[27]));
			obj.setTotalTax2A(CheckForNegativeValue(arr[29]));
			obj.setInvoiceValue2A(CheckForNegativeValue(arr[31]));
		} else {
			obj.setTaxable2A(arr[19] != null ? arr[19].toString() : null);
			obj.setIgst2A(arr[21] != null ? arr[21].toString() : null);
			obj.setCgst2A(arr[23] != null ? arr[23].toString() : null);
			obj.setSgst2A(arr[25] != null ? arr[25].toString() : null);
			obj.setCess2A(arr[27] != null ? arr[27].toString() : null);
			obj.setTotalTax2A(arr[29] != null ? arr[29].toString() : null);
			obj.setInvoiceValue2A(arr[31] != null ? arr[31].toString() : null);
		}
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setTaxablePR(CheckForNegativeValue(arr[20]));
			obj.setIgstPR(CheckForNegativeValue(arr[22]));
			obj.setCgstPR(CheckForNegativeValue(arr[24]));
			obj.setSgstPR(CheckForNegativeValue(arr[26]));
			obj.setCessPR(CheckForNegativeValue(arr[28]));
			obj.setTotalTaxPR(CheckForNegativeValue(arr[30]));
			obj.setInvoiceValuePR(CheckForNegativeValue(arr[32]));
		} else {
			obj.setTaxablePR(arr[20] != null ? arr[20].toString() : null);
			obj.setIgstPR(arr[22] != null ? arr[22].toString() : null);
			obj.setCgstPR(arr[24] != null ? arr[24].toString() : null);
			obj.setSgstPR(arr[26] != null ? arr[26].toString() : null);
			obj.setCessPR(arr[28] != null ? arr[28].toString() : null);
			obj.setTotalTaxPR(arr[30] != null ? arr[30].toString() : null);
			obj.setInvoiceValuePR(arr[32] != null ? arr[32].toString() : null);
		}
		obj.setPos2A(arr[33] != null
				? isJsonConv ? arr[33].toString() : "'" + arr[33].toString()
				: null);
		obj.setPosPR(arr[34] != null
				? isJsonConv ? arr[34].toString() : "'" + arr[34].toString()
				: null);
		obj.setCfsFlag(arr[35] != null ? arr[35].toString() : null);
		obj.setReverseChargeFlag2A(arr[36] != null ? arr[36].toString() : null);
		obj.setReverseChargeFlagPR(arr[37] != null ? arr[37].toString() : null);
		obj.setOrgdocNumberPR(arr[38] != null ? arr[38].toString() : null);
		obj.setOrgdocNumber2A(arr[39] != null ? arr[39].toString() : null);
		obj.setOrgDatePR(arr[40] != null ? arr[40].toString() : null);
		obj.setOrgDate2A(arr[41] != null ? arr[41].toString() : null);
		obj.setSupplyTypePR(arr[42] != null ? arr[42].toString() : null);
		obj.setOriginalSgstinPR(arr[43] != null ? arr[43].toString() : null);
		obj.setGstr1FilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setGstr1FilingDate(arr[45] != null
				? isJsonConv ? arr[45].toString() : "'" + arr[45].toString()
				: null);
		obj.setGstr1FilingPeriod(arr[46] != null
				? isJsonConv ? arr[46].toString() : "'" + arr[46].toString()
				: null);
		obj.setGstr3bFilingStatus(arr[47] != null ? arr[47].toString() : null);
		obj.setGstr3bFilingDate(arr[48] != null
				? isJsonConv ? arr[48].toString() : "'" + arr[48].toString()
				: null);
		obj.setSuppGstinCancelDate(arr[49] != null
				? isJsonConv ? arr[49].toString() : "'" + arr[49].toString()
				: null);
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
}

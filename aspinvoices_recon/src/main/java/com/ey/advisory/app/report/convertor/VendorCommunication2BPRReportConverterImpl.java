package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.VendorComm2BPRReportGenDto;

@Service("VendorCommunication2BPRReportConverterImpl")
public class VendorCommunication2BPRReportConverterImpl
		implements VendorCommReportConverter {

	@Override
	public Object convert(Object[] arr, boolean isJsonConv) {
		VendorComm2BPRReportGenDto obj = new VendorComm2BPRReportGenDto();
		String docType2B = arr[12] != null ? arr[12].toString() : null;
		String docTypePR = arr[13] != null ? arr[13].toString() : null;
		obj.setMismatchReason(arr[0] != null ? arr[0].toString() : null);
		obj.setReportCategory(arr[1] != null ? arr[1].toString() : null);
		String reportType = arr[2] != null ? arr[2].toString() : null;
		obj.setReportType(reportType);
		obj.setTaxPeriod2B(arr[3] != null
				? isJsonConv ? arr[3].toString() : "'" + arr[3].toString()
				: null);
		obj.setTaxPeriodPR(arr[4] != null
				? isJsonConv ? arr[4].toString() : "'" + arr[4].toString()
				: null);
		obj.setCalenderMonth(arr[5] != null
				? isJsonConv ? arr[5].toString() : "'" + arr[5].toString()
				: null);
		obj.setRecipientGstin2B(arr[6] != null ? arr[6].toString() : null);
		obj.setRecipientGstinPR(arr[7] != null ? arr[7].toString() : null);
		obj.setSupplierGStin2B(arr[8] != null ? arr[8].toString() : null);
		obj.setSupplierGStinPR(arr[9] != null ? arr[9].toString() : null);
		obj.setSupplierTradeName2B(arr[10] != null ? arr[10].toString() : null);
		obj.setSupplierNamePR(arr[11] != null ? arr[11].toString() : null);
		obj.setDocType2B(arr[12] != null ? arr[12].toString() : null);
		obj.setDocTypePR(arr[13] != null ? arr[13].toString() : null);
		obj.setDocumentNumber2B(arr[14] != null
				? isJsonConv ? arr[14].toString() : "'" + arr[14].toString()
				: null);
		obj.setDocumentNumberPR(arr[15] != null
				? isJsonConv ? arr[15].toString() : "'" + arr[15].toString()
				: null);
		obj.setDate2B(arr[16] != null ? arr[16].toString() : null);
		obj.setDatePR(arr[17] != null ? arr[17].toString() : null);
		obj.setPos2B(arr[18] != null
				? isJsonConv ? arr[18].toString() : "'" + arr[18].toString()
				: null);
		obj.setPosPR(arr[19] != null
				? isJsonConv ? arr[19].toString() : "'" + arr[19].toString()
				: null);
		obj.setGSTPercentage2B(arr[20] != null ? arr[20].toString() : null);
		obj.setGSTPercentagePR(arr[21] != null ? arr[21].toString() : null);
		if (docType2B != null && (docType2B.equalsIgnoreCase("CR")
				|| docType2B.equalsIgnoreCase("C")
				|| docType2B.equalsIgnoreCase("RCR"))) {
			obj.setTaxable2B(CheckForNegativeValue(arr[22]));
			obj.setIgst2B(CheckForNegativeValue(arr[24]));
			obj.setCgst2B(CheckForNegativeValue(arr[26]));
			obj.setSgst2B(CheckForNegativeValue(arr[28]));
			obj.setCess2B(CheckForNegativeValue(arr[30]));
			obj.setTotalTax2B(CheckForNegativeValue(arr[32]));
			obj.setInvoiceValue2B(CheckForNegativeValue(arr[34]));
		} else {
			obj.setTaxable2B(arr[22] != null ? arr[22].toString() : null);
			obj.setIgst2B(arr[24] != null ? arr[24].toString() : null);
			obj.setCgst2B(arr[26] != null ? arr[26].toString() : null);
			obj.setSgst2B(arr[28] != null ? arr[28].toString() : null);
			obj.setCess2B(arr[30] != null ? arr[30].toString() : null);
			obj.setTotalTax2B(arr[32] != null ? arr[32].toString() : null);
			obj.setInvoiceValue2B(arr[34] != null ? arr[34].toString() : null);
		}
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setTaxablePR(CheckForNegativeValue(arr[23]));
			obj.setIgstPR(CheckForNegativeValue(arr[25]));
			obj.setCgstPR(CheckForNegativeValue(arr[27]));
			obj.setSgstPR(CheckForNegativeValue(arr[29]));
			obj.setCessPR(CheckForNegativeValue(arr[31]));
			obj.setTotalTaxPR(CheckForNegativeValue(arr[33]));
			obj.setInvoiceValuePR(CheckForNegativeValue(arr[35]));
		} else {
			obj.setTaxablePR(arr[23] != null ? arr[23].toString() : null);
			obj.setIgstPR(arr[25] != null ? arr[25].toString() : null);
			obj.setCgstPR(arr[27] != null ? arr[27].toString() : null);
			obj.setSgstPR(arr[29] != null ? arr[29].toString() : null);
			obj.setCessPR(arr[31] != null ? arr[31].toString() : null);
			obj.setTotalTaxPR(arr[33] != null ? arr[33].toString() : null);
			obj.setInvoiceValuePR(arr[35] != null ? arr[35].toString() : null);
		}
		obj.setReverseChargeFlag2B(arr[36] != null ? arr[36].toString() : null);
		obj.setReverseChargeFlagPR(arr[37] != null ? arr[37].toString() : null);
		obj.setOrgdocNumber2B(arr[38] != null ? arr[38].toString() : null);
		obj.setOrgdocNumberPR(arr[39] != null ? arr[39].toString() : null);
		obj.setOrgDate2B(arr[40] != null ? arr[40].toString() : null);
		obj.setOrgDatePR(arr[41] != null ? arr[41].toString() : null);
		obj.setSupplyTypePR(arr[42] != null ? arr[42].toString() : null);
		obj.setOriginalSgstinPR(arr[43] != null ? arr[43].toString() : null);
		// obj.setGstr1FilingStatus(arr[44] != null ? arr[44].toString() : "");
		obj.setGstr1FilingDate(arr[45] != null
				? isJsonConv ? arr[45].toString() : "'" + arr[45].toString()
				: null);
		obj.setGstr1FilingPeriod(arr[46] != null
				? isJsonConv ? arr[46].toString() : "'" + arr[46].toString()
				: null);
		obj.setGstr3BFilingStatus(arr[47] != null ? arr[47].toString() : null);
		obj.setCancellationDate(arr[48] != null
				? isJsonConv ? arr[48].toString() : "'" + arr[48].toString()
				: null);
		obj.setGstr3BFilingDate(arr[49] != null
				? isJsonConv ? arr[49].toString() : "'" + arr[49].toString()
				: null);
		obj.setItcAvailFlag(arr[50] != null ? arr[50].toString() : null);
		obj.setReasnForItcUnvlbilty(
				arr[51] != null ? arr[51].toString() : null);
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

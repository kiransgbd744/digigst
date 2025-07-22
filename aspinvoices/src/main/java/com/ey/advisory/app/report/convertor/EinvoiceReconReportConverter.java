package com.ey.advisory.app.report.convertor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconErrorReportDto;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportTypeConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Component("EinvoiceReconReportConverter")
@Slf4j
public class EinvoiceReconReportConverter implements ReportConvertor {

	@Override
	public Object convert(Object[] arr, String reportType) {

		Gstr1EinvInitiateReconErrorReportDto obj = new Gstr1EinvInitiateReconErrorReportDto();
		obj.setResponse(arr[0] != null ? arr[0].toString() : null);
		obj.setPreviousResponse(arr[1] != null ? arr[1].toString() : null);
		obj.setRemarks(arr[2] != null ? arr[2].toString() : null);
		obj.setMismatchReason(arr[3] != null ? arr[3].toString() : null);
		obj.setScoreOutof11(arr[4] != null ? arr[4].toString() : null);
		obj.setReportType(arr[5] != null ? arr[5].toString() : null);
		obj.setPreviousReport(arr[6] != null ? arr[6].toString() : null);
		obj.setTaxPeriodGSTN(arr[7] != null && !arr[7].toString().isEmpty()
				? "'" + arr[7].toString() : null);
		obj.setTaxPeriodDigiGST(arr[8] != null && !arr[8].toString().isEmpty()
				? "'" + arr[8].toString() : null);
		obj.setCalenderMonthDigiGST(
				arr[9] != null && !arr[9].toString().isEmpty()
						? "'" + arr[9].toString() : null);
		obj.setSupplierGSTINGSTN(arr[10] != null ? arr[10].toString() : null);
		obj.setSupplierGSTINDigiGST(
				arr[11] != null ? arr[11].toString() : null);
		obj.setRecipientGSTINGSTN(arr[12] != null ? arr[12].toString() : null);
		obj.setRecipientGSTINDigiGST(
				arr[13] != null ? arr[13].toString() : null);
		obj.setRecipientNameGSTN(arr[14] != null ? arr[14].toString() : null);
		obj.setRecipientNameDigiGST(
				arr[15] != null ? arr[15].toString() : null);
		obj.setDocTypeGSTN(arr[16] != null ? arr[16].toString() : null);
		obj.setDocTypeDigiGST(arr[17] != null ? arr[17].toString() : null);
		obj.setSupplyTypeGSTN(arr[18] != null ? arr[18].toString() : null);
		obj.setSupplyTypeDigiGST(arr[19] != null ? arr[19].toString() : null);
		obj.setDocumentNumberGSTN(
				arr[20] != null && !arr[20].toString().isEmpty()
						? "'" + arr[20].toString() : null);
		obj.setDocumentNumberDigiGST(
				arr[21] != null && !arr[21].toString().isEmpty()
						? "'" + arr[21].toString() : null);
		obj.setDocumentDateGSTN(arr[22] != null ? arr[22].toString() : null);
		obj.setDocumentDateDigiGST(arr[23] != null ? arr[23].toString() : null);
		obj.setTaxableValueGSTN(arr[24] != null ? arr[24].toString() : null);
		obj.setTaxableValueDigiGST(arr[25] != null ? arr[25].toString() : null);
		obj.setIgstGSTN(arr[26] != null ? arr[26].toString() : null);
		obj.setIgstDigiGST(arr[27] != null ? arr[27].toString() : null);
		obj.setCgstGSTN(arr[28] != null ? arr[28].toString() : null);
		obj.setCgstDigiGST(arr[29] != null ? arr[29].toString() : null);
		obj.setSgstGSTN(arr[30] != null ? arr[30].toString() : null);
		obj.setSgstDigiGST(arr[31] != null ? arr[31].toString() : null);
		obj.setCessGSTN(arr[32] != null ? arr[32].toString() : null);
		obj.setCessDigiGST(arr[33] != null ? arr[33].toString() : null);
		obj.setTotalTaxGSTN(arr[34] != null ? arr[34].toString() : null);
		obj.setTotalTaxDigiGST(arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceValueGSTN(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceValueDigiGST(arr[37] != null ? arr[37].toString() : null);
		obj.setPosGSTN(arr[38] != null && !arr[38].toString().isEmpty()
				? "'" + arr[38].toString() : null);
		obj.setPosDigiGST(arr[39] != null && !arr[39].toString().isEmpty()
				? "'" + arr[39].toString() : null);
		obj.setReverseChargeFlagGSTN(
				arr[40] != null ? arr[40].toString() : null);
		obj.setReverseChargeFlagDigiGST(
				arr[41] != null ? arr[41].toString() : null);
		obj.setEcomGSTINGSTN(arr[42] != null ? arr[42].toString() : null);
		obj.setEcomGSTINDigiGST(arr[43] != null ? arr[43].toString() : null);
		obj.setPortCodeGSTN(arr[44] != null ? arr[44].toString() : null);
		obj.setPortCodeDigiGST(arr[45] != null ? arr[45].toString() : null);
		obj.setShippingBillNumberGSTN(
				arr[46] != null ? arr[46].toString() : null);
		obj.setShippingBillNumberDigiGST(
				arr[47] != null ? arr[47].toString() : null);
		obj.setShippingBillDateGSTN(
				arr[48] != null ? arr[48].toString() : null);
		obj.setShippingBillDateDigiGST(
				arr[49] != null ? arr[49].toString() : null);
		obj.setSourceTypeGSTN(arr[50] != null ? arr[50].toString() : null);
		obj.setIrnGSTN(arr[51] != null ? arr[51].toString() : null);
		obj.setIrnDigiGST(arr[52] != null ? arr[52].toString() : null);
		obj.setIrnGenDateGSTN(arr[53] != null ? arr[53].toString() : null);
		obj.setIrnGenDateDigiGST(arr[54] != null ? arr[54].toString() : null);
		obj.setEInvoiceStatus(arr[55] != null ? arr[55].toString() : null);
		obj.setAutoDraftstatus(arr[56] != null ? arr[56].toString() : null);
		obj.setAutoDraftedDate(arr[57] != null ? arr[57].toString() : null);
		obj.setErrorCode(arr[58] != null ? arr[58].toString() : null);
		obj.setErrorMessage(arr[59] != null ? arr[59].toString() : null);
		obj.setTableTypeGSTN(arr[60] != null ? arr[60].toString() : null);
		obj.setTableTypeDigiGST(arr[61] != null ? arr[61].toString() : null);
		obj.setCustomerType(arr[62] != null ? arr[62].toString() : null);
		obj.setCustomerCode(arr[63] != null ? arr[63].toString() : null);
		obj.setAccountingVoucherNumber(
				arr[64] != null ? arr[64].toString() : null);
		obj.setAccountingVoucherDate(
				arr[65] != null ? arr[65].toString() : null);
		obj.setCompanyCode(arr[66] != null ? arr[66].toString() : null);
		obj.setRecordStatusDigiGST(arr[67] != null ? arr[67].toString() : null);
		obj.setEInvoiceGetCallDate(arr[68] != null ? arr[68].toString() : null);
		obj.setEInvoiceGetCallTime(arr[69] != null ? arr[69].toString() : null);
		obj.setReconID(arr[70] != null && !arr[70].toString().isEmpty()
				? "'" + arr[70].toString() : null);
		obj.setReconDate(arr[71] != null ? arr[71].toString() : null);
		if (arr[72] != null && !arr[72].toString().isEmpty()) {
			String patternString = "HH:mm:ss";
			String inputString = arr[72].toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("UTC_TIME:", arr[72].toString());
			}
			DateFormat utcFormat = new SimpleDateFormat(patternString);
			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			DateFormat indianFormat = new SimpleDateFormat(patternString);
			indianFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			Date timestamp = null;
			try {
				timestamp = utcFormat.parse(inputString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String time = indianFormat.format(timestamp);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("IST_TIME:", time);
			}
			obj.setReconTime("'" + time);
		} else {
			obj.setReconTime(null);
		}
		obj.setDocHeaderId(arr[73] != null ? arr[73].toString() : null);
		obj.setGetCallId(arr[74] != null ? arr[74].toString() : null);
		obj.setDocKeyDigiGST(arr[75] != null ? arr[75].toString() : null);
		obj.setDocKeyEINV(arr[76] != null ? arr[76].toString() : null);
		obj.setReportCategory(arr[77] != null ? arr[77].toString() : null);

		if (ReportTypeConstants.ERROR.equalsIgnoreCase(reportType)) {
			obj.setErrorId((arr[78] != null) ? arr[78].toString() : null);
			obj.setErrorDescription(
					arr[79] != null ? arr[79].toString() : null);
		}
		obj.setSubCategory(arr[80] != null ? arr[80].toString() : null);
		obj.setReasonForMismatch(arr[81] != null ? arr[81].toString() : null);
		//added 7 columns
		obj.setPlantCode(arr[82] != null ? arr[82].toString() : null);
		obj.setDivision(arr[83] != null ? arr[83].toString() : null);
		obj.setSubDivision(arr[84] != null ? arr[84].toString() : null);
		obj.setLocation(arr[85] != null ? arr[85].toString() : null);
		obj.setProfitCentre1(arr[86] != null ? arr[86].toString() : null);
		obj.setProfitCentre2(arr[87] != null ? arr[87].toString() : null);
		obj.setProfitCentre3(arr[88] != null ? arr[88].toString() : null);
		return obj;
	}
}

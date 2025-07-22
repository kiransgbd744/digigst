package com.ey.advisory.app.report.convertor;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr3b.Gstr3b180DaysReversalRespReportDto;
import com.ey.advisory.common.ReportConvertor;

/**
 * @author Saif.S
 *
 */

@Component("Gstr3B180daysReversalRespReportConvertor")
public class Gstr3B180daysReversalRespReportConvertor
		implements ReportConvertor {

	@Override
	public Object convert(Object[] arr, String reportType) {

		Gstr3b180DaysReversalRespReportDto obj = new Gstr3b180DaysReversalRespReportDto();
		obj.setUserRespTxPrdItcReversal(
				arr[0] != null ? "'" + arr[0].toString() : null);
		obj.setUserRespTxPrdItcReclaim(
				arr[1] != null ? "'" + arr[1].toString() : null);
		obj.setActionType(arr[2] != null ? arr[2].toString() : null);
		obj.setCustomerGstin(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplierGstin(arr[4] != null ? arr[4].toString() : null);
		obj.setSupplierName(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplierCode(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentType(arr[7] != null ? arr[7].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? arr[8].toString() : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		obj.setInvoiceVal(arr[10] != null ? arr[10].toString() : null);
		obj.setStatutoryDednsAplcbl(
				arr[11] != null ? arr[11].toString() : null);
		obj.setStatutoryDednsAmount(
				arr[12] != null ? arr[12].toString() : null);
		obj.setAnyOthDednsAmount(arr[13] != null ? arr[13].toString() : null);
		obj.setRemarksForDedns(arr[14] != null ? arr[14].toString() : null);
		obj.setDueDateOfPayment(arr[15] != null ? arr[15].toString() : null);
		obj.setPaymentRefStatus(arr[16] != null ? arr[16].toString() : null);
		obj.setPaymentRefNumber(arr[17] != null ? arr[17].toString() : null);
		obj.setPaymentRefDate(arr[18] != null ? arr[18].toString() : null);
		obj.setPaymentDesc(arr[19] != null ? arr[19].toString() : null);
		obj.setPaymentStatus(arr[20] != null ? arr[20].toString() : null);
		obj.setPaidAmtToSupp(arr[21] != null ? arr[21].toString() : null);
		obj.setCurrencyCode(arr[22] != null ? arr[22].toString() : null);
		obj.setExchangeRate(arr[23] != null ? arr[23].toString() : null);
		obj.setUnpaidAmtToSupp(arr[24] != null ? arr[24].toString() : null);
		obj.setPostingDate(arr[25] != null ? arr[25].toString() : null);
		obj.setPlantCode(arr[26] != null ? arr[26].toString() : null);
		obj.setProfitCentre(arr[27] != null ? arr[27].toString() : null);
		obj.setDivision(arr[28] != null ? arr[28].toString() : null);
		obj.setUserDefinedField1(arr[29] != null ? arr[29].toString() : null);
		obj.setUserDefinedField2(arr[30] != null ? arr[30].toString() : null);
		obj.setUserDefinedField3(arr[31] != null ? arr[31].toString() : null);
		obj.setDocDate180Days(arr[32] != null ? arr[32].toString() : null);
		obj.setReturnPeriodPr(
				arr[33] != null ? "'" + arr[33].toString() : null);
		obj.setReturnPeriod2aprResponse(
				arr[34] != null ? "'" + arr[34].toString() : null);
		obj.setIgstTaxPaidPr(arr[35] != null ? arr[35].toString() : null);
		obj.setCgstTaxPaidPr(arr[36] != null ? arr[36].toString() : null);
		obj.setSgstTaxPaidPr(arr[37] != null ? arr[37].toString() : null);
		obj.setCessTaxpaidPr(arr[38] != null ? arr[38].toString() : null);
		obj.setAvailableIgstPr(arr[39] != null ? arr[39].toString() : null);
		obj.setAvailableCgstPr(arr[40] != null ? arr[40].toString() : null);
		obj.setAvailableSgstPr(arr[41] != null ? arr[41].toString() : null);
		obj.setAvailableCessPr(arr[42] != null ? arr[42].toString() : null);
		obj.setItcReversalOrReclaimStatus(
				arr[43] != null ? arr[43].toString() : null);
		obj.setItcReversalRetPeriod(
				arr[44] != null ? arr[44].toString() : null);
		obj.setReversalOfIgst(arr[45] != null ? arr[45].toString() : null);
		obj.setReversalOfCgst(arr[46] != null ? arr[46].toString() : null);
		obj.setReversalOfSgst(arr[47] != null ? arr[47].toString() : null);
		obj.setReversalOfCess(arr[48] != null ? arr[48].toString() : null);
		obj.setReclaimRetPeriod(arr[49] != null ? arr[49].toString() : null);
		obj.setReclaimOfIgst(arr[50] != null ? arr[50].toString() : null);
		obj.setReclaimOfCgst(arr[51] != null ? arr[51].toString() : null);
		obj.setReclaimOfSgst(arr[52] != null ? arr[52].toString() : null);
		obj.setReclaimOfCess(arr[53] != null ? arr[53].toString() : null);
		obj.setItcReversalCompDateAndTime(
				arr[54] != null ? "'" + arr[54].toString() : null);
		obj.setItcReversalComputeRequestID(
				arr[55] != null ? "'" + arr[55].toString() : null);
		obj.setReconciliationDateAndTime(
				arr[56] != null ? "'" + arr[56].toString() : null);
		obj.setReconciliationRequestID(
				arr[57] != null ? "'" + arr[57].toString() : null);
		return obj;
	}

}

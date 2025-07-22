package com.ey.advisory.app.recon.type.tagging.report;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GlConsolidatedRptDownloadDto;
import com.ey.advisory.common.ReportConvertor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GlConsolidatedSummaryReportConvertor")
public class GlConsolidatedSummaryReportConvertor
		implements ReportConvertor {

	static final String OLDFARMATTER = "yyyy-MM-dd";
	static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public Object convert(Object[] arr, String reportType) {

		GlConsolidatedRptDownloadDto obj = new GlConsolidatedRptDownloadDto();

		obj.setUserId((arr[0] != null) ? arr[0].toString() : null);
		obj.setFileId((arr[1] != null) ? arr[1].toString() : null);
		obj.setFileName((arr[2] != null) ? arr[2].toString() : null);
		obj.setModeOfDataProcessing(
				(arr[3] != null) ? arr[3].toString() : null);
		obj.setTransactionType((arr[4] != null) ? arr[4].toString() : null);
		obj.setCompanyCode((arr[5] != null) ? arr[5].toString() : null);
		obj.setFiscalYear((arr[6] != null) ? arr[6].toString() : null);
		obj.setPeriodMmYyyy((arr[7] != null) ? arr[7].toString() : null);
		obj.setBusinessPlace((arr[8] != null) ? arr[8].toString() : null);
		obj.setBusinessArea((arr[9] != null) ? arr[9].toString() : null);
		obj.setGlAccount((arr[10] != null) ? arr[10].toString() : null);
		obj.setGlDescription((arr[11] != null) ? arr[11].toString() : null);
		obj.setText((arr[12] != null) ? arr[12].toString() : null);
		obj.setAssignmentNumber((arr[13] != null) ? arr[13].toString() : null);
		obj.setErpDocumentType((arr[14] != null) ? arr[14].toString() : null);
		obj.setAccountingVoucherNumber(
				(arr[15] != null) ? arr[15].toString() : null);
		obj.setAccountingVoucherDate(
				(arr[16] != null) ? arr[16].toString() : null);
		obj.setItemSerialNumber((arr[17] != null) ? arr[17].toString() : null);
		obj.setPostingKey((arr[18] != null) ? arr[18].toString() : null);
		obj.setPostingDate((arr[19] != null) ? arr[19].toString() : null);
		obj.setAmountInLocalCurrency(
				(arr[20] != null) ? arr[20].toString() : null);
		obj.setLocalCurrencyCode((arr[21] != null) ? arr[21].toString() : null);
		obj.setClearingDocumentNumber(
				(arr[22] != null) ? arr[22].toString() : null);
		obj.setClearingDocumentDate(
				(arr[23] != null) ? arr[23].toString() : null);
		obj.setCustomerCode((arr[24] != null) ? arr[24].toString() : null);
		obj.setCustomerName((arr[25] != null) ? arr[25].toString() : null);
		obj.setCustomerGstin((arr[26] != null) ? arr[26].toString() : null);
		obj.setSupplierCode((arr[27] != null) ? arr[27].toString() : null);
		obj.setSupplierName((arr[28] != null) ? arr[28].toString() : null);
		obj.setSupplierGstin((arr[29] != null) ? arr[29].toString() : null);
		obj.setPlantCode((arr[30] != null) ? arr[30].toString() : null);
		obj.setCostCentre((arr[31] != null) ? arr[31].toString() : null);
		obj.setProfitCentre((arr[32] != null) ? arr[32].toString() : null);
		obj.setSpecialGlIndicator(
				(arr[33] != null) ? arr[33].toString() : null);
		obj.setReference((arr[34] != null) ? arr[34].toString() : null);
		obj.setAmountInDocumentCurrency(
				(arr[35] != null) ? arr[35].toString() : null);
		obj.setEffectiveExchangeRate(
				(arr[36] != null) ? arr[36].toString() : null);
		obj.setDocumentCurrencyCode(
				(arr[37] != null) ? arr[37].toString() : null);
		obj.setAccountType((arr[38] != null) ? arr[38].toString() : null);
		obj.setTaxCode((arr[39] != null) ? arr[39].toString() : null);
		obj.setWithholdingTaxAmount(
				(arr[40] != null) ? arr[40].toString() : null);
		obj.setWithholdingExemptAmount(
				(arr[41] != null) ? arr[41].toString() : null);
		obj.setWithholdingTaxBaseAmount(
				(arr[42] != null) ? arr[42].toString() : null);
		obj.setInvoiceReference((arr[43] != null) ? arr[43].toString() : null);
		obj.setDebitCreditIndicator(
				(arr[44] != null) ? arr[44].toString() : null);
		obj.setPaymentDate((arr[45] != null) ? arr[45].toString() : null);
		obj.setPaymentBlock((arr[46] != null) ? arr[46].toString() : null);
		obj.setPaymentReference((arr[47] != null) ? arr[47].toString() : null);
		obj.setTermsOfPayment((arr[48] != null) ? arr[48].toString() : null);
		obj.setMaterial((arr[49] != null) ? arr[49].toString() : null);
		obj.setReferenceKey1((arr[50] != null) ? arr[50].toString() : null);
		obj.setOffsettingAccountType(
				(arr[51] != null) ? arr[51].toString() : null);
		obj.setOffsettingAccountNumber(
				(arr[52] != null) ? arr[52].toString() : null);
		obj.setDocumentHeaderText(
				(arr[53] != null) ? arr[53].toString() : null);
		obj.setBillingDocumentNumber(
				(arr[54] != null) ? arr[54].toString() : null);
		obj.setBillingDocumentDate(
				(arr[55] != null) ? arr[55].toString() : null);
		obj.setMigoNumber((arr[56] != null) ? arr[56].toString() : null);
		obj.setMigoDate((arr[57] != null) ? arr[57].toString() : null);
		obj.setMiroNumber((arr[58] != null) ? arr[58].toString() : null);
		obj.setMiroDate((arr[59] != null) ? arr[59].toString() : null);
		obj.setExpenseGlMapping((arr[60] != null) ? arr[60].toString() : null);
		obj.setSegment((arr[61] != null) ? arr[61].toString() : null);
		obj.setGeoLevel((arr[62] != null) ? arr[62].toString() : null);
		obj.setStateName((arr[63] != null) ? arr[63].toString() : null);
		obj.setParkedBy((arr[64] != null) ? arr[64].toString() : null);
		obj.setEntryDate((arr[65] != null) ? arr[65].toString() : null);
		obj.setTimeOfEntry((arr[66] != null) ? arr[66].toString() : null);
		obj.setRemarks((arr[67] != null) ? arr[67].toString() : null);
		obj.setUserDefinedField1((arr[68] != null) ? arr[68].toString() : null);
		obj.setUserDefinedField2((arr[69] != null) ? arr[69].toString() : null);
		obj.setUserDefinedField3((arr[70] != null) ? arr[70].toString() : null);
		obj.setUserDefinedField4((arr[71] != null) ? arr[71].toString() : null);
		obj.setUserDefinedField5((arr[72] != null) ? arr[72].toString() : null);
		obj.setUserDefinedField6((arr[73] != null) ? arr[73].toString() : null);
		obj.setUserDefinedField7((arr[74] != null) ? arr[74].toString() : null);
		obj.setUserDefinedField8((arr[75] != null) ? arr[75].toString() : null);
		obj.setUserDefinedField9((arr[76] != null) ? arr[76].toString() : null);
		obj.setUserDefinedField10(
				(arr[77] != null) ? arr[77].toString() : null);

		return obj;
	}


}

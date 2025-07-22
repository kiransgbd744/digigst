package com.ey.advisory.app.docs.converters;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.GlDumpDownloadReportDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ReportConvertor;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Component("GLDumpFileStatusReportConverter")
@Slf4j
public class GLDumpFileStatusReportConverter implements ReportConvertor {

	@Override
	public Object convert(Object[] arr, String reportType) {

		GlDumpDownloadReportDto obj = new GlDumpDownloadReportDto();

		obj.setDigigstStatus(arr[0] != null ? arr[0].toString().trim() : null);
		obj.setDigigstErrorDesc(
				arr[1] != null ? arr[1].toString().trim() : null);
		obj.setUserName(arr[2] != null ? arr[2].toString().trim() : null);
		obj.setFileId(arr[3] != null ? arr[3].toString().trim() : null);
		obj.setFileName(arr[4] != null ? arr[4].toString().trim() : null);
		obj.setModeOfDataProcessing(
				arr[5] != null ? arr[5].toString().trim() : null);
		obj.setTransactionType(
				arr[6] != null ? arr[6].toString().trim() : null);
		obj.setCompanyCode(arr[7] != null ? arr[7].toString().trim() : null);
		obj.setFiscalYear(arr[8] != null
				? DownloadReportsConstant.CSVCHARACTER.concat((String) arr[8])
				: null);
		obj.setTaxPeriod(arr[9] != null
				? DownloadReportsConstant.CSVCHARACTER.concat((String) arr[9])
				: null);
		obj.setBusinessPlace(
				arr[10] != null ? arr[10].toString().trim() : null);
		obj.setBusinessArea(arr[11] != null ? arr[11].toString().trim() : null);
		obj.setGlAccount(arr[12] != null ? arr[12].toString().trim() : null);
		obj.setGlDescription(
				arr[13] != null ? arr[13].toString().trim() : null);
		obj.setText(arr[14] != null ? arr[14].toString().trim() : null);
		obj.setAssignmentNumber(
				arr[15] != null ? arr[15].toString().trim() : null);
		obj.setErpDocumentType(
				arr[16] != null ? arr[16].toString().trim() : null);
		obj.setAccountingVoucherNumber(
				arr[17] != null ? arr[17].toString().trim() : null);
		obj.setAccountingVoucherDate(
				arr[18] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[18].toString().trim()) : null);
		obj.setItemSerialNumber(arr[19] != null
				? DownloadReportsConstant.CSVCHARACTER.concat((String) arr[19])
				: null);
		obj.setPostingKey(arr[20] != null ? arr[20].toString().trim() : null);
		obj.setPostingDate(
				arr[21] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[21].toString().trim()) : null);
		obj.setAmountInLocalCurrency(arr[22] != null
				? DownloadReportsConstant.CSVCHARACTER.concat((String) arr[22])
				: null);
		obj.setLocalCurrencyCode(
				arr[23] != null ? arr[23].toString().trim() : null);
		obj.setClearingDocumentNumber(
				arr[24] != null ? arr[24].toString().trim() : null);
		obj.setClearingDocumentDate(
				arr[25] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[25].toString().trim()) : null);
		obj.setCustomerCode(arr[26] != null ? arr[26].toString().trim() : null);
		obj.setCustomerName(arr[27] != null ? arr[27].toString().trim() : null);
		obj.setCustomerGstin(
				arr[28] != null ? arr[28].toString().trim() : null);
		obj.setSupplierCode(arr[29] != null ? arr[29].toString().trim() : null);
		obj.setSupplierName(arr[30] != null ? arr[30].toString().trim() : null);
		obj.setSupplierGstin(
				arr[31] != null ? arr[31].toString().trim() : null);
		obj.setPlantCode(arr[32] != null ? arr[32].toString().trim() : null);
		obj.setCostCentre(arr[33] != null ? arr[33].toString().trim() : null);
		obj.setProfitCentre(arr[34] != null ? arr[34].toString().trim() : null);
		obj.setSpecialGLIndicator(
				arr[35] != null ? arr[35].toString().trim() : null);
		obj.setReference(arr[36] != null ? arr[36].toString().trim() : null);
		obj.setAmountinDocumentCurrency(arr[37] != null
				? DownloadReportsConstant.CSVCHARACTER.concat((String) arr[37])
				: null);
		obj.setEffectiveExchangeRate(
				arr[38] != null ? arr[38].toString().trim() : null);
		obj.setDocumentCurrencyCode(
				arr[39] != null ? arr[39].toString().trim() : null);
		obj.setAccountType(arr[40] != null ? arr[40].toString().trim() : null);
		obj.setTaxCode(arr[41] != null ? arr[41].toString().trim() : null);
		obj.setWithholdingTaxAmount(
				arr[42] != null ? arr[42].toString().trim() : null);
		obj.setWithholdingExemptAmount(
				arr[43] != null ? arr[43].toString().trim() : null);
		obj.setWithholdingTaxBaseAmount(
				arr[44] != null ? arr[44].toString().trim() : null);
		obj.setInvoiceReference(
				arr[45] != null ? arr[45].toString().trim() : null);
		obj.setDebitCreditIndicator(
				arr[46] != null ? arr[46].toString().trim() : null);
		obj.setPaymentDate(
				arr[47] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[47].toString().trim()) : null);
		obj.setPaymentBlock(arr[48] != null ? arr[48].toString().trim() : null);
		obj.setPaymentReference(
				arr[49] != null ? arr[49].toString().trim() : null);
		obj.setTermsofPayment(
				arr[50] != null ? arr[50].toString().trim() : null);
		obj.setMaterial(arr[51] != null ? arr[51].toString().trim() : null);
		obj.setReferenceKey1(
				arr[52] != null ? arr[52].toString().trim() : null);
		obj.setOffsettingAccountType(
				arr[53] != null ? arr[53].toString().trim() : null);
		obj.setOffsettingAccountNumber(
				arr[54] != null ? arr[54].toString().trim() : null);
		obj.setDocumentHeaderText(
				arr[55] != null ? arr[55].toString().trim() : null);
		obj.setBillingDocumentNumber(
				arr[56] != null ? arr[56].toString().trim() : null);
		obj.setBillingDocumentDate(
				arr[57] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[57].toString().trim()) : null);
		obj.setMigoNumber(arr[58] != null ? arr[58].toString().trim() : null);
		obj.setMigoDate(arr[59] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[59].toString().trim()) : null);
		obj.setMiroNumber(arr[60] != null ? arr[60].toString().trim() : null);
		obj.setMiroDate(arr[61] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[61].toString().trim()) : null);
		obj.setExpenseGLMapping(
				arr[62] != null ? arr[62].toString().trim() : null);
		obj.setSegment(arr[63] != null ? arr[63].toString().trim() : null);
		obj.setGeoLevel(arr[64] != null ? arr[64].toString().trim() : null);
		obj.setStateName(arr[65] != null ? arr[65].toString().trim() : null);
		obj.setUserID(arr[66] != null ? arr[66].toString().trim() : null);
		obj.setParkedBy(arr[67] != null ? arr[67].toString().trim() : null);
		obj.setEntryDate(arr[68] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[68].toString().trim()) : null);
		obj.setTimeofEntry(arr[69] != null ? arr[69].toString().trim() : null);
		obj.setRemarks(arr[70] != null ? arr[70].toString().trim() : null);
		obj.setUserDefinedField1(
				arr[71] != null ? arr[71].toString().trim() : null);
		obj.setUserDefinedField2(
				arr[72] != null ? arr[72].toString().trim() : null);
		obj.setUserDefinedField3(
				arr[73] != null ? arr[73].toString().trim() : null);
		obj.setUserDefinedField4(
				arr[74] != null ? arr[74].toString().trim() : null);
		obj.setUserDefinedField5(
				arr[75] != null ? arr[75].toString().trim() : null);
		obj.setUserDefinedField6(
				arr[76] != null ? arr[76].toString().trim() : null);
		obj.setUserDefinedField7(
				arr[77] != null ? arr[77].toString().trim() : null);
		obj.setUserDefinedField8(
				arr[78] != null ? arr[78].toString().trim() : null);
		obj.setUserDefinedField9(
				arr[79] != null ? arr[79].toString().trim() : null);
		obj.setUserDefinedField10(
				arr[80] != null ? arr[80].toString().trim() : null);

		return obj;

	}

	private String removeQuotes(String data) {
		if (Strings.isNullOrEmpty(data)) {
			return null;
		}
		if (data.contains("'")) {
			return data.replace("'", "");
		}
		if (data.contains("`")) {
			return data.replace("`", "");
		}

		return data;

	}
}

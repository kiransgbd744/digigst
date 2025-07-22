package com.ey.advisory.app.service.ims.supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor	
public class DetailedSupplierImsSummaryReportDto {
	
	private String gstin;
	private String returnPeriod;
	private String tableType;
	
	private String totalRecordsCount;
	private String totalRecordsTotalTaxableVal;
	private String totalRecordsTotalTax;
	private String totalRecordsIgst;
	private String totalRecordsCgst;
	private String totalRecordsSgst;
	private String totalRecordsCess;
	
	private String acceptedCount;
	private String acceptedTotalTaxableVal;
	private String acceptedTotalTax;
	private String acceptedIgst;
	private String acceptedCgst;
	private String acceptedSgst;
	private String acceptedCess;
	
	private String pendingCount;
	private String pendingTotalTaxableVal;
	private String pendingTotalTax;
	private String pendingIgst;
	private String pendingCgst;
	private String pendingSgst;
	private String pendingCess;
	
	private String rejectedCount;
	private String rejectedTotalTaxableVal;
	private String rejectedTotalTax;
	private String rejectedIgst;
	private String rejectedCgst;
	private String rejectedSgst;
	private String rejectedCess;
	
	private String noActionCount;
	private String noActionTotalTaxableVal;
	private String noActionTotalTax;
	private String noActionIgst;
	private String noActionCgst;
	private String noActionSgst;
	private String noActionCess;
	
	private String gstr1Count;
	private String gstr1TotalTaxableVal;
	private String gstr1TotalTax;
	private String gstr1Igst;
	private String gstr1Cgst;
	private String gstr1Sgst;
	private String gstr1Cess;
	
	private String gstr1aCount;
	private String gstr1aTotalTaxableVal;
	private String gstr1aTotalTax;
	private String gstr1aIgst;
	private String gstr1aCgst;
	private String gstr1aSgst;
	private String gstr1aCess;
	
	private String diffCount;
	private String diffTotalTaxableVal;
	private String diffTotalTax;
	private String diffIgst;
	private String diffCgst;
	private String diffSgst;
	private String diffCess;

}


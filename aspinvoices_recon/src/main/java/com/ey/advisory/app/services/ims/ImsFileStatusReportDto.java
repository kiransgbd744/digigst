package com.ey.advisory.app.services.ims;

import lombok.Data;

@Data
public class ImsFileStatusReportDto {

	private String actionResponse;

	private String responseRemarks;

	private String gstnAction;

	private String digiGstAction;

	private String digiGstActionDateTime;

	private String savedToGstn;
	
	private String availableInImsGstn;

	private String tableType;
	
	private String recipientGstin;

	private String supplierGstin;

	private String supplierLegalName;

	private String supplierTradeName;

	private String documentType;

	private String documentNumber;

	private String documentDate;

	private String taxableValue;
	
	private String igst;

	private String cgst;

	private String sgst;
	
	private String cess;
	
	private String totalTax;

	private String invoiceValue;
	
	private String pos;

	private String formType;

	private String gstr1FilingStatus;

	private String gstr1FilingPeriod;

	private String originalDocNo;

	private String originalDocDate;

	private String pendingActionBlocked;

	private String checkSum;

	private String getCallDateTime;

	private String imsUniqueId;
	
	private String itcRedReq;

	private String declIgst;

	private String declSgst;

	private String declCgst;

	private String declCess;

}

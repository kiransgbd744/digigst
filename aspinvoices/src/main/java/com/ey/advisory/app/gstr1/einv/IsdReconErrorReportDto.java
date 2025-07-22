package com.ey.advisory.app.gstr1.einv;

import lombok.Data;

@Data
public class IsdReconErrorReportDto{
	
	private String errorCode;
	private String errorMessage;
	private String sourceType;
	private String isdgstin;
	private String supplierGstin;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String itemSerialNumber;
	private String gstinforDistribution;
	private String createdOn;
	private String actionType;

}

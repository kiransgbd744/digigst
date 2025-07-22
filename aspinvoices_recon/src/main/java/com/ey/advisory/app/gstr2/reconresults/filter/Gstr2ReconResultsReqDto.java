package com.ey.advisory.app.gstr2.reconresults.filter;

import java.util.List;

import lombok.Data;

/**
* @author Sakshi.jain
*
*/

@Data
public class Gstr2ReconResultsReqDto {
	
	private String entityId;
	private String responseRemarks;
	private String taxPeriodGstr3b;
	private String avaiIgst;
	private String avaiCgst;
	private String avaiSgst;
	private String avaiCess;
	private String reconType;
	private List<String> gstins;
	private String reportType;
	private String fromTaxPeriod;
	private String toTaxPeriod;
	private List<Long> reconLinkId;
	private String indentifier;
	private String itcReversal;
	private String reconCriteria;
	private String imsUserResponse;
	private String imsResponseRemarks;
	private List<String> vendorPans;
	private List<String> vendorGstins;
	private String taxPeriodBase;
	private List<String> reportTypes;
	private List<String> docType;

}

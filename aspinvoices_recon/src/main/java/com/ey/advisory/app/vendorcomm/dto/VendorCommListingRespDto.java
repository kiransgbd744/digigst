package com.ey.advisory.app.vendorcomm.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VendorCommListingRespDto {

	
	private Long requestId;
	private Long noOfRecipientGstins;
	private Long noOfVendorGstins;
	private Long noOfReportTypes;
	private String createdOn;
	private String status;
	private long totalEmails;
	private long sentEmails;
	private String taxPeriod;
	private String reconType;
	private List<GstinDto> reportTypes;
	private List<GstinDto> recipientGstins;
	private long vendrResponded;
}

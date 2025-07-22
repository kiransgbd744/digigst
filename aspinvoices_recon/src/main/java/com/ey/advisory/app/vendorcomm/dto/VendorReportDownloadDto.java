package com.ey.advisory.app.vendorcomm.dto;

import java.util.List;

import lombok.Data;

@Data
public class VendorReportDownloadDto {

	private List<GstinDto> recipientGstins;
	private List<GstinDto> vendorGstins;
	private List<ReportTypesDto> reportTypes;
	private String fromTaxPeriod;
	private String toTaxPeriod;
	private String entityId;
	private String reconType;
	private String respDate;
	private List<Boolean> status;
	private String identifier;
	private String reqId;
	private List<GstinDto> vendorPans;

}

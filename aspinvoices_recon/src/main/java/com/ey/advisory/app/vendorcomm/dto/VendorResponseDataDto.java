package com.ey.advisory.app.vendorcomm.dto;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class VendorResponseDataDto {
	
	@Expose
	private String date;
	@Expose
	private String vendorPan;
	@Expose
	private String vendorGstin;
	@Expose
	private String vendorName;
	@Expose
	private String reqId;
	@Expose
	private String totalRec;
	@Expose
	private String taxPeriod;
	@Expose
	private String respRecords;
	@Expose
	private boolean isRespDownld;
	@Expose
	private boolean isTotRespDownld;
	
	@Expose
	private String respDowldPath;
	@Expose
	private String totalRespDowldPath;
	@Expose
	private String status;
	
	
}

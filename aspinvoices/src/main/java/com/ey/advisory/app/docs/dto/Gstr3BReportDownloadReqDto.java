package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Rajesh N K
 *
 */
@Getter
@Setter
public class Gstr3BReportDownloadReqDto {
	
	@Expose
	@SerializedName("gstins")
	private String gstins;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	@SerializedName("reportType")
	private String reportType;

}

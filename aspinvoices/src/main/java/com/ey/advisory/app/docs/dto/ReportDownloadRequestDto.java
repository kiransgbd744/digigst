package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sakshi.jain
 *
 */
@Getter
@Setter
public class ReportDownloadRequestDto {
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("gstins")
	private List<String> gstins ;
	
	@Expose
	@SerializedName("fromdate")
	private String fromdate;
	
	@Expose
	@SerializedName("toDate")
	private String toDate;
	
	@Expose
	@SerializedName("criteria")
	private String criteria;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
}

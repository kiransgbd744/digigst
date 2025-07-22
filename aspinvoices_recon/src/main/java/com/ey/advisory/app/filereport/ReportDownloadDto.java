package com.ey.advisory.app.filereport;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDownloadDto {
	
	@Expose
	private String reportType;
	
	@Expose
	private String reportCateg;
	
	@Expose
	private boolean isDwnld;
	
	@Expose
	@SerializedName("requestId")
	private Long configId;
	
	public ReportDownloadDto() { }

}

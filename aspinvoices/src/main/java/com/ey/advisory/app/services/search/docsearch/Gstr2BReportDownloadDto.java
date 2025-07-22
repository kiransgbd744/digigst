package com.ey.advisory.app.services.search.docsearch;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gstr2BReportDownloadDto {
	
	@Expose
	private String reportType;
	
	@Expose
	private String reportCateg;
	
	@Expose
	private boolean isDwnld;
	
	@Expose
	@SerializedName("requestId")
	private Long configId;
	
	public Gstr2BReportDownloadDto() { }

}

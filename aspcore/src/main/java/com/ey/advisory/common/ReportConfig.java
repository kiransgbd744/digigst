package com.ey.advisory.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportConfig {

	private String headersKey;
	
	private String columnMappingsKey;
	
	private ReportConvertor reportConvertor;
	
	public ReportConfig(){}


	public ReportConfig(String headersKey, String columnMappingsKey,
			 ReportConvertor reportConvertor) {
		super();
		this.headersKey = headersKey;
		this.columnMappingsKey = columnMappingsKey;
		this.reportConvertor = reportConvertor;
	}
	
	
}

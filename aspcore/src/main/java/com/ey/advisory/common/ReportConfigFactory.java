package com.ey.advisory.common;

public interface ReportConfigFactory {
	
	public ReportConfig getReportConfig(String reportType, 
			String reportCateg, String dataType);

}

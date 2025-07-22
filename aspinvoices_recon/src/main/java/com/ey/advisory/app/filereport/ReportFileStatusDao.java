package com.ey.advisory.app.filereport;

import java.util.List;

public interface ReportFileStatusDao {
	
	public List<ReportFileStatusReportDto> getFileStatusReportDetails(String userName);

}

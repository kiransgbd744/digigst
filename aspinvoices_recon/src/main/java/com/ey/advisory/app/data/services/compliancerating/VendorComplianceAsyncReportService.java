package com.ey.advisory.app.data.services.compliancerating;

public interface VendorComplianceAsyncReportService {

	public void generateReports(Long id, String source, Long entityId);
}

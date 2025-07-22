package com.ey.advisory.service.vendor.compliance;

public interface VendorComplianceReportDownloadService {

	public void getData(Long batchId, String reportType,String financialYear);

}

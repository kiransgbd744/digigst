package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aspose.cells.Workbook;

public interface NonComplaintVendorCommunicationService {

	public void persistGstnApiForSelectedFinancialYear(String finYear,
			List<String> vendorGstins,String complianceType);

	Workbook getNonComplaintVendorReport(String financialYear,
			String reportType, Long entityId,File tempDir,Long batchId);

	Workbook getNonComplaintVendorTableReport(String financialYear,
			String returnType, String viewType, Long entityId);

	public OverAllFilingStatusWithFinancialYearInfo getOverallReturnFilingStatus(
			String financialYear, String returnType, String viewType,
			Long entityId, List<String> vendorPans, List<String> vendorGstins);

	Map<String, Set<String>> getNonComplaintComVendorGstins(
			String financialYear, List<String> vendorGstinsList, Long entityId,
			String reportType);

}

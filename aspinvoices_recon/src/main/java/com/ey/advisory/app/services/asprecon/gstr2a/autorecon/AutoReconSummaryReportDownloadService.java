package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.ArrayList;

import com.aspose.cells.Workbook;

public interface AutoReconSummaryReportDownloadService {

	public Workbook getReconSummaryReport(ArrayList<String> recipientGstins,
			String fromTaxPeriodPR, String toTaxPeriodPR,
			String fromTaxPeriod2A, String toTaxPeriod2A, String fromReconDate,
			String toReconDate, Long entityId, String criteria);

}

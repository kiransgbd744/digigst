package com.ey.advisory.app.data.services.ewb;

import java.io.File;
import java.util.List;

public interface EwbPdfReportService {

	String generateEwbDetailedPdfZip(File tmpDir, List<String> ewbNo);
	
	String generateEwbSummaryPdfZip(File tmpDir, List<String> ewbNo);
}

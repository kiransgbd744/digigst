package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.util.List;

public interface Gstr2NonApManualGenerateReportService {

	void generateReport(Long requestId, List<String> reportList) throws Exception;

}

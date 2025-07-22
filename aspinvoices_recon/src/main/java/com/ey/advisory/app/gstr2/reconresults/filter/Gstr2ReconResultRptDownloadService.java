package com.ey.advisory.app.gstr2.reconresults.filter;

import java.io.IOException;

public interface Gstr2ReconResultRptDownloadService {

	
	public String generateReconResultReport(Long requestId) throws IOException;

}

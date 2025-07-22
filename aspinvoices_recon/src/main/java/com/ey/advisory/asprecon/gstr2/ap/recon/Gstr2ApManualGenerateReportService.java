package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.util.List;

public interface Gstr2ApManualGenerateReportService {
	
	public void generateReport(Long configId,  Long entityId, 
			List<String> addnReportList) throws Exception;

}

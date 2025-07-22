package com.ey.advisory.app.asprecon.gstr2.pr2b.reports;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */
public interface Gstr2InitiateRecon2BPRFetchReportDetails {

	public void get2BPRReconReportData(Long configId, List<String> reportList) 
			throws IOException;

}

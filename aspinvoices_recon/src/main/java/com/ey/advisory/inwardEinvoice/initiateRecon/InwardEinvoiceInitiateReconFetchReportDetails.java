package com.ey.advisory.inwardEinvoice.initiateRecon;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */
public interface InwardEinvoiceInitiateReconFetchReportDetails {

	public void getInwardEinvoiceReconReportData(Long configId, List<String> reportList) 
			throws IOException;

}

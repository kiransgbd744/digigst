package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.io.IOException;

/**
 * 
 * @author vishal.verma
 *
 */
public interface Gstr2InitiateReconFetchReportDetails {

	public void getReconReportData(Long configId) throws IOException;

}

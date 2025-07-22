package com.ey.advisory.app.anx2.initiaterecon;

import java.io.IOException;

/**
 * @author Arun.KA
 *
 */
public interface InitiateReconFetchReportDetails {
	
	public String getReconReportData(Long configId) throws IOException;

}

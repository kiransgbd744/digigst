package com.ey.advisory.app.gstr1.einv;

import java.io.IOException;

/**
 * @author vishal.verma
 *
 */
public interface Gstr1EinvInitiateReconFetchReportDetails {
	
	public void getReconReportData(Long configId) throws IOException;

}

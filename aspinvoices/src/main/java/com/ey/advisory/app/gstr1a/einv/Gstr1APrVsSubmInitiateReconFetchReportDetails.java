package com.ey.advisory.app.gstr1a.einv;

import java.io.IOException;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1APrVsSubmInitiateReconFetchReportDetails {

	public void getPrVSSubmiReconReportData(Long configId) throws IOException;

}

package com.ey.advisory.app.asprecon.gstr2.pr2b.reports;

import java.io.IOException;
import java.util.List;

import com.ey.advisory.app.recon3way.EWB3WayProcedureListDto;

/**
 * 
 * @author vishal.verma
 *
 */
public interface EWB3WayInitiateReconFetchReportDetails {

	public void getInitiateReconReportData(Long configId) throws IOException;

}

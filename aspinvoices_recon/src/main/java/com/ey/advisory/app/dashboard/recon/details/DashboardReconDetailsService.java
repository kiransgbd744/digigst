package com.ey.advisory.app.dashboard.recon.details;

import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */
public interface DashboardReconDetailsService {

	public DashboardReconDetailsDto getDashboardReconDetails(
			Long entityId, String taxPeriod);

}

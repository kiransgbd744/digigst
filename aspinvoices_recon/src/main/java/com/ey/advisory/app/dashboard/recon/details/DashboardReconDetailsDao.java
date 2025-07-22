package com.ey.advisory.app.dashboard.recon.details;

import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */
public interface DashboardReconDetailsDao {
	
	public List<DbResponseDto> getReconDetails(Long entityId,
			String taxPeriod);
	
	public List<DbResponseDto> getRespDetails(Long entityId,
			String taxPeriod);

}

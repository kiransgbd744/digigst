package com.ey.advisory.app.dashboard.homeOld;

import java.util.List;

/**
 * 
 * @author mohit.basak
 *
 */
public interface DashboardHODao {

	public List<DashboardHOReturnStatusDto> getDashBoardReturnStatus(
			Long entityId, String taxPeriod);

	public List<DashboardHOReturnComplianceStatusDto> 
	getDashBoardReturnComplianceStatus(
			Long entityId, String taxPeriod, List<String> gstins);

	public DashboardHOOutwardSupplyDto getDashBoardOutwardStatus(
			Long entityId, String taxPeriod, List<String> gstins);

	public DashboardHOReconSummaryDto getDashBoardReconSummary(Long entityId,
			String taxPeriod);
	
	public DashboardHOReconSummary2bprDto getDashBoardReconSummary2bpr(Long entityId,
			String taxPeriod);

}

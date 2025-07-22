package com.ey.advisory.app.dashboard.homeOld;

/**
 * 
 * @author mohit.basak
 *
 */
public interface DashboardHOService {

	public DashboardHOReturnsStatusUIDto getDashBoardReturnStatus
	(Long entityId, String taxPeriod);

	public DashboardHOReturnComplinceWithAuthCountDto 
		getDashBoardReturnComplianceStatus(Long entityId, String taxPeriod);

	public DashboardHOOutwardSupplyUIDto getDashBoardOutwardStatus(
			Long entityId, String taxPeriod);

	public DashboardHOReconSummaryDto getDashBoardReconSummary(Long entityId,
			String taxPeriod);
	
	public DashboardHOReconSummary2bprDto getDashBoardReconSummary2bpr(Long entityId,
			String taxPeriod);

}

/**
 * 
 */
package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface InitiateReconReportRequestStatusService{
	
	public List<InitiateReconReportRequestStatusDto> getReportRequestStatus(
			String userName);

}

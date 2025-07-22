package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

/**
 * @author vishal.verma
 *
 */

public interface InitiateReconReportRequestStatusDao {

	public List<InitiateReconReportRequestStatusDto> getReportRequestStatus(
			String userName);
}

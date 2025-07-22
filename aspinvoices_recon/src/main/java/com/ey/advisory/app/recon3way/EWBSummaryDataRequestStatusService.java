package com.ey.advisory.app.recon3way;

import java.util.List;

import com.ey.advisory.gstr2.initiaterecon.EWBSummaryDataRequestStatusDto;

/**
 * @author Sakshi.jain
 *
 */
public interface EWBSummaryDataRequestStatusService {

	List<EWBSummaryDataRequestStatusDto> getRequestDataStatus(
			List<String> gstinlist, String criteria, String fromDate,
			String toDate, Long entityId);

}

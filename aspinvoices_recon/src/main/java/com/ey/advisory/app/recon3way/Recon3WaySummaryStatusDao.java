/**
 * 
 */
package com.ey.advisory.app.recon3way;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

/**
 * @author Sakshi.jain
 *
 */
public interface Recon3WaySummaryStatusDao {

	public List<Gstr2ReconSummaryStatusDto> find3WayReconSummStatus(
			Long entityId, String fromReturnPeriod, String toReturnPeriod,
			String criteria);

}

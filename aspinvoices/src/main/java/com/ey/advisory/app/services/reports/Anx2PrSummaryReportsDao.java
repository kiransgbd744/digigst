/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.PRSummaryProcessedRecordsDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Anx2PrSummaryReportsDao {

	/*List<PRSummaryProcessedRecordsDto> getProcessedReports(
			SearchCriteria criteria);
	*/
	public List<Object> getProcessedReports(SearchCriteria criteria);

}

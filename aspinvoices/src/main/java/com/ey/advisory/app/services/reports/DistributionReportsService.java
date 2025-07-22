/**
 * 
 */
package com.ey.advisory.app.services.reports;

import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface DistributionReportsService {

	public void generateFileStatusCsv(SearchCriteria criteria,
			PageRequest pageReq, String fullPath);

}

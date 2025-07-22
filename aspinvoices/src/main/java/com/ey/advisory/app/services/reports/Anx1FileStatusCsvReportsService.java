/**
 * 
 */
package com.ey.advisory.app.services.reports;

import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Anx1FileStatusCsvReportsService {

	public void generateFileStatusCsv(SearchCriteria criteria,
			PageRequest pageReq, String fullPath);

}

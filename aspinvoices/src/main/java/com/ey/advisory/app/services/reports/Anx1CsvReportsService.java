/**
 * 
 */
package com.ey.advisory.app.services.reports;

/**
 * @author Laxmi.Salukuti
 *
 */

import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface Anx1CsvReportsService {

	public void generateCsvForCriteira(SearchCriteria criteria,
			PageRequest pageReq, String fullPath);


}

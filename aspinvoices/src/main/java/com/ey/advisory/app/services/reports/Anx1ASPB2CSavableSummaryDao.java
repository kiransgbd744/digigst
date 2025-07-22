/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Anx1ASPB2CSavableSummaryDao {
	
	List<Object> getAnx1B2CSavableReports(SearchCriteria criteria);

	}




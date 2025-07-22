package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

public interface Gstr1OutwardVerticalProcessAdvAdjDao {
	
	List<Object> getGstr1RSReports(SearchCriteria criteria);

}

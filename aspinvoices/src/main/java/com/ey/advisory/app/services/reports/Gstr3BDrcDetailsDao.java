package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Harsh
 *
 * 
 */
public interface Gstr3BDrcDetailsDao {
	
	//List<Object> getDrcReports(SearchCriteria criteria);

	List<Object[]> getDrcReports(String entityName, String gstin,
			String taxPeriod, String refId, SearchCriteria criteria);

}

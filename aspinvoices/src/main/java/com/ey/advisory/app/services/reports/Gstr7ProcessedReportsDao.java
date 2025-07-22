package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

public interface Gstr7ProcessedReportsDao {
	
	List<Object> getGstr7RSReports(SearchCriteria criteria);

	}




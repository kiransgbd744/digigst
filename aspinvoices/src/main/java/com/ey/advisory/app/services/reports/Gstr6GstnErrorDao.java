package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

public interface Gstr6GstnErrorDao {
	
	List<Object> getGstr6GstnErrorReport(SearchCriteria criteria);

	}


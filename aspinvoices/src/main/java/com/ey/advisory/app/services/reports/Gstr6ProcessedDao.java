package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

public interface Gstr6ProcessedDao {
	
	List<Object> getGstr6Reports(SearchCriteria criteria);

	}



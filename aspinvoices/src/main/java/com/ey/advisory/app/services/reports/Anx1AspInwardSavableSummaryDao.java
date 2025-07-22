package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

public interface Anx1AspInwardSavableSummaryDao {
	List<Object> getAnx1HsnInwardSavableReports(SearchCriteria criteria);
}

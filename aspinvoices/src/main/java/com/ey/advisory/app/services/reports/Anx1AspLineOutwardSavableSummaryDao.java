package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

public interface Anx1AspLineOutwardSavableSummaryDao {

	List<Object> getLineOutwardSavableReports(SearchCriteria criteria);

}

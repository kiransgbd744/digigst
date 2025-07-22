package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

public interface Gstr1ASPAdvAdjSavableSummaryLevelDao {
	
	List<Object> getGstr1AdjSavableReports(SearchCriteria criteria);

	
	}



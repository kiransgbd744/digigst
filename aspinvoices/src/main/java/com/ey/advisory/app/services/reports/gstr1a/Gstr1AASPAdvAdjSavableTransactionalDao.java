package com.ey.advisory.app.services.reports.gstr1a;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Shashikant.Shukla
 *
 * 
 */

public interface Gstr1AASPAdvAdjSavableTransactionalDao {
	
	List<Object> getGstr1AdjSavableReports(SearchCriteria criteria);

	
	}



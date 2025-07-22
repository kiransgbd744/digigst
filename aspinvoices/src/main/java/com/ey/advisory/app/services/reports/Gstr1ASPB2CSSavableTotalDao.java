package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

public interface Gstr1ASPB2CSSavableTotalDao {
	
	List<Object> getGstr1B2CSSavableReports(SearchCriteria criteria);

	
	}



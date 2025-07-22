package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

public interface Gstr1OutwardVerticalProcessInvDao {

	List<Object> getGstr1RSReports(SearchCriteria criteria);

}

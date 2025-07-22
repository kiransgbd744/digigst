/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr1ReviewSummaryReportsDao {

	List<Object> getGstr1RSReports(SearchCriteria criteria);

}

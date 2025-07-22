/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Ret1ProcessedScreenDao {

	List<Object> getRet1ProcessedScrrenReports(SearchCriteria criteria);

}

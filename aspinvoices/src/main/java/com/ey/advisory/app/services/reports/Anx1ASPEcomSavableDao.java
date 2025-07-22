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
public interface Anx1ASPEcomSavableDao {

	List<Object> getAnx1EcomSavableReports(SearchCriteria criteria);

}

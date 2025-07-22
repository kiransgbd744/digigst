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
public interface Gstr1AdvRecSavableDao {
	
	List<Object> getGstr1AdvRecSavableReports(SearchCriteria criteria);

}

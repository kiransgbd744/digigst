/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Mahesh.Golla
 *
 * 
 */
public interface Gstr6DeterminationDao {

	List<Object> gstr6DeterminationReports(SearchCriteria criteria);

}

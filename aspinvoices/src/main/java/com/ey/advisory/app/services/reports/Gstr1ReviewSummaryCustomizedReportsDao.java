/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author vishal.verma
 *
 */
public interface Gstr1ReviewSummaryCustomizedReportsDao {

	Pair<List<Object[]>, List<Object>> getGstr1RSReports(
			SearchCriteria criteria);

	

}

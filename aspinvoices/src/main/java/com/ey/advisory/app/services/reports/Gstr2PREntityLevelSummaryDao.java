package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Gstr2PREntityLevelSummaryDao {

	List<Object> getEntityLevelSummary(SearchCriteria criteria);
}

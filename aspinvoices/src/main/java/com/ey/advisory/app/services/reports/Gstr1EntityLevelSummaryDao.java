/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.GSTR1EntityLevelSummaryDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Gstr1EntityLevelSummaryDao {

	List<GSTR1EntityLevelSummaryDto> getEntityLevelSummary(SearchCriteria criteria);

}

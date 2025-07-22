/**
 * 
 */
package com.ey.advisory.app.data.services.Gstr1A;

import java.util.List;

import com.ey.advisory.app.data.views.client.GSTR1GSTNTransactionalLevelTablesDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Gstr1AaGetDao {

	List<Object> getGstnConsolidatedReports(
			SearchCriteria criteria);

}

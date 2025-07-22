
package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

public interface Gstr1ASPB2CSSavableService {

	public Workbook findGstr1B2CSSavableReports(SearchCriteria criteria,
			PageRequest pageReq);

}

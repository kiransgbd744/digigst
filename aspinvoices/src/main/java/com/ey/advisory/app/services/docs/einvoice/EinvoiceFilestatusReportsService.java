/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface EinvoiceFilestatusReportsService {

	public Workbook findProcesseRec(SearchCriteria criteria,
			PageRequest pageReq);

}

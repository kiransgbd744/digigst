/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Arun KA
 *
 */
public interface Anx1AsyncFileStatusCsvReportsDao {

	List<DataStatusEinvoiceDto> getFileStatusCsvReports(
			SearchCriteria criteria, int noOfRowsToFetch, int pageNo);

}

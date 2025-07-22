/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Anx1CsvReportsDao {

	List<DataStatusEinvoiceDto> getCsvReports(SearchCriteria criteria);
}

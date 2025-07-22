/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportResponse;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface CEWBDownloadReportDao {

	List<CEWBDownloadReportResponse> getCEWBCsvReports(SearchCriteria criteria);

}

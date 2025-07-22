/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface ItcReportDao {

	List<Object> getItcReports(Gstr1VerticalDownloadReportsReqDto request);

}

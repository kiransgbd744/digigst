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
public interface Gstr2xReportDao {

	List<Object> getGstr2xReports(Gstr1VerticalDownloadReportsReqDto request);

}

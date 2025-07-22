/**
 * 
 */
package com.ey.advisory.app.service.report.gstr7;

import java.util.List;

import com.aspose.cells.Workbook;

/**
 * @author vishal.verma
 *
 */

public interface Gstr7EntityLevelReportDownloadService {

	public Workbook getGstr7ReportData(Long entityId, List<String> 
				gstins, String taxPeriod);

}

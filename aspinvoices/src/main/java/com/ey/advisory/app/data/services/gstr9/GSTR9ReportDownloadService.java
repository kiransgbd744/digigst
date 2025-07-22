/**
 * 
 */
package com.ey.advisory.app.data.services.gstr9;

import com.aspose.cells.Workbook;

/**
 * @author vishal.verma
 *
 */
public interface GSTR9ReportDownloadService {

	public Workbook getData(String gstin, String taxPeriod, String entityId,
			String finYear, Integer fy);

}

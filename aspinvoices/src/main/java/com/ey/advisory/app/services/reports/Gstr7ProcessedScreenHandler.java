/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 */

@Component("Gstr7ProcessedScreenHandler")
public class Gstr7ProcessedScreenHandler {

	@Autowired
	@Qualifier("Gstr7ProcessedScreenServiceImpl")
	private Gstr7ProcessedScreenDownloadService gstr7ProcessedScreenDownloadService;

	public Workbook getGstr1SummTablesReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr7ProcessedScreenDownloadService
				.findProcessedScreenDownload(criteria, null);

	}
}
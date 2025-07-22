package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

public interface Gstr1ReviewSummaryDownloadService {

	public Workbook findGstr1RevSummTablesReports(
			Gstr1ReviwSummReportsReqDto criteria, Object object);

}

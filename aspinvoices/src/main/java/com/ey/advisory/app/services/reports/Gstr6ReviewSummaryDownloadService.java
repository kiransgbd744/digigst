package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Gstr6ReviewSummaryDownloadService {

	public Workbook findGstr6RevSummTablesReports(
			Annexure1SummaryReqDto criteria);

}

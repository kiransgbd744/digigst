package com.ey.advisory.common;

import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

public interface AsyncReportB2BDownloadService {
	
	public void generateB2bReports(Gstr1ReviwSummReportsReqDto criteria, Long id);

}

package com.ey.advisory.app.services.search.docsearch;

import java.util.List;

public interface Gstr2BGenerateReportFileIdWiseService {
	public List<Gstr2BReportDownloadDto> getDownloadData(Long requestId);
}

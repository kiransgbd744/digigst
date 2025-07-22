package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

public interface Gstr7VerticalDao {
	
	List<Object> getGstr7VerticalReports(
			Gstr1VerticalDownloadReportsReqDto request);

}




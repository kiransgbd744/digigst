package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1InwardTotalRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface Anx1InwardTotalReportsDao {
	
	List<Anx1InwardTotalRecordsDto> getInwardTotalReports(
			Anx1FileStatusReportsReqDto request);

}



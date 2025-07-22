package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1ErrorReportsView;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface Anx1ErrorReportsDao {
	
	List<Anx1ErrorReportsView> getErrorReports(
			Anx1FileStatusReportsReqDto request);


}

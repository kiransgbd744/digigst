package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx2InwardErrorRecordsDto;
import com.ey.advisory.core.search.SearchCriteria;

public interface Anx2ErrorReportsDao {
			
		List<Anx2InwardErrorRecordsDto> getErrorReports(
				SearchCriteria criteria);
		
		
	}




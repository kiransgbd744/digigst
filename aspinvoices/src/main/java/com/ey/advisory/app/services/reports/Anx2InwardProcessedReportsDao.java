package com.ey.advisory.app.services.reports;

import java.util.List;
import com.ey.advisory.app.data.views.client.Anx2InwardProcessedRecordsDto;
import com.ey.advisory.core.search.SearchCriteria;

public interface Anx2InwardProcessedReportsDao {
	
		List<Anx2InwardProcessedRecordsDto> getProcessedReports(
				SearchCriteria criteria);
		
		
	}




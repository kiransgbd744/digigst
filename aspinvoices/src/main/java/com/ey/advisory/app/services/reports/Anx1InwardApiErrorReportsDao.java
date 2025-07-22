package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1inwardapiErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Siva.Nandam
 *
 */
public interface Anx1InwardApiErrorReportsDao {

	
	List<Anx1inwardapiErrorRecordsDto> getApiErrorReports(
			SearchCriteria criteria);
	
	
}

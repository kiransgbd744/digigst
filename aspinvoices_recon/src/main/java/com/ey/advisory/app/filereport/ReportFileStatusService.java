package com.ey.advisory.app.filereport;

import java.util.List;

import org.javatuples.Pair;

public interface ReportFileStatusService {
	
	Pair<List<ReportFileStatusReportDto>, Integer> getFileStatusDetails(String userName,
			List<String> reportCateg, String fromDate, String toDate,
			int pageSize, int pageNum, List<String> dataType, Long entityId);

}

package com.ey.advisory.app.dashboard.mergefiles;

import java.util.List;

import org.javatuples.Triplet;

public interface APICallDashboardReportMerger {

	public String mergeReport(
			List<Triplet<String, String, String>> combinations, Long reportId);

}


package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1DataStatusSummaryReportView;
import com.ey.advisory.app.data.views.client.Anx1ErrorReportsView;
import com.ey.advisory.app.data.views.client.Anx1ProcessedRecordsView;
import com.ey.advisory.app.data.views.client.Anx1ProcesssedInfo;
import com.ey.advisory.app.data.views.client.Anx1TotalRecordsView;
import com.ey.advisory.app.docs.dto.ReportSearchReqDto;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public interface Anx1ReportsDao {

	List<Anx1ProcessedRecordsView> getProcessedReports(
			ReportSearchReqDto request);

	List<Anx1ErrorReportsView> getErrorReports(ReportSearchReqDto request);

	List<Anx1TotalRecordsView> getTotRecReports(ReportSearchReqDto request);
	List<Anx1ProcesssedInfo> getInfoReports(ReportSearchReqDto request);
	List<Anx1DataStatusSummaryReportView> getconvertSummmary(ReportSearchReqDto request);
	
	
	}


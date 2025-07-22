package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.views.client.Gstr1ErrorReportView;
import com.ey.advisory.app.data.views.client.Gstr1InformationReportView;
import com.ey.advisory.app.data.views.client.Gstr1ProcessedReportView;
import com.ey.advisory.app.data.views.client.Gstr1TotRecReportView;
import com.ey.advisory.app.docs.dto.ReportSearchReqDto;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public interface Gstr1ReportsDao {

	List<Gstr1ProcessedReportView> getProcessedReports(
			ReportSearchReqDto request);

	List<Gstr1ErrorReportView> getErrorReports(ReportSearchReqDto request);

	List<Gstr1TotRecReportView> getTotRecReports(ReportSearchReqDto request);
	List<Gstr1InformationReportView> getInfoReports(ReportSearchReqDto request);
}

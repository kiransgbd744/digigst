/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;

/**
 * @author Mahesh.Golla
 *
 */
@Component("AuditTrailOutwardSummaryReportHandler")
public class AuditTrailOutwardSummaryReportHandler {

	@Autowired
	@Qualifier("AuditTrailOutwardSummaryReportsServiceImpl")
	private AuditTrailOutwardSummaryReportsServiceImpl reportsServiceImpl;

	public Workbook downloadAudiTrailoutwardSummaryReport(
			AuditTrailReportsReqDto criteria) {

		return reportsServiceImpl.downloadReports(criteria, null);

	}

}
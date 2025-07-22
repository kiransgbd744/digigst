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
@Component("AuditTrailInwardSummaryReportHandler")
public class AuditTrailInwardSummaryReportHandler {

	@Autowired
	@Qualifier("AuditTrailInwardSummaryReportsServiceImpl")
	private AuditTrailInwardSummaryReportsServiceImpl reportsServiceImpl;

	public Workbook downloadAudiTrailnwardSummaryReport(
			AuditTrailReportsReqDto criteria) {

		return reportsServiceImpl.downloadReports(criteria, null);

	}

}
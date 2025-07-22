package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;
/**
 * @author Sujith.Nanga
 *
 */
@Component("AuditTrailOutwardReportHandler")
public class AuditTrailOutwardReportHandler {

	@Autowired
	@Qualifier("AuditTrailOutwardReportServiceImpl")
	private AuditTrailOutwardReportService auditTrailOutwardReportService;

	public Workbook downloadAuditOutward(AuditTrailReportsReqDto criteria) {

		return auditTrailOutwardReportService.findAuditOutwardRecords(criteria,
				null);

	}

}

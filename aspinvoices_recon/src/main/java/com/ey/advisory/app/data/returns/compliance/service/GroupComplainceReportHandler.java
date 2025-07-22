/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("GroupComplainceReportHandler")
public class GroupComplainceReportHandler {

	@Autowired
	@Qualifier("GroupComplainceReportServiceImpl")
	private GroupComplainceReportService groupComplainceReportService;

	public Workbook downloadComplainceReport(
			GroupComplianceHistoryDataRecordsReqDto criteria) {

		return groupComplainceReportService.findComplaince(criteria, null);
	}

}

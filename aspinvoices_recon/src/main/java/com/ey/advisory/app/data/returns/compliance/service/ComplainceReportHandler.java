/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;

/**
 * @author Sujith.Nanga
 *
 */

@Component("ComplainceReportHandler")
public class ComplainceReportHandler {

	@Autowired
	@Qualifier("ComplainceReportServiceImpl")
	private ComplainceReportService complainceReportService;

	public Workbook downloadComplainceReport(
			Gstr2aProcessedDataRecordsReqDto criteria) {

		return complainceReportService.findComplaince(criteria, null);

	}

}

/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Anx1VerticalProcessedInfoReportHandler")
public class Anx1VerticalProcessedInfoReportHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1VerticalProcessedReportHandler.class);

	@Autowired
	@Qualifier("Anx1VerticalProcessedInfoReportsServiceImpl")
	private Anx1VerticalReportsService anx1VerticalReportsService;

	public Workbook downloadVerticalProcessedInfoReport(
			Anx1VerticalDownloadReportsReqDto criteria) {

		return anx1VerticalReportsService.findReports(criteria, null);

	}

}

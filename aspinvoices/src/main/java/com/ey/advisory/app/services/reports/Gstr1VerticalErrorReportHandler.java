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
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Gstr1VerticalErrorReportHandler")
public class Gstr1VerticalErrorReportHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1VerticalErrorReportHandler.class);

	@Autowired
	@Qualifier("Gstr1VerticalErrorReportsServiceImpl")
	private Gstr1VerticalReportsService gstr1VerticalReportsService;

	public Workbook downloadGstr1VerticalErrorReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return gstr1VerticalReportsService.downloadReports(criteria, null);

	}

}
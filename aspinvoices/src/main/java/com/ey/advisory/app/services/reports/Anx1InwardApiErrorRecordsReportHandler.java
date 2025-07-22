package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx1InwardApiErrorRecordsReportHandler")
public class Anx1InwardApiErrorRecordsReportHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1InwardApiErrorRecordsReportHandler.class);

	@Autowired
	@Qualifier("Anx1InwardApiErrorReportsServiceImpl")
	private Anx1InwardApiErrorReportsServiceImpl anx1InwardApiErrorReportsService;

	public Workbook downloadApiInwardErrorRecordsReport(
			Anx1ReportSearchReqDto criteria) {

		return anx1InwardApiErrorReportsService.findApiTotalRec(criteria,
				null);

	}

}





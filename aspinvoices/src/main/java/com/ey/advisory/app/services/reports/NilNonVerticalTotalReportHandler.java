/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
	@Component("NilNonVerticalTotalReportHandler")
	public class NilNonVerticalTotalReportHandler {
		@Autowired
		@Qualifier("NilNonVerticalTotalReportsServiceImpl")
		private NilNonVerticalReportsService nilNonVerticalReportsService;

		public Workbook downloadNilNonVerticalTotalReport(
				Gstr1VerticalDownloadReportsReqDto criteria) {

			return nilNonVerticalReportsService.downloadReports(criteria, null);

		}

	}


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
 * 
 *  @author Balakrishna.S
 * 
 */

	@Component("NilNonVerticalProcessedInfoReportHandler")
	public class NilNonVerticalProcessedInfoReportHandler {
		@Autowired
		@Qualifier("NilNonVerticalProcessedInfoReportsServiceImpl")
		private NilNonVerticalReportsService nilNonVerticalReportsService;

		public Workbook downloadNilNonVerticalProcessedInfoReport(
				Gstr1VerticalDownloadReportsReqDto criteria) {

			return nilNonVerticalReportsService.downloadReports(criteria, null);

		}

	}


/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1NilSectionReportHandler")
	public class Gstr1NilSectionReportHandler {

		@Autowired
		@Qualifier("Gstr1NilSectionReportServiceImpl")
		private Gstr1AspNilRatedReportsService gstr1AspNilRatedReportsService;

		public Workbook downloadRSProcessedReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1AspNilRatedReportsService
					.findNilReport(criteria, null);

		}

	}


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

	@Component("Gstr1GstErrorReportHandler")
	public class Gstr1GstErrorReportHandler {

		@Autowired
		@Qualifier("Gstr1GstErrorReportServiceImpl")
		private Gstr1GstnErrorReportService gstr1GstnErrorReportService;

		public Workbook getGstr1GstSavableReports(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1GstnErrorReportService.findGstnErrorReports(criteria, null);

		}
	}


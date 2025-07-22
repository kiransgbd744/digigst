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

	@Component("Gstr6GstErrorReportHandler")
	public class Gstr6GstErrorReportHandler {

		@Autowired
		@Qualifier("Gstr6GstErrorReportServiceImpl")
		private Gstr6GstnErrorReportService gstr6GstnErrorReportService;

		public Workbook getGstInvSavableReports(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr6GstnErrorReportService.findGstnErrorReports(criteria, null);

		}
	}


package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;


	@Component("Gstr6GstnErrorReportHandler")
	public class Gstr6GstnErrorReportHandler {

		@Autowired
		@Qualifier("Gstr6GstnErrorReportServiceImpl")
		private Gstr6GstnErrorReportService gstr6GstnErrorReportService;

		public Workbook getGstr1InvSavableReports(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr6GstnErrorReportService.findGstnErrorReports(criteria, null);

		}
	}


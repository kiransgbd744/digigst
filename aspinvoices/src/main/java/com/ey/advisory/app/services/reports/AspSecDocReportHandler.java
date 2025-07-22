package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("AspSecDocReportHandler")
	public class AspSecDocReportHandler{

		@Autowired
		@Qualifier("AspSecDocReportServiceImpl")
		private Gstr1ASPInvSavableService gstr1ASPInvSavableService;

		public Workbook downloadGstr1DocReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ASPInvSavableService.findGstr1InvSavableReports(criteria,
					null);

		}

	}

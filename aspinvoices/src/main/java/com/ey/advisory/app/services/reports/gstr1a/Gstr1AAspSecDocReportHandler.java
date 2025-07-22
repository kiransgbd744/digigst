package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr1AAspSecDocReportHandler")
	public class Gstr1AAspSecDocReportHandler{

		@Autowired
		@Qualifier("Gstr1AAspSecDocReportServiceImpl")
		private Gstr1AASPInvSavableService gstr1ASPInvSavableService;

		public Workbook downloadGstr1DocReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ASPInvSavableService.findGstr1InvSavableReports(criteria,
					null);

		}

	}

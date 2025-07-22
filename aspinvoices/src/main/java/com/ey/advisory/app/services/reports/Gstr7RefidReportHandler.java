package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr7RefidReportHandler")
	public class Gstr7RefidReportHandler {

		@Autowired
		@Qualifier("Gstr7RefidServiceImpl")
		private Gstr7GstnErrorService gstr7GstnErrorService;

		public Workbook downloadRefProcessedReport(Gstr1ReviwSummReportsReqDto criteria) {

			return gstr7GstnErrorService.findGstr7GstnSummRecords(criteria, null);

		}

	}


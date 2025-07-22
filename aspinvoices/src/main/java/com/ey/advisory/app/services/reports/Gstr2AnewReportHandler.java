package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

	@Component("Gstr2AnewReportHandler")
	public class Gstr2AnewReportHandler {

		@Autowired
		@Qualifier("Gstr2ANewConsolidatedServiceImpl")
		private Gstr2ANewConsolidatedServiceImpl gstr2ANewConsolidatedServiceImpl;

		public Workbook downloadGet2aNewReport(Gstr2ProcessedRecordsReqDto criteria) {

			return gstr2ANewConsolidatedServiceImpl
					.findError(criteria, null);

		}

	}


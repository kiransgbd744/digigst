package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.Anx2InwardErrorRequestDto;


	
	@Component("Anx2InwardErrorReportHandler")
	public class Anx2InwardErrorReportHandler {

		@Autowired
		@Qualifier("Anx2InwardErrorReportsServiceImpl")
		private Anx2InwardErrorReportsServiceImpl anx2ErrorReportsService;

		public Workbook downloadErrorReport(Anx2InwardErrorRequestDto criteria) {

			return anx2ErrorReportsService.findErrorRec(criteria, null);

		}

	}




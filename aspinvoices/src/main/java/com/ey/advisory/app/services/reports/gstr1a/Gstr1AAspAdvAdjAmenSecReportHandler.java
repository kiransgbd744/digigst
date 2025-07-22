package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr1AAspAdvAdjAmenSecReportHandler")
	public class Gstr1AAspAdvAdjAmenSecReportHandler {

		@Autowired
		@Qualifier("Gstr1AAspAdvAdjAmenSecServiceImpl")
		private Gstr1AASPAdvAdjSavableService gstr1ASPAdvAdjSavableService;

		public Workbook getGstr1AdjSavableReports(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ASPAdvAdjSavableService.
					findGstr1AdjSavableReports(criteria, null);

		}

	}


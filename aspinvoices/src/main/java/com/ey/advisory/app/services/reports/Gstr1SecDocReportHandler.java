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
	@Component("Gstr1SecDocReportHandler")
	public class Gstr1SecDocReportHandler{

		@Autowired
		@Qualifier("Gstr1SecDocReportServiceImpl")
		private Gstr1ASPInvSavableService gstr1ASPInvSavableService;

		public Workbook downloadGstr1DocReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ASPInvSavableService.findGstr1InvSavableReports(criteria,
					null);

		}

	}


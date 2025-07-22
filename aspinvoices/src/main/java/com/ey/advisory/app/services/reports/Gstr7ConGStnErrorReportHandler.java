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

	@Component("Gstr7ConGStnErrorReportHandler")
	public class Gstr7ConGStnErrorReportHandler {

		@Autowired
		@Qualifier("Gstr7ErrorServiceImpl")
		private Gstr7GstnErrorService gstr7GstnErrorService;

		public Workbook downloadConProcessedReport(Gstr1ReviwSummReportsReqDto criteria) {

			return gstr7GstnErrorService.findGstr7GstnSummRecords(criteria, null);

		}

	}



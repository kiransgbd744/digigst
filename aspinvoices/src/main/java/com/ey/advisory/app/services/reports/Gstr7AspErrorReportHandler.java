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

	
	@Component("Gstr7AspErrorReportHandler")
	public class Gstr7AspErrorReportHandler {

		@Autowired
		@Qualifier("Gstr7AspErrorServiceImpl")
		private Gstr7AspErrorReportsService gstr7AspErrorReportsService;

		public Workbook downloadErrorReport(Gstr1ReviwSummReportsReqDto criteria) {

			return gstr7AspErrorReportsService.findGstr7AspSummRecords(criteria, null);

		}

	}


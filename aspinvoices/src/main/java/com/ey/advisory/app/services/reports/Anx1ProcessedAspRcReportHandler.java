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

	@Component("Anx1ProcessedAspRcReportHandler")
	public class Anx1ProcessedAspRcReportHandler {

		@Autowired
		@Qualifier("Anx1ProcessedAspRcServiceImpl")
		private Anx1ASPRCSavableService anx1ASPRCSavableService;

		public Workbook findRCSavableReports(
				Gstr1ReviwSummReportsReqDto criteria) {

			return anx1ASPRCSavableService.findRCSavableReports(criteria,
					null);

		}

	}


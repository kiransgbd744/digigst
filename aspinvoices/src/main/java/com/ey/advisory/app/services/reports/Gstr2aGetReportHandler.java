/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */
	@Slf4j
	@Component("Gstr2aGetReportHandler")
	public class Gstr2aGetReportHandler {

		@Autowired
		@Qualifier("Gstr2aGetReportServiceImpl")
		private Gstr1AGstnErrorReportService gstr1AGstnErrorReportService;

		public Workbook findGstnErrorReports(
				List<GstnConsolidatedReqDto> criteria) {

			return gstr1AGstnErrorReportService.findGstnErrorReports(criteria,
					null);

		}

	}


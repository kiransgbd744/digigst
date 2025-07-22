/**
 * 
 *//*
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1ReviwSummReportsReqDto;

*//**
 * @author Sujith.Nanga
 *
 * 
 *//*

	@Component("ItcReversalOutwardReportHandler")
	public class ItcReversalOutwardReportHandler {

		@Autowired
		@Qualifier("ItcReversalInwardServiceImpl")
		private ItcReversalInwardReportsService itcReversalInwardReportsService;

		public Workbook findOutwardItcReversal(Gstr1ReviwSummReportsReqDto criteria) {

			return itcReversalInwardReportsService.findItcReversal(criteria, null);

		}

	}
*/
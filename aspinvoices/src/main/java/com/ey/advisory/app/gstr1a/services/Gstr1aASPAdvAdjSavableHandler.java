package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ASPAdvAdjSavableService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Gstr1aASPAdvAdjSavableHandler")
public class Gstr1aASPAdvAdjSavableHandler {

	@Autowired
	@Qualifier("Gstr1aASPAdvAdjSavableServiceImpl")
	private Gstr1ASPAdvAdjSavableService gstr1ASPAdvAdjSavableService;

	public Workbook getGstr1AdjSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPAdvAdjSavableService.findGstr1AdjSavableReports(criteria,
				null);

	}

}

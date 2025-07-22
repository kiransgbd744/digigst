package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ASPInvSavableService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Gstr1aASPInvSavableHandler")
public class Gstr1aASPInvSavableHandler {

	@Autowired
	@Qualifier("Gstr1aASPInvSavableServiceImpl")
	private Gstr1ASPInvSavableService gstr1ASPInvSavableService;

	public Workbook getGstr1InvSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPInvSavableService.findGstr1InvSavableReports(criteria,
				null);

	}

}

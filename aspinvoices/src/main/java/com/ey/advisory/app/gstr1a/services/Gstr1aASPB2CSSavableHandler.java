package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ASPB2CSSavableService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;



@Component("Gstr1aASPB2CSSavableHandler")
public class Gstr1aASPB2CSSavableHandler {
	
	@Autowired
	@Qualifier("Gstr1aASPB2CSSavableServiceImpl")
	private Gstr1ASPB2CSSavableService gstr1ASPB2CSSavableService;

	public Workbook getGstr1B2CSSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPB2CSSavableService
				.findGstr1B2CSSavableReports(criteria, null);

	}

}



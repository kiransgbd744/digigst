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

@Component("Gstr1ASPAdvAdjSavableHandler")
public class Gstr1ASPAdvAdjSavableHandler {

	@Autowired
	@Qualifier("Gstr1ASPAdvAdjSavableServiceImpl")
	private Gstr1ASPAdvAdjSavableService gstr1ASPAdvAdjSavableService;

	public Workbook getGstr1AdjSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPAdvAdjSavableService.findGstr1AdjSavableReports(criteria,
				null);

	}

}

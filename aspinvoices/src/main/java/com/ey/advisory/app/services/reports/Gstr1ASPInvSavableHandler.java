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

@Component("Gstr1ASPInvSavableHandler")
public class Gstr1ASPInvSavableHandler {

	@Autowired
	@Qualifier("Gstr1ASPInvSavableServiceImpl")
	private Gstr1ASPInvSavableService gstr1ASPInvSavableService;

	public Workbook getGstr1InvSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPInvSavableService.findGstr1InvSavableReports(criteria,
				null);

	}

}

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

@Component("Gstr1GstnSummaryTablesHandler")
public class Gstr1GstnSummaryTablesHandler {

	@Autowired
	@Qualifier("Gstr1GstnSummaryTablesServiceImpl")
	private Gstr1GstnSummaryTablesService gstr1GstnSummaryTablesService;

	public Workbook getGstr1SummTablesReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1GstnSummaryTablesService.findGstSummaryTables(criteria,
				null);

	}
}

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

@Component("Anx1TransactionalLevelReportHandler")
public class Anx1TransactionalLevelReportHandler {

	@Autowired
	@Qualifier("Anx1TransactionalLevelServiceImpl")
	private Anx1TransacttionalLevelService anx1TransacttionalLevelService;

	public Workbook downloadTransLevelSummary(
			Gstr1ReviwSummReportsReqDto criteria) {

		return anx1TransacttionalLevelService.findTransLevelSummary(criteria,
				null);

	}

}

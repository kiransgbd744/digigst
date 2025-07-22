/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("ITC04EntitylevelHandler")
public class ITC04EntitylevelHandler {

	@Autowired
	@Qualifier("ITC0404EntityLevelReportsServiceImpl")
	private ITC0404EntityLevelReportsServiceImpl itc04EntityLevelReportsService;

	public Workbook downloadEntityLevelSummary(
			Gstr6SummaryRequestDto setDataSecurity) {
		return itc04EntityLevelReportsService.findEntitySummary(setDataSecurity,
				null);

	}

}

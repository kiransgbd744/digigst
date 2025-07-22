package com.ey.advisory.app.data.daos.client.gstr2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr6EntityLevelReportHandler")
public class Gstr6EntityLevelReportHandler {

	@Autowired
	@Qualifier("Gstr6EntityLevelReportsServiceImpl")
	private Gstr6EntityLevelReportsServiceImpl gstr6EntityLevelReportsService;

	public Workbook downloadEntityLevelSummary(
			Gstr6SummaryRequestDto setDataSecurity) {
		return gstr6EntityLevelReportsService.findEntitySummary(setDataSecurity,
				null);

	}

	public Workbook downloadGstr6EntityLevelSummary(
			Gstr6SummaryRequestDto setDataSecurity) throws Exception {
		return gstr6EntityLevelReportsService
				.findGstr6EntitySummary(setDataSecurity, null);
	}

}

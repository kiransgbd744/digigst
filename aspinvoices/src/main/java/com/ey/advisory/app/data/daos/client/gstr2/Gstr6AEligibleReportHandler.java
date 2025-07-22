package com.ey.advisory.app.data.daos.client.gstr2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr6AEligibleReportHandler")
public class Gstr6AEligibleReportHandler {

	@Autowired
	@Qualifier("Gstr6EligibleReportsServiceImpl")
	private Gstr6EligibleReportsServiceImpl gstr6AEligibleReportsService;

	public Workbook downloadEligibleDstbData(
			Annexure1SummaryReqDto setDataSecurity) {
		return gstr6AEligibleReportsService
				.findEligibleDistrbSummary(setDataSecurity, null);
	}

	public Workbook downloadIneligibleDstbData(
			Annexure1SummaryReqDto setDataSecurity) {
		return gstr6AEligibleReportsService.findInEligibleSummary(setDataSecurity,
				null);
	}

	public Workbook downloadEligibleRedstbData(
			Annexure1SummaryReqDto setDataSecurity) {
		return gstr6AEligibleReportsService
				.findReDistrbEligibleSummary(setDataSecurity, null);

	}
	
	public Workbook downloadInEligibleRedstbData(
			Annexure1SummaryReqDto setDataSecurity) {
		return gstr6AEligibleReportsService
				.findReDistrbInEligibleSummary(setDataSecurity, null);

	}
}

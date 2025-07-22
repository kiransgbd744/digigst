package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

/**
 * @author Sasidhar Reddy
 *
 * 
 */
@Component("Gstr1vs3bReviewSummaryReportHandler")
public class Gstr1vs3bReviewSummaryReportHandler {

	@Autowired
	@Qualifier("Gstr1vs3bReviewSummaryServiceImpl")
	private Gstr1vs3bReviewSummaryServiceImpl gstr1vs3bReviewSummaryServiceImpl;

	public Workbook findReviewSummaryData(
			Gstr1VsGstr3bProcessSummaryReqDto criteria) {

		return gstr1vs3bReviewSummaryServiceImpl
				.findReviewSummaryData(criteria);

	}

}

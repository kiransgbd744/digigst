package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

/**
 * @author Siva K
 *
 * 
 */
@Component("Gstr2Avs3bReviewSummaryReportHandler")
public class Gstr2Avs3bReviewSummaryReportHandler {

	@Autowired
	@Qualifier("Gstr2Avs3bReviewSummaryServiceImpl")
	private Gstr2Avs3bReviewSummaryServiceImpl gstr2Avs3bReviewSummaryServiceImpl;

	public Workbook findReviewSummaryData(
			Gstr1VsGstr3bProcessSummaryReqDto criteria) {

		return gstr2Avs3bReviewSummaryServiceImpl
				.findReviewSummaryData(criteria);

	}

}

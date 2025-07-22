package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

/**
 * @author Shashikant.Shukla
 *
 * 
 */
@Component("Gstr2bvs3bReviewSummaryReportHandler")
public class Gstr2bvs3bReviewSummaryReportHandler {

	@Autowired
	@Qualifier("Gstr2bvs3bReviewSummaryServiceImpl")
	private Gstr2bvs3bReviewSummaryServiceImpl gstr2bvs3bReviewSummaryServiceImpl;

	public Workbook findReviewSummaryData(
			Gstr1VsGstr3bProcessSummaryReqDto criteria) {

		return gstr2bvs3bReviewSummaryServiceImpl
				.findReviewSummaryData(criteria);

	}

}

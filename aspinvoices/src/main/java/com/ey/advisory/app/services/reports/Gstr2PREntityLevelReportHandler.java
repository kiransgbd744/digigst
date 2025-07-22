package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr2PREntityLevelReportHandler")
public class Gstr2PREntityLevelReportHandler {

	@Autowired
	@Qualifier("Gstr2PREntityLevelSummaryServiceImpl")
	private Gstr2PREntityLevelSummaryService gstr2PREntityLevelSummaryService;

	public Workbook downloadEntityLevelSummary(
			Gstr2ProcessedRecordsReqDto searchParams) {

		return gstr2PREntityLevelSummaryService
				.findEntityLevelSummary(searchParams, null);

	}
	
}

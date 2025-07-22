/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.ret1.Ret1ProcessedRecordsRequestDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Ret1ProcessedScreenReportHandler")
public class Ret1ProcessedScreenReportHandler {

	@Autowired
	@Qualifier("Ret1ProcessedScreenServiceImpl")
	private Ret1ProcessedScreenService ret1ProcessedScreenService;

	public Workbook findProcessedScreen(Ret1ProcessedRecordsRequestDto criteria)
			throws Exception {

		return ret1ProcessedScreenService.findProcessedScreen(criteria, null);

	}

}

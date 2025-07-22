/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Gstr2AConsolidatedReportHandler")
public class Gstr2AConsolidatedReportHandler {

	@Autowired
	@Qualifier("Gstr2AConsolidatedServiceImpl")
	private Gstr2AConsolidatedServiceImpl gstr2AConsolidatedServiceImpl;

	public Workbook downloadGet2aReport(Gstr2ProcessedRecordsReqDto criteria) {

		return gstr2AConsolidatedServiceImpl
				.findError(criteria, null);

	}

}

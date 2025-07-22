package com.ey.advisory.app.gmr;

import com.aspose.cells.Workbook;

/**
 * 
 * @author Ravindra V S
 *
 */

public interface GMRInwardSummaryDownloadService {

	public Workbook find(GmrInwardSummaryDto dto);

	public GmrInwardEntityAndMonthDto entityAndTaxPeriod(
			GmrInwardSummaryDto criteria);

}

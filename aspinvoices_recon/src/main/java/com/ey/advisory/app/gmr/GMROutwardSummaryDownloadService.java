package com.ey.advisory.app.gmr;

import com.aspose.cells.Workbook;

/**
 * 
 * @author Sakshi.jain
 *
 */

public interface GMROutwardSummaryDownloadService {

	public Workbook find(GmrOutwardSummaryDto dto);

	public GmrOutwardEntityAndMonthDto entityAndTaxPeriod(
			GmrOutwardSummaryDto criteria);

}

package com.ey.advisory.app.inward.einvoice;

import java.util.List;


/**
 * 
 * @author Ravindra V S
 *
 */

public interface EinvoicesDetailedLineItemScreenService {
	public List<EinvoiceDetailedLineItemReportDto> findTableData(
			String irnNum);

}

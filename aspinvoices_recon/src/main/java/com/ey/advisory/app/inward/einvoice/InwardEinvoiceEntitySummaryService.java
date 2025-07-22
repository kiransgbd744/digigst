package com.ey.advisory.app.inward.einvoice;

import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */

public interface InwardEinvoiceEntitySummaryService {
	public List<InwardEinvoiceEntitySummaryResponseDto> findTableData(
			InwardEinvoiceEntitySummaryReqDto criteria);
	
	public List<InwardEinvoiceDetailedInfoResponseDto> findTableDetailedData(
			InwardEinvoiceEntitySummaryReqDto criteria);
	
	public InwardEinvoiceStatusScreenResponseDto findStatusData(
			InwardEinvoiceEntitySummaryReqDto criteria);

}

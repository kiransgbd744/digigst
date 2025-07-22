package com.ey.advisory.app.inward.einvoice;

/**
 * 
 * @author vishal.verma
 *
 */

public interface InwardEinvoiceGstinLevelService {
	public InwardEinvoiceGstinLevelResponseDto getGstinLevelData(
			InwardEinvoiceGstinLevelReqDto criteria);

}

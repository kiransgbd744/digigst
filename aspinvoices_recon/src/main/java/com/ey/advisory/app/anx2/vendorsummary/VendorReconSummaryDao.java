/**
 * 
 */
package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface VendorReconSummaryDao {

	public List<VendorReconSummaryResponseDto> loadReconSummaryDetails(
			VendorReconSummaryReqDto criteria);
	
}

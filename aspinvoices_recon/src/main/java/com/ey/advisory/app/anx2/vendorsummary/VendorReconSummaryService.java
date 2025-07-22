/**
 * 
 */
package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface VendorReconSummaryService {
	
	public List<VendorReconSummaryResponseDto>  
	             getReconSummaryDetails(VendorReconSummaryReqDto criteria);
	
	

}

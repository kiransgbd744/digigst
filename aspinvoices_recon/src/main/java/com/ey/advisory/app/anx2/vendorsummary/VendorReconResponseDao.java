/**
 * 
 */
package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;
import java.util.Map;

/**
 * @author mohit.basak
 *
 */
public interface VendorReconResponseDao {

	public List<VendorReconResponseRepDto> loadReconResponseDetails(
			VendorReconSummaryReqDto criteria);
	
	//public Map<String, String> getVendorNameForVPans(List<String> vPans);
}

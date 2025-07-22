/**
 * 
 */
package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;

/**
 * @author Mohit.Basak
 *
 */
public interface VenderReconResponseService {
	public List<VendorReconResponseRepDto> getReconResponseDetails(
			VendorReconSummaryReqDto reqDto);
}

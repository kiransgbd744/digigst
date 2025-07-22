/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;


/**
 * @author vishal.verma
 *
 */
public interface Gstr2ReconSummaryStatusDao {
	
	public List<Gstr2ReconSummaryStatusDto> findReconSummStatus(Long entityId, 
			String toTaxPeriod2A, String fromTaxPeriod2A,
			String toTaxPeriodPR, String fromTaxPeriodPR, 
			String fromDocDate, String toDocDate, String reconType);


}

/**
 * 
 */
package com.ey.advisory.app.anx2.reconsummary;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface Anx2ReconSummaryDao {
	
    public List<Anx2ReconSummaryDto> findReconSummDetails(
    		ReconSummaryReqDto reqDto, int taxPeriod, String validQuery);
}

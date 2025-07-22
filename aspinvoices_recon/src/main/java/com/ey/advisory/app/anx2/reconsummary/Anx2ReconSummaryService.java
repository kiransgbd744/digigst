/**
 * 
 */
package com.ey.advisory.app.anx2.reconsummary;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface Anx2ReconSummaryService {
	
  public List<Anx2ReconSummaryDto> getReconSummaryDetails(
		  ReconSummaryReqDto reqDto, int taxPeriod);
}

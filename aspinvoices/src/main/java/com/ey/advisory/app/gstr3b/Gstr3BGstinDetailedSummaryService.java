/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BGstinDetailedSummaryService {

	public void saveGstinDashboardUserInputs(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInput, String status);
	
	public void deleteGstinDashboardUserInputs(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInput, String status);

}

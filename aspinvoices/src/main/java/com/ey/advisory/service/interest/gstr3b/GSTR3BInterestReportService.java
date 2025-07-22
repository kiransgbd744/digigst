/**
 * 
 */
package com.ey.advisory.service.interest.gstr3b;

import java.util.List;

/**
 * @author vishal.verma
 *
 */

public interface GSTR3BInterestReportService {

	public List<GSTR3BInterestDto> getGstr3BInterestSummaryReportData(String
					gstin, String respJson);

	public List<GSTR3BInterestDetailsDto> getGstr3BInterestDetailsReportData(String 
				gstin, String respJson);

}

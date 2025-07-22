/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface ReconResultReportTypeDao {
	
	public List<String> findReportTypeForGstins(int taxPeriod
			   ,List<String> gstins);

}

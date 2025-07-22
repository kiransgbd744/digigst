/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface ReconResultReportTypeService {

	public List<String> getReconResultReportNames
					(int taxPeriod,List<String> gstins);
}

package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

/**
 * @author Arun.KA
 *
 */
public interface InitiateReconcileService {
	
	public String initiatReconcile(List<String> gstins, 
			List<String> infoReports, String taxPeriod, Long entityId);

}

package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

/**
 * @author Arun.KA
 *
 */
public interface InitiateReconcileDao {
	
	public String createReconcileData(List<String> gstins,
			List<String> infoReports, String taxPeriod, Long entityId);

}

package com.ey.advisory.app.recon3way;

import java.util.List;

public interface EWB3WayInitiateReconDao {
	
	public String createReconcileData(List<String> gstins, Long entityId, 
			String fromReturnPeriod, String toReturnPeriod, 
			String criteria,String gstr1Type, String eInvType, String gewbType,
			List<String> addReport);

}

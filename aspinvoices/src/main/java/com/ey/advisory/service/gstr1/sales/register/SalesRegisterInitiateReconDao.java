package com.ey.advisory.service.gstr1.sales.register;

import java.util.List;

public interface SalesRegisterInitiateReconDao {
	
	public String createReconcileData(List<String> gstins, Long entityId, 
			String fromReturnPeriod, String toReturnPeriod);

}

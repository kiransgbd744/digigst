package com.ey.advisory.service.hsn.summary;

import java.util.List;

public interface HsnSummaryInitiateReportDao {
	
	public String createReconcileData(List<String> gstins, Long entityId, 
			String fromReturnPeriod, String toReturnPeriod, Long id);

}

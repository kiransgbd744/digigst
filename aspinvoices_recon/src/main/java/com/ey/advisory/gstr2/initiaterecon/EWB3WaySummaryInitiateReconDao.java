package com.ey.advisory.gstr2.initiaterecon;

import java.util.List;

public interface EWB3WaySummaryInitiateReconDao {
	
	List<EWB3WaySummaryInitiateReconLineItemDto> ewb3WayInitiateRecon(EWB3WaySummaryInitiateReconDto request);

}

package com.ey.advisory.app.anx2.reconsummary;

import java.util.List;

public interface Anx2ReconSummaryStatusService {
	
	public List<Anx2ReconSummaryStatusDto> getReconDetailSummaryStatus(int taxPeriod, 
			Long entityId);
	

}

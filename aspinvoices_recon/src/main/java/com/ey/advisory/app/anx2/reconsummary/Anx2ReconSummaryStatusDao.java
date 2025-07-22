package com.ey.advisory.app.anx2.reconsummary;

import java.util.List;

public interface Anx2ReconSummaryStatusDao {
	
	public List<Anx2ReconSummaryStatusDto> findReconSummStatus(Long entityId,
			int taxPeriod);

}

package com.ey.advisory.app.services.ledger;

import java.io.IOException;
import java.util.List;

public interface LiabilityLedgerReportService {
	
	public void getLiabilityLedgerReport(Long id,String fromReturnPeriod,String toReturnPeriod,List<String> activeGstnList) throws IOException;
}

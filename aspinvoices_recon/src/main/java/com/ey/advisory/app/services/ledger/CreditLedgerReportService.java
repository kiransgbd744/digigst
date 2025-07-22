package com.ey.advisory.app.services.ledger;

import java.io.IOException;
import java.util.List;

public interface CreditLedgerReportService {
	
	public void getCreditCashAndCrRevReclaimReport(List<CreditLedgerBulkDetailsDto> reqDto,Long id,String fromDate,String toDate,String reportType) throws IOException;
}

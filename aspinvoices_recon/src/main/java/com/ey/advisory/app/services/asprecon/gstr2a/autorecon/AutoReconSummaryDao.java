package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.time.LocalDate;
import java.util.List;

public interface AutoReconSummaryDao {

	List<Object[]> getAutoReconSummaryData(List<String> recipientGstins,
			boolean isDateRange, Integer fromTaxPeriodPR, Integer toTaxPeriodPR,
			Integer fromTaxPeriod2A, Integer toTaxPeriod2A,
			LocalDate fromReconDate, LocalDate toReconDate, String criteria);

	List<Object[]> getIncrementalDataSummary(List<String> recipientGstins);

	List<Object[]> getAutoReconGstinsData(Long entityId);
	
	Object getAutoReconUpdatedOn(Long entityId);
	
	Object getIncrementalDataUpdatedOn(Long entityId);
}
package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

public interface AutoReconSummaryService {

	AutoReconSummaryTabDto getReconSummaryDetails(List<String> recipientGstins,
			String fromTaxPeriodPR, String toTaxPeriodPR,
			String fromTaxPeriod2A, String toTaxPeriod2A, String fromReconDate,
			String toReconDate, Long entityId, String criteria);

	IncrementalDataSummaryTabDto getIncremenatalDataSummaryDetails(
			List<String> recipientGstins, Long entityId);

	List<Gstr2ReconSummaryStatusDto> getAutoReconGstinDetails(Long entityId);
}

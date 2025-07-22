package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.util.List;

import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

public interface ProcessedRecordsDao {
	public List<ProcessedRecDetForGstinTaxPeriodDto> findProcessedRec(
			RevIntegrationScenarioTriggerDto req, List<String> taxPeriods);
}

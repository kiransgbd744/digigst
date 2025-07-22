package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.util.List;

import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

public interface ProcessRecordsService {
    
	public List<ProcessedRecDetForGstinTaxPeriodDto> getProcessedRecords(
			RevIntegrationScenarioTriggerDto req, List<String> taxPeiods);
	
}

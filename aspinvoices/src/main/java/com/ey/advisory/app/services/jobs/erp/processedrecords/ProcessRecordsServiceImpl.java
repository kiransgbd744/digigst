package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("ProcessRecordsServiceImpl")
public class ProcessRecordsServiceImpl implements ProcessRecordsService {

	@Autowired
	@Qualifier("ProcessedRecordsDaoImpl")
	ProcessedRecordsDao processedRecDao;
	
	@Override
	public List<ProcessedRecDetForGstinTaxPeriodDto> getProcessedRecords(
			RevIntegrationScenarioTriggerDto req, List<String> taxPeriods) {
		
		LOGGER.debug("Calling Processed records DAO Layer for get Data"
				+ " for Processed Records :");
		return processedRecDao.findProcessedRec(req, taxPeriods);
	}

}

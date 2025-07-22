package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

import java.util.List;

import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
/**
 * 
 * @author vishal.verma
 *
 */

public interface VendorMismatchDao {

	 List<VendorMismatchDto> findVendorMismatchRecords(
			 RevIntegrationScenarioTriggerDto req);
	 
	/* List<Pair<VendorMismatchRevRecordsDto, List<Long>>> 
     				convertDocsAsDtosByChunking(List<Object[]> objs);*/
} 
 
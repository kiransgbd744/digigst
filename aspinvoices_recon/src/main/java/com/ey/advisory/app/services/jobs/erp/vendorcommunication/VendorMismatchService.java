package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

import java.util.List;

import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

/**
 * 
 * @author vishal.verma
 *
 */


public interface VendorMismatchService {
	
	public List<VendorMismatchDto> getVendorMismatchRecords(
			RevIntegrationScenarioTriggerDto req);
	
	/*public List<Pair<VendorMismatchRevRecordsDto, List<Long>>> 
               getDocsAsDtosByChunking(List<Object[]> objs);
	*/

} 

package com.ey.advisory.app.services.docs;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

public interface OriginalDocCheckService {
	
	Map<String, List<ProcessingResult>> checkForCrDrOrgInvoices(List<OutwardTransDocument> docs, Boolean isIntegrated, ProcessingContext context);

	Map<String, List<ProcessingResult>> checkForInwardCrDrOrgInvoices(List<InwardTransDocument> docs, ProcessingContext context);

}

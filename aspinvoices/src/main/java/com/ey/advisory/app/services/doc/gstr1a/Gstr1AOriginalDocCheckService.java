package com.ey.advisory.app.services.doc.gstr1a;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

public interface Gstr1AOriginalDocCheckService {

	Map<String, List<ProcessingResult>> checkForCrDrOrgInvoices(
			List<Gstr1AOutwardTransDocument> docs, Boolean isIntegrated,
			ProcessingContext context);

	Map<String, List<ProcessingResult>> checkForInwardCrDrOrgInvoices(
			List<InwardTransDocument> docs, ProcessingContext context);

}

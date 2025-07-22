package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;
import java.util.Map;


/**
 * @author Arun KA
 *
 */

public interface RecipientService {
	
	public List<String> getCgstinsForEntity(Long entityId);
	
	public List<RecipientSummaryFilterDto> getCgstinsForGstins(
			List<String> gstins, String taxPeriod);
	
	public Map<String, String> getCNamesForCPans(List<String> cPans);

}

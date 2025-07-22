package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;
import java.util.Map;

/**
 * @author Arun KA
 *
 */

public interface RecipientDao {
	
	public List<String> getCgstinsForEntity(String sPan);
	
	public List<CgstinSgstinDto> getCgstinsForGstins(List<String> gstin, String taxPeriod);
	
	public Map<String, String> getCNamesForCPans(List<String> cPans);

}

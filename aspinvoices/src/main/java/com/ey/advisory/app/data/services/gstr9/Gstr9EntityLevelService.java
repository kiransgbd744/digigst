package com.ey.advisory.app.data.services.gstr9;

import jakarta.servlet.http.HttpServletRequest;

import com.ey.advisory.core.api.APIResponse;

public interface Gstr9EntityLevelService {

	public APIResponse getGstr9Details(String gstin, String taxPeriod);

	public APIResponse getAutoCalcDetails(String gstin, String taxPeriod);

	String copyGstr9ComputeData(String gstin, String taxPeriod,String fy);
	
	String copyGstr9AutoComputeData(String gstin, String taxPeriod,String fy);

}

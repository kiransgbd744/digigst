package com.ey.advisory.common;

import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.domain.master.GstnAPIGroupConfig;

public interface APIPersistenceManager {
	
	
	public GstnAPIAuthInfo loadAPIAuthInfo(String gstin, String providerName);
	
	public GstnAPIGstinConfig loadAPIGStinConfig(String gstin);
	
	public GstnAPIGroupConfig loadAPIGroupConfig(String groupCode);
	
	public GstnAPIAuthInfo saveAPIAuthInfo(GstnAPIAuthInfo apiAuthInfo);

}

package com.ey.advisory.app.anx1.counterparty;

import java.util.List;
import java.util.Map;

public interface Anx1FetchInfoService {
	
	public Map<String, Anx1FetchInfoDto> getAnx1FetchInfoForGstins(
			List<String> gstins,String taxPeriod);

}

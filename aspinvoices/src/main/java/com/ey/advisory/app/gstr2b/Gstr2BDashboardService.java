package com.ey.advisory.app.gstr2b;

import java.util.List;
import java.util.Map;

public interface Gstr2BDashboardService {

	public List<Gstr2BDashBoardRespDto> getStatusData(List<String> gstins,
			int derivedStartPeriod, int derivedEndPeriod,
			Map<String, String> stateNames,
			Map<String, String> authTokenStatus);

}

package com.ey.advisory.app.services.daos.gstr6a;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ADashBoardRespDto;

public interface Gstr6ADashboardService {

	public List<Gstr6ADashBoardRespDto> getStatusData(List<String> gstins,
			int derivedStartPeriod, int derivedEndPeriod,
			Map<String, String> stateNames,
			Map<String, String> authTokenStatus);

}

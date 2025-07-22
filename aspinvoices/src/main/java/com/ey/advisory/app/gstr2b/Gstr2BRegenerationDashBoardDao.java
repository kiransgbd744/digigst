package com.ey.advisory.app.gstr2b;

import java.util.List;

public interface Gstr2BRegenerationDashBoardDao {

	List<Gstr2BDashBoardErrorDto> getErrorCodeforGetCall(List<String> gstins, 
			int derivedStartPeriod, int derivedEndPeriod);
}

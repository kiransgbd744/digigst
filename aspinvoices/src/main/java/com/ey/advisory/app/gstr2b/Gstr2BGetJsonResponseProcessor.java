package com.ey.advisory.app.gstr2b;

import java.util.List;

public interface Gstr2BGetJsonResponseProcessor {

	void processJsonResponse(String gstin, String taxPeriod, Long invocationId,
			List<Long> reqIds, boolean isAuto);
}

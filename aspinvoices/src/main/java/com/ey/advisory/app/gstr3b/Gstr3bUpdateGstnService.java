package com.ey.advisory.app.gstr3b;

import com.ey.advisory.core.api.APIResponse;

public interface Gstr3bUpdateGstnService {

	public APIResponse getGstnCall(String gstin, String taxPeriod);
}

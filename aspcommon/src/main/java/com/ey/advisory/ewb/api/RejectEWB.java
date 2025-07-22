package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.RejectEwbReqDto;

public interface RejectEWB {
	
	public APIResponse rejectEwb(RejectEwbReqDto req, String gstin);

}

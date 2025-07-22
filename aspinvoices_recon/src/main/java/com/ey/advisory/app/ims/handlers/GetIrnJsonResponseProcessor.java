package com.ey.advisory.app.ims.handlers;


import java.util.List;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;


public interface GetIrnJsonResponseProcessor {

	void processJsonResponse(String gstin, String taxPeriod, Long invocationId,
			List<Long> reqIds, boolean isAuto, String section, Gstr1GetInvoicesReqDto dto);
}

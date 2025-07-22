package com.ey.advisory.app.services.jobs.gstr1A;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1AHSNSummaryDataAtGstn {

	public Long gstr1HSNSummaryDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);
}

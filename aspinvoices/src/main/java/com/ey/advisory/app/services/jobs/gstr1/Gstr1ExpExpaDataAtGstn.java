package com.ey.advisory.app.services.jobs.gstr1;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1ExpExpaDataAtGstn {

	public Long findExpDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);
}

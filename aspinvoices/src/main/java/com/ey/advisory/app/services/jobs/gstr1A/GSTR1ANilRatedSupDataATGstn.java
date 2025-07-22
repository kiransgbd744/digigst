package com.ey.advisory.app.services.jobs.gstr1A;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface GSTR1ANilRatedSupDataATGstn {

	public Long findNilRatedSupDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);
}

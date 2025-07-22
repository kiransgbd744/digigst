package com.ey.advisory.app.services.jobs.gstr1;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1B2csB2csaDataAtGstn {

	public Long findB2CSDataATGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);

}

package com.ey.advisory.app.services.jobs.gstr1A;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1AB2bB2baDataAtGstn {

	public Long findB2bB2baDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);

}

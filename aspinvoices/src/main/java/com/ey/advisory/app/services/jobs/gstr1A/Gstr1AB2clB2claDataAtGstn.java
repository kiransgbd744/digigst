package com.ey.advisory.app.services.jobs.gstr1A;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1AB2clB2claDataAtGstn {

	public Long findB2CLDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);

}

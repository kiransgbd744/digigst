package com.ey.advisory.app.services.jobs.gstr8;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr8TcsTcsaDataAtGstn {

	public Long findTcsTcsaDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);

}

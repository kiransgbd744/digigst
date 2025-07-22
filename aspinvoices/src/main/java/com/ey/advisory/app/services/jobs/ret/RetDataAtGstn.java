package com.ey.advisory.app.services.jobs.ret;

import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface RetDataAtGstn {

	public String findRetDataAtGstn(RetGetInvoicesReqDto dto,
			String groupCode, String jsonReq);
}

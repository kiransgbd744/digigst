package com.ey.advisory.app.services.jobs.gstr2a;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface Gstr2XDataAtGstn {

	public Long findGstr2xDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonRequest);

}

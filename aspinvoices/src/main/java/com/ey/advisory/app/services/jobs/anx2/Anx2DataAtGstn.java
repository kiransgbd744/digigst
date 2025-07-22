package com.ey.advisory.app.services.jobs.anx2;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Anx2DataAtGstn {

	public String findAnx2DataAtGstn(Anx2GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);

}

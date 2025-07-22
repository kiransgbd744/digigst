package com.ey.advisory.app.services.jobs.gstr7;

import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr7DataAtGstn {

	public Long findGstr7DataAtGstn(Gstr7GetInvoicesReqDto dto, String groupCode, String type, String jsonRequest);
}

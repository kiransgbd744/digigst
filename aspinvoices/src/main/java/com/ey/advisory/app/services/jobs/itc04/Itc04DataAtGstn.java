package com.ey.advisory.app.services.jobs.itc04;

import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Itc04DataAtGstn {
	public Long findItc04DataAtGstn(Itc04GetInvoicesReqDto dto, String groupCode, String type, String jsonRequest);

}

package com.ey.advisory.app.services.jobs.gstr6;

import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr6DataAtGstn {

	public Long findGstr6DataAtGstn(Gstr6GetInvoicesReqDto dto, String groupCode, String type, String jsonRequest);
}

package com.ey.advisory.app.services.jobs.itc04;

import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Itc04InvoicesAtGstn {
	public Long findInvFromGstn(Itc04GetInvoicesReqDto dto, String groupCode, String type, Long batchId);

}

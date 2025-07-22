package com.ey.advisory.app.services.jobs.gstr6;

import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr6InvoicesAtGstn {

	public Long findInvFromGstn(Gstr6GetInvoicesReqDto dto, String groupCode, String type, Long batchId);

}

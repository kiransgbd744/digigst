package com.ey.advisory.app.services.jobs.gstr6a;

import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr6aInvoicesAtGstn {

	public Long findInvFromGstn(Gstr6aGetInvoicesReqDto dto, String groupCode, String type, Long batchId);

}

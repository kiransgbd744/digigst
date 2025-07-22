package com.ey.advisory.app.services.jobs.gstr2a;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr2aAmdhistInvoicesAtGstn {
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode, String type, Long batchId);

}

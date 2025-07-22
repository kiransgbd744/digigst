package com.ey.advisory.app.services.jobs.gstr1;

import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr1SummaryDataAtGstn {

	public APIResponse findSummaryDataAtGstn(final Gstr1GetInvoicesReqDto dto, 
			final String groupCode);

	public APIResponse findGstr1ASummaryDataAtGstn(final Gstr1GetInvoicesReqDto dto, 
			final String groupCode);

}

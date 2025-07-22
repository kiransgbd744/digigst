package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1HSNOrSACInvoicesEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AHSNOrSACInvoicesEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1AHSNSummaryDataParser {
	/*
	 * public List<GetGstr1HSNSummaryInvoicesEntity> gstr1HSNSummaryDataParser(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1AHSNOrSACInvoicesEntity> gstr1HSNSummaryDataParser(
			Gstr1GetInvoicesReqDto dto, String apiResp);

}

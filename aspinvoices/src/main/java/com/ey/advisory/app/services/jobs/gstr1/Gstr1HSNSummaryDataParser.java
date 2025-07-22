package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1HSNOrSACInvoicesEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1HSNSummaryDataParser {
	/*
	 * public List<GetGstr1HSNSummaryInvoicesEntity> gstr1HSNSummaryDataParser(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1HSNOrSACInvoicesEntity> gstr1HSNSummaryDataParser(
			Gstr1GetInvoicesReqDto dto, String apiResp);

}

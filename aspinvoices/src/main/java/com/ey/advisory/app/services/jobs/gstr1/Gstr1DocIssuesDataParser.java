package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1DocIssuedEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1DocIssuesDataParser {

	/*
	 * public List<GetGstr1DocIssuedInvoicesEntity> dataParseFromGstr1(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1DocIssuedEntity> dataParseFromGstr1(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}

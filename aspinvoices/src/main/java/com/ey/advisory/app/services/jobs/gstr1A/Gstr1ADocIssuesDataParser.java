package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ADocIssuedEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1ADocIssuesDataParser {

	/*
	 * public List<GetGstr1DocIssuedInvoicesEntity> dataParseFromGstr1(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1ADocIssuedEntity> dataParseFromGstr1(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}

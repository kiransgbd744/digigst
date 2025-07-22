package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1ExpHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1ExpaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1ExpExpaDataParser {

	/*
	 * public List<GetGstr1EXPInvoicesEntity>
	 * parseExpData(Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1ExpHeaderEntity> parseExpData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1ExpaHeaderEntity> parseExpaData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}

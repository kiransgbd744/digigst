package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AExpHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AExpaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1AExpExpaDataParser {

	/*
	 * public List<GetGstr1EXPInvoicesEntity>
	 * parseExpData(Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1AExpHeaderEntity> parseExpData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1AExpaHeaderEntity> parseExpaData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}

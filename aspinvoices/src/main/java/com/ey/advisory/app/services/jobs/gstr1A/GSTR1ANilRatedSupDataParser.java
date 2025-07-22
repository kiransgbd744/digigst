package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ANilRatedEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface GSTR1ANilRatedSupDataParser {

	/*
	 * public List<GetGstr1NilDetailsInvoicesEntity> gstr1NilRatedSupDataParser(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1ANilRatedEntity> gstr1NilRatedSupDataParser(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}

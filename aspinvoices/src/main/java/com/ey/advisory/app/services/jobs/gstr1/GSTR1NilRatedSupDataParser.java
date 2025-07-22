package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1NilRatedEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface GSTR1NilRatedSupDataParser {

	/*
	 * public List<GetGstr1NilDetailsInvoicesEntity> gstr1NilRatedSupDataParser(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1NilRatedEntity> gstr1NilRatedSupDataParser(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}

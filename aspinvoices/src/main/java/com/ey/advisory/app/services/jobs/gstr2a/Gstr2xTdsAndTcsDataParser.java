package com.ey.advisory.app.services.jobs.gstr2a;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr2xTdsAndTcsDataParser {

	public void parseTdsData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type, Long batchId);
}

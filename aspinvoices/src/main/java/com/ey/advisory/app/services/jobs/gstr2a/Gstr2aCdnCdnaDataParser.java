package com.ey.advisory.app.services.jobs.gstr2a;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr2aCdnCdnaDataParser {

	public List<GetGstr2aStagingCdnInvoicesHeaderEntity> parseCdnData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type, Long batchId);
	
	public List<GetGstr2aStagingCdnaInvoicesHeaderEntity> parseCdnaData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type, Long batchId);

}

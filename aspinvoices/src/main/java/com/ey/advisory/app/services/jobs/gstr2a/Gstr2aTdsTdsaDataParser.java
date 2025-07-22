package com.ey.advisory.app.services.jobs.gstr2a;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingTDSADetailsEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingTDSDetailsEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr2aTdsTdsaDataParser {

	public List<GetGstr2aStagingTDSDetailsEntity> parseTdsData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type,Long batchId);
	
	public List<GetGstr2aStagingTDSADetailsEntity> parseTdsaData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type,Long batchId);
}

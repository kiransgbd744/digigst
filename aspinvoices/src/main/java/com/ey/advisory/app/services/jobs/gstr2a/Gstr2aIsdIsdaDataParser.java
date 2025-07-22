package com.ey.advisory.app.services.jobs.gstr2a;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdaInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Santosh.Gururaj
 *
 */

public interface Gstr2aIsdIsdaDataParser {

	public List<GetGstr2aStagingIsdInvoicesHeaderEntity> parseIsdData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type,Long batchId);
	
	public List<GetGstr2aStagingIsdaInvoicesHeaderEntity> parseIsdaData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type,Long batchId);

}

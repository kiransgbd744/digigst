package com.ey.advisory.app.services.jobs.gstr2a;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2baInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */

public interface Gstr2aB2bB2baDataParser {

	public List<GetGstr2aStagingB2bInvoicesHeaderEntity> parseB2bData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

	public List<GetGstr2aStagingB2baInvoicesHeaderEntity> parseB2baData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			long asLong);

}

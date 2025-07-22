package com.ey.advisory.app.services.jobs.gstr2a;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author vishal.verma
 *
 */

public interface Gstr2aEcomEcomaDataParser {

	public List<GetGstr2aStagingEcomInvoicesHeaderEntity> parseEcomData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

	public List<GetGstr2aStagingEcomaInvoicesHeaderEntity> parseEcomaData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			long asLong);

}

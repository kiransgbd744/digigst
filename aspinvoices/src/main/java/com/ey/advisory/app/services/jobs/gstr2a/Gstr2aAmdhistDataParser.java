package com.ey.advisory.app.services.jobs.gstr2a;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingAmdhistHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr2aAmdhistDataParser {

	public List<GetGstr2aStagingAmdhistHeaderEntity> parseAmdData(Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}

package com.ey.advisory.app.services.jobs.anx2;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;

/**
 * 
 * @author Dibyakanta.sahoo
 *
 */

public interface Anx2SummaryDataParser {
	public void parseAnx2SummaryData(
			Anx2GetInvoicesReqDto dto, String apiResp, Long batchId);

}

/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetAnx1SummaryEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Anx1SummaryDataParser {

	public GetAnx1SummaryEntity parseAnx1SummaryData(
			Anx1GetInvoicesReqDto dto, String apiResp, Long batchId);

}

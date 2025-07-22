/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx2ItcSummaryInvoicesEntity;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx2ItcSummaryDataParser {

	public Set<GetAnx2ItcSummaryInvoicesEntity> parseItcSummryData(
			Anx2GetInvoicesReqDto dto, String apiResp, Long batchId);
}

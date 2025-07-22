package com.ey.advisory.app.services.jobs.anx2;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx2B2bInvoicesHeaderEntity;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Anx2B2bDataParser {

	public Set<GetAnx2B2bInvoicesHeaderEntity> parseB2bData(
			Anx2GetInvoicesReqDto dto, String apiResp, Long batchId);
}

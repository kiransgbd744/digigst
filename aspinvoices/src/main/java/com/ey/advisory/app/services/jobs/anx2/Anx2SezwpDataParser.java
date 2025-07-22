package com.ey.advisory.app.services.jobs.anx2;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx2SezwpInvoicesHeaderEntity;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Anx2SezwpDataParser {

	public Set<GetAnx2SezwpInvoicesHeaderEntity> parseSezwpData(
			Anx2GetInvoicesReqDto dto, String apiResp, Long batchId);
}

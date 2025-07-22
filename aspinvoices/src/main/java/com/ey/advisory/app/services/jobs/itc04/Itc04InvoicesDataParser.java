package com.ey.advisory.app.services.jobs.itc04;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Itc04InvoicesEntity;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Itc04InvoicesDataParser {
	public List<Itc04InvoicesEntity> parseItc04GetData(Itc04GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}

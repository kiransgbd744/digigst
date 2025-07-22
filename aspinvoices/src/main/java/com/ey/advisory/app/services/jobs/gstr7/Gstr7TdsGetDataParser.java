package com.ey.advisory.app.services.jobs.gstr7;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr7TdsDetailsEntity;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr7TdsGetDataParser {

	public List<Gstr7TdsDetailsEntity> parseTdsGetData(Gstr7GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}

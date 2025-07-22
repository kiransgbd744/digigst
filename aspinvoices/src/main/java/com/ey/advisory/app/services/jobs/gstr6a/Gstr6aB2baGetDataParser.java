package com.ey.advisory.app.services.jobs.gstr6a;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aB2baHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2baHeaderEntity;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr6aB2baGetDataParser {
	public List<GetGstr6aStagingB2baHeaderEntity> parseB2baGetData(
			Gstr6aGetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}

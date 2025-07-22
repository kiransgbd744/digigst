package com.ey.advisory.app.services.jobs.gstr6a;
/**
 * 
 * @author Anand3.M
 *
 */

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnHeaderEntity;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;

public interface Gstr6aCdnGetDataParser {
	public List<GetGstr6aStagingCdnHeaderEntity> parseCdnGetData(
			Gstr6aGetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}

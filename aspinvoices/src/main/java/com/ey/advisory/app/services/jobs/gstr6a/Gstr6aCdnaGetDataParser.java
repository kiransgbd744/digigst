package com.ey.advisory.app.services.jobs.gstr6a;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aCdnaHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaHeaderEntity;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;

public interface Gstr6aCdnaGetDataParser {
	
	public List<GetGstr6aStagingCdnaHeaderEntity> parseCdnaGetData(
			Gstr6aGetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}

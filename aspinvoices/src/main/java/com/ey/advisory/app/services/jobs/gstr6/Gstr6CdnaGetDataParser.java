package com.ey.advisory.app.services.jobs.gstr6;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6CdnaHeaderEntity;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr6CdnaGetDataParser {
	public List<GetGstr6CdnaHeaderEntity> parseCdnaGetData(Gstr6GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}

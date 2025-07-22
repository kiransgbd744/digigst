package com.ey.advisory.app.services.jobs.gstr6;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6LateFeeEntity;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr6LateFeeDataParser {
	public List<GetGstr6LateFeeEntity> parseLateFeeData(Gstr6GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}

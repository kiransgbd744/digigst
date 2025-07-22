package com.ey.advisory.app.services.daos.prsummary;

import java.util.List;

import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;
import com.ey.advisory.core.dto.Anx2PRSProcessedResponseDto;

public interface Anx2PRSProcessedDataService {
	
	List<Anx2PRSProcessedResponseDto> getAnx2PRSProcessedRecords(
			Anx2PRSProcessedRequestDto criteria);
}

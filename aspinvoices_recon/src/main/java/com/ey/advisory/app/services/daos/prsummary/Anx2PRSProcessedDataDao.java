package com.ey.advisory.app.services.daos.prsummary;

import java.util.List;

import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;
import com.ey.advisory.core.dto.Anx2PRSProcessedResponseDto;

public interface Anx2PRSProcessedDataDao {

	List<Anx2PRSProcessedResponseDto> getAnx2PRSProcessedRecs(
			Anx2PRSProcessedRequestDto dto);
}

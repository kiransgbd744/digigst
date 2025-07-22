package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import com.ey.advisory.app.docs.dto.Anx2GetProcessedRequestDto;
import com.ey.advisory.app.docs.dto.Anx2GetProcessedResponseDto;

public interface AnxGet2aprocessedService {
	
	List<Anx2GetProcessedResponseDto> getAnx2Get2aProcessedRecords(
			Anx2GetProcessedRequestDto criteria);
}

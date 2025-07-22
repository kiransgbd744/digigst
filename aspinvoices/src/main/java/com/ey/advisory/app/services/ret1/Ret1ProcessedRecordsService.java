package com.ey.advisory.app.services.ret1;

import java.util.List;

public interface Ret1ProcessedRecordsService {

	public List<Ret1ProcessedRecordsResponseDto> fetchProcessedRecords(
			Ret1ProcessedRecordsRequestDto processedRecordsRequestDto)
			throws Exception;

	
}

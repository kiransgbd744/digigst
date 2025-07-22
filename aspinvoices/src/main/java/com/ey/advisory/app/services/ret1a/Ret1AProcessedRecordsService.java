package com.ey.advisory.app.services.ret1a;

import java.util.List;

public interface Ret1AProcessedRecordsService {

	public List<Ret1AProcessedRecordsResponseDto> fetchProcessedRecords(
			Ret1AProcessedRecordsRequestDto processedRecordsRequestDto)
			throws Exception;

}
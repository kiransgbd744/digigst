package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.services.ret1a.Ret1AProcessedRecordsRequestDto;
import com.ey.advisory.app.services.ret1a.Ret1AProcessedRecordsResponseDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Ret1AProcessedRecordsDao {
	List<Ret1AProcessedRecordsResponseDto> fetchRet1AProcessedRecords(
			List<String> gstins, Ret1AProcessedRecordsRequestDto dto);

	public void addStateAndAuthTokenData(Ret1AProcessedRecordsResponseDto dto,
			String gstin);

}

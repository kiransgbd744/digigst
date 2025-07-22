package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.services.ret1.Ret1ProcessedRecordsRequestDto;
import com.ey.advisory.app.services.ret1.Ret1ProcessedRecordsResponseDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Ret1ProcessedRecordsDao {
	List<Ret1ProcessedRecordsResponseDto> fetchRet1ProcessedRecords(
			List<String> gstins, Ret1ProcessedRecordsRequestDto dto);
	public void addStateAndAuthTokenData(Ret1ProcessedRecordsResponseDto dto,
			String gstin);

}

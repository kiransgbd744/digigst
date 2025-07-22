package com.ey.advisory.app.data.daos.client;

import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedDataRecordsRespDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Itc04ComplianceDao {
	
	Gstr2aProcessedDataRecordsRespDto FetchRec(
			Gstr2aProcessedDataRecordsReqDto gstr2aProcessedDataRecordsReqDto);
	
	/*List<Gstr2aProcessedDataRecordsRespDto> fetchItc04Records(
			List<String> gstins, Gstr2aProcessedDataRecordsReqDto dto);
	public void addStateAndAuthTokenData(Ret1ProcessedRecordsResponseDto dto,
			String gstin);*/

}




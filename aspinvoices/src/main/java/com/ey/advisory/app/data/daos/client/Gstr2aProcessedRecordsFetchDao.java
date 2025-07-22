package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedRecordsRespDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

public interface Gstr2aProcessedRecordsFetchDao {
	public List<Gstr2aProcessedRecordsRespDto> loadGstr2aProcessedRecords(
			Gstr2AProcessedRecordsReqDto processedRecordsReqDto);

}

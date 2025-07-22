package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx1.Gstr2ProcessedRecordsFinalRespDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

public interface Gstr2ProcessedRecordsFetchDao {
	public List<Gstr2ProcessedRecordsFinalRespDto> loadGstr2ProcessedRecords(
			Gstr2ProcessedRecordsReqDto processedRecordsReqDto);

}

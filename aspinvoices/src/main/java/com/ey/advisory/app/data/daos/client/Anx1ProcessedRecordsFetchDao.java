package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsFinalRespDto;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;

public interface Anx1ProcessedRecordsFetchDao {

	public List<Anx1ProcessedRecordsFinalRespDto> loadAnx1ProcessedRecords(
			Anx1ProcessedRecordsReqDto processedRecordsReqDto, String functionType);
}

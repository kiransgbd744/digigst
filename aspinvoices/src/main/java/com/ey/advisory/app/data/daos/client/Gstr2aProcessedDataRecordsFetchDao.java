package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedDataRecordsRespDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr2aProcessedDataRecordsFetchDao {
	public List<Gstr2aProcessedDataRecordsRespDto> loadGstr2aDataProcessedRecords(
			Gstr2aProcessedDataRecordsReqDto processedRecordsReqDto);

}

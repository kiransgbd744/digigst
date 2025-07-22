package com.ey.advisory.app.services.search.apisummarysearch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.DataStatusApiSummaryResDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.DataStatusApiSummaryReqDto;

@Service("DataStatusApiSummaryService")
public class DataStatusApiSummaryService {

	@Autowired
	@Qualifier("DataStatusApiSummaryDaoImpl")
	private DataStatusApiSummaryDao dataStatusApiSummaryDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<DataStatusApiSummaryResDto> find(
			DataStatusApiSummaryReqDto dataStatusApiRequest) {
		DataStatusApiSummaryReqDto reqDto = processedRecordsCommonSecParam
				.setDataStatusSearchParams(dataStatusApiRequest);
		return dataStatusApiSummaryDao.findDataStatusApiSummary(reqDto);
	}

}

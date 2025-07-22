package com.ey.advisory.app.services.search.apisummarysearch;

import java.util.List;

import com.ey.advisory.app.docs.dto.DataStatusApiSummaryResDto;
import com.ey.advisory.core.dto.DataStatusApiSummaryReqDto;

public interface DataStatusApiSummaryDao {

	public List<DataStatusApiSummaryResDto> findDataStatusApiSummary(
			DataStatusApiSummaryReqDto dataStatusApiRequest);

}

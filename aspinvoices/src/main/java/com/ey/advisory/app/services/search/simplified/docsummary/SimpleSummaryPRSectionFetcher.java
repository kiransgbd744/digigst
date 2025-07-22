package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr2PRSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */

public interface SimpleSummaryPRSectionFetcher {

	public Map<String, List<Gstr2PRSummarySectionDto>> fetch(
			Gstr2ProcessedRecordsReqDto req);
}

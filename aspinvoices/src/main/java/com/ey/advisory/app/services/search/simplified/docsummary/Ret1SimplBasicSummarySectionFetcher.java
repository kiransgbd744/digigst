package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Ret1SimplBasicSummarySectionFetcher {

	public Map<String, List<Ret1SummarySectionDto>> fetch(Annexure1SummaryReqDto req);
}

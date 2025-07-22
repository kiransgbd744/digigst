package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.simplified.TaxSectionPaymentSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Ret1A6SimplBasicSummarySectionFetcher {

	public Map<String, List<TaxSectionPaymentSummaryDto>> fetch(
			Annexure1SummaryReqDto req);

}

package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;

public interface Gstr1BasicSummaryNilSectionFetcher {
	
	public List<Gstr1NilRatedSummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds, 
			int fromTaxPeriod, int toTaxPeriod);


}

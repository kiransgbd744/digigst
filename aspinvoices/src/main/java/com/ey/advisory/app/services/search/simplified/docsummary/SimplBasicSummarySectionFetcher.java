package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
/**
 * 
 * @author Mohana.Dasari
 *
 */
interface SimplBasicSummarySectionFetcher {
	
	/*public List<Annexure1SummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds, 
			int fromTaxPeriod, int toTaxPeriod);*/
	
	public Map<String, List<Annexure1SummarySectionDto>> fetch(Annexure1SummaryReqDto req);
	
	public Map<String, List<Annexure1SummarySectionDto>> fetchb2c(Annexure1SummaryReqDto req);
}

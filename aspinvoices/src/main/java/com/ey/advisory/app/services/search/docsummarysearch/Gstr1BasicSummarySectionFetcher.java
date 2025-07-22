package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;


import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
/**
 * 
 * @author Mahesh.Golla
 *
 */
interface Gstr1BasicSummarySectionFetcher {
	
	public List<Gstr1BasicSummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds, 
			int fromTaxPeriod, int toTaxPeriod);
}

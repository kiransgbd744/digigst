package com.ey.advisory.app.docs.dto.gstr2;

import java.util.List;

public interface Gstr2BasicSummarySectionFetcher {
	
	public List<Gstr2BasicSummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds, 
			int fromTaxPeriod, int toTaxPeriod);


}

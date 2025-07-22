package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
/**
 * 
 * @author Balakrishna.S
 *
 */

public interface Gstr1BasicSummaryDocSectionFetcher {
	
	public List<Gstr1DocIssuedSummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds, 
			int fromTaxPeriod, int toTaxPeriod);

}

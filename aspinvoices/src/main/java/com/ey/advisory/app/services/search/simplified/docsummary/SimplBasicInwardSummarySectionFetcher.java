package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface SimplBasicInwardSummarySectionFetcher {
	
	public Map<String, List<Annexure1SummarySectionDto>> fetch(
			Annexure1SummaryReqDto req);
	

}

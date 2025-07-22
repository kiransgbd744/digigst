/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
public interface SimplBasicEcommSummarySectionFetcher {

	public Map<String, List<Annexure1SummarySectionEcomDto>> fetch(
			Annexure1SummaryReqDto req);
	
}

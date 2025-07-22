/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1AAdvSimpleSummarySectionFetcher {

	public Map<String, List<Gstr1SummarySectionDto>> fetchAT(
			Annexure1SummaryReqDto req);

}

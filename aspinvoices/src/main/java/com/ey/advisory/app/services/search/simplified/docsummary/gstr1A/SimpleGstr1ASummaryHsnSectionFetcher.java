/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
public interface SimpleGstr1ASummaryHsnSectionFetcher {

	public Map<String, List<Gstr1SummaryCDSectionDto>> fetch(
			Annexure1SummaryReqDto req);
}

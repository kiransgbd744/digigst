/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
public interface AdvSimpleSummarySectionFetcher {

	public Map<String, List<Gstr1SummarySectionDto>> fetchAT(
			Annexure1SummaryReqDto req);

	/*public Map<String, List<Gstr1SummarySectionDto>> fetchATA(
			Annexure1SummaryReqDto req);
	
	public Map<String, List<Gstr1SummarySectionDto>> fetchTXPD(
			Annexure1SummaryReqDto req);

	public Map<String, List<Gstr1SummarySectionDto>> fetchTXPDA(
			Annexure1SummaryReqDto req);*/
}

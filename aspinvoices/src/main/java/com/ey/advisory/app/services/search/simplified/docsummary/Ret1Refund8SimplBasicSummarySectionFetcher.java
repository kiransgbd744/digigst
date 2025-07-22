package com.ey.advisory.app.services.search.simplified.docsummary;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.Ret1RefundSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Ret1Refund8SimplBasicSummarySectionFetcher {

	public Map<String, List<Ret1RefundSummarySectionDto>> fetch(
			Annexure1SummaryReqDto req);

}

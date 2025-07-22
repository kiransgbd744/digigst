/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenSezReqRespHandler;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SezSimpleDocSummarySearchService")
public class Gstr1SezSimpleDocSummarySearchService implements SearchService{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SezSimpleDocSummarySearchService.class);

	@Autowired
	@Qualifier("SimpleGstr1SezBasicSummarySectionFetcher")
	SimpleGstr1SezBasicSummarySectionFetcher fetcher;

	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Map<String, List<Gstr1SummarySectionDto>> eySeztSummaries = fetcher
				.fetchSezt(req);
		
		List<Gstr1SummarySectionDto> seztlist = eySeztSummaries.get("SEZWP");
	//	LOGGER.debug("Executing Sez without Tax---->"+seztlist);
		Gstr1BasicSectionSummaryDto seztSummary = new Gstr1BasicSectionSummaryDto();
		seztSummary.setEySummary(seztlist);

	
		List<Gstr1SummarySectionDto> sezwtlist = eySeztSummaries.get("SEZWOP");
	//	LOGGER.debug("Executing Sez with Tax---->"+sezwtlist);
		Gstr1BasicSectionSummaryDto sezwtSummary = new Gstr1BasicSectionSummaryDto();
		sezwtSummary.setEySummary(sezwtlist);

		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		summary.setSezt(seztSummary);
		summary.setSezwt(sezwtSummary);
		List<Gstr1CompleteSummaryDto> list = new ArrayList<>();
		list.add(summary);
		return (SearchResult<R>) new SearchResult<Gstr1CompleteSummaryDto>(
				list);

	
}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

	
}

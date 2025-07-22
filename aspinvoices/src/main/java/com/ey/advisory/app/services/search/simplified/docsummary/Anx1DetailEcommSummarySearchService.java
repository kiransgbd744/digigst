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

import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicEcomSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * @author BalaKrishna S
 *
 */
@Service("Anx1DetailEcommSummarySearchService")
public class Anx1DetailEcommSummarySearchService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1DetailEcommSummarySearchService.class);

	
	@Autowired
	@Qualifier("Anx1BasicDetailEcomSummarySectionFetcher")
	private Anx1BasicDetailEcomSummarySectionFetcher fetcher;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ey.advisory.core.search.SearchService#find(com.ey.advisory.core.
	 * search.SearchCriteria, com.ey.advisory.core.search.PageRequest,
	 * java.lang.Class)
	 */
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Annexure1SummaryDto summary = new Annexure1SummaryDto();

		LOGGER.debug("Executing Eomm Service ");
		Map<String, List<Annexure1SummarySectionEcomDto>> eySummaries = fetcher
				.fetch(req);
		List<Annexure1SummarySectionEcomDto> table4List = eySummaries.get("4");

		Annexure1BasicEcomSummaryDto table4Summary = new Annexure1BasicEcomSummaryDto();
		table4Summary.setEySummary(table4List);

		summary.setTable4(table4Summary);

		List<Annexure1SummaryDto> list = new ArrayList<>();
		list.add(summary);

		return (SearchResult<R>) new SearchResult<Annexure1SummaryDto>(
				list);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ey.advisory.core.search.SearchService#find(com.ey.advisory.core.
	 * search.SearchCriteria, java.lang.Class)
	 */
	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}

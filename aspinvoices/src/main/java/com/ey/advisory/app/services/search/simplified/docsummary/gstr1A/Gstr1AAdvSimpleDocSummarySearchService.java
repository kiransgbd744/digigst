/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

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
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr1AAdvSimpleDocSummarySearchService")
public class Gstr1AAdvSimpleDocSummarySearchService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AAdvSimpleDocSummarySearchService.class);

	@Autowired
	@Qualifier("SimpleGstr1AAdvBasicSummarySectionFetcher")
	SimpleGstr1AAdvBasicSummarySectionFetcher fetcher;

	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Map<String, List<Gstr1SummarySectionDto>> eyATSummaries = fetcher
				.fetchAT(req);

		List<Gstr1SummarySectionDto> atlist = eyATSummaries.get("ADV REC");
		LOGGER.debug("Fetching the AT Data ----->" + atlist);
		Gstr1BasicSectionSummaryDto atSummary = new Gstr1BasicSectionSummaryDto();
		atSummary.setEySummary(atlist);

		/*
		 * Map<String, List<Gstr1SummarySectionDto>> eyATASummaries = fetcher
		 * .fetchATA(req);
		 */
		List<Gstr1SummarySectionDto> atalist = eyATSummaries.get("ADV REC-A");
		LOGGER.debug("Fetching the ATA Data ----->" + atalist);
		Gstr1BasicSectionSummaryDto ataSummary = new Gstr1BasicSectionSummaryDto();
		ataSummary.setEySummary(atalist);

		/*
		 * Map<String, List<Gstr1SummarySectionDto>> eyTXPDSummaries = fetcher
		 * .fetchTXPD(req);
		 */
		List<Gstr1SummarySectionDto> txpdlist = eyATSummaries.get("ADV ADJ");
		LOGGER.debug("Fetching the TXPD Data ----->" + txpdlist);
		Gstr1BasicSectionSummaryDto txpdSummary = new Gstr1BasicSectionSummaryDto();
		txpdSummary.setEySummary(txpdlist);

		/*
		 * Map<String, List<Gstr1SummarySectionDto>> eyTXPDASummaries = fetcher
		 * .fetchTXPDA(req);
		 */
		List<Gstr1SummarySectionDto> txpdalist = eyATSummaries.get("ADV ADJ-A");
		LOGGER.debug("Fetching the TXPDA Data ----->" + txpdalist);
		Gstr1BasicSectionSummaryDto txpdaSummary = new Gstr1BasicSectionSummaryDto();
		txpdaSummary.setEySummary(txpdalist);

		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		summary.setAt(atSummary);
		summary.setAta(ataSummary);
		summary.setTxpd(txpdSummary);
		summary.setTxpda(txpdaSummary);

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
